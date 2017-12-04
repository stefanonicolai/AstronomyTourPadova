package it.snicolai.pdastrotour.utils;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import it.snicolai.pdastrotour.MainActivity;
import it.snicolai.pdastrotour.R;
import it.snicolai.pdastrotour.at.AtPoint;
import it.snicolai.pdastrotour.game.ShowPointActivity;

// A bound and started service that is promoted to a foreground service when location updates have been requested and all clients unbind.
// When the activity is removed from the foreground, the service promotes itself to a foreground service, and location updates continue.
// When the activity comes back to the foreground, the foreground service stops, and the notification assocaited with that service is removed.
public class LocationService extends Service implements
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener,
    LocationListener {
    
    // Variables
    private static final String TAG = "LocationService";
    
    private static final String PACKAGE_NAME = "it.snicolai.pdastrotour.utils";
    public static final String ACTION_BROADCAST = PACKAGE_NAME + ".broadcast";
    public static final String ACTION_DEBUG = PACKAGE_NAME + ".broadcast_debug";
    public static final String EXTRA_LOCATION = PACKAGE_NAME + ".location";
    public static final String EXTRA_ATPOINT = PACKAGE_NAME + ".atpoint";
    
    public static final String ACTION_RESOLUTION = PACKAGE_NAME + ".resolution";
    public static final String EXTRA_STATUS = PACKAGE_NAME + ".resolution_required";
    
    public static final String ACTION_SNACKBAR_DISMISS = PACKAGE_NAME + ".snackbar_dismiss";
    
    private final IBinder iBinder = new LocalBinder();
    
    // int to handle rea-time change in Location accuracy (in meters)
    private static final int ACCURACY_HIGHEST = 20;
    
    // int to handle rea-time change in radius of tolerance (in meters)
    private static final int TOLERANCE_PERCENTAGE_HIGHEST = 15; // Percentage
    private static final int TOLERANCE_DELTA_NOWIFI = 2;
    
    // The desired interval for location updates. Inexact. Updates may be more or less frequent.
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS_PRECISION_HIGHEST = (5 * 1000); // 5 seconds, in milliseconds
    
    // The fastest rate for active location updates. Updates will never be more frequent than this value.
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS_PRECISION_HIGHEST = (2 * 1000); // 2 second, in milliseconds
    
    // The identifier for the notification displayed for the foreground service.
    private static final int NOTIFICATION_ID = 12345678;
    
    // Used to check whether the bound activity has really gone away and not unbound as part of an orientation change.
    private boolean changingConfiguration = false;
    
    private Handler handler;
    
    private Integer currentNotificationPointId = null;
    private String currentNotificationTitle = null;
    private String currentNotificationText = null;
    
    private NotificationManager notificationManager;
    
    // Entry point to Google Play services.
    private GoogleApiClient googleApiClient;
    
    // Stores if WiFi is connected
    private static boolean wifiConnected = false;
    
    // The BroadcastReceiver used to listen from broadcasts from the service.
    private AtNetworkReceiver AtNetworkReceiver = new AtNetworkReceiver();
    
    // Stores parameters for requests to the FusedLocationProviderApi.
    private LocationRequest locationRequest;
    
    // Stores the types of location services the client is interested in using.
    protected LocationSettingsRequest locationSettingsRequest;
    
    // Stores the current distance from the Goal
    private static double distanceFromGoal = 0;
    
    // Store the current best-radius, based on accuracy and services availables
    private static double toleranceRadius = TOLERANCE_PERCENTAGE_HIGHEST;
    
    // Store the current minimum accuracy needed to
    private static double minimumAccuracy = ACCURACY_HIGHEST;
    
    // Current Location
    private static Location location;
    
    
    public static Location getKnownLocation() {
        return location;
    }
    
    public static double getToleranceRadius() {
        double radius = toleranceRadius;
        
        // In case i don't have WiFi coverage, i need to relax the tolerance by a delta
        if (!wifiConnected) {
            Log.d(TAG, "NOT wifiConnected");
            radius = handleNoWifiTolerance(radius);
        }
        
        return radius;
    }
    
    private void setMinimumAccuracy(double accuracy) {
        Log.d(TAG, "setMinimumAccuracy()");
        
        // In case i don't have WiFi coverage, i need to relax the accuracy
        if (!wifiConnected) {
            accuracy = getToleranceRadius();
        }
        minimumAccuracy = accuracy;
    }
    
    public static double getMinimumAccuracy() {
        return minimumAccuracy;
    }
    
    
    // In case WiFi is off the accuracy of the localization drops. We need to relax the radius of tolerance to allow the Player to reach the goal
    private static double handleNoWifiTolerance(double radius) {
        radius += radius / TOLERANCE_DELTA_NOWIFI;
        
        return radius;
    }
    
    public LocationService() {
    }
    
    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        googleApiClient = new GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build();
        googleApiClient.connect();
        
        createLocationRequest("highest");
        
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        builder.setAlwaysShow(true); // "This app wants to change your device settings for GPS location"
        locationSettingsRequest = builder.build();
        
        HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        
        currentNotificationTitle = getResources().getString(R.string.app_name);
        currentNotificationText = getResources().getString(R.string.notification_desc);
        
        // Handling interaction with WiFi
        wifiConnected = Utils.isWifiConnected(getApplicationContext());
        
        // Listening to Network changes (WiFi mostly)
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.wifi.WIFI_STATUS_CHANGED");
        intentFilter.addAction("android.net.wifi.STATUS_CHANGE");
        
        registerReceiver(AtNetworkReceiver, intentFilter);
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        // If the system kills the service after onStartCommand() returns, do not recreate the service unless there are pending intents to deliver.
        // This is the safest option
        return START_NOT_STICKY;
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.d(TAG, "onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
        changingConfiguration = true;
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "in onBind()");
        // Called when the App comes to the foreground and binds with this service.
        stopForeground(true);
        changingConfiguration = false;
        return iBinder;
    }
    
    @Override
    public void onRebind(Intent intent) {
        Log.d(TAG, "in onRebind()");
        // Called when the App returns to the foreground and binds once again with this service.
        stopForeground(true);
        changingConfiguration = false;
        super.onRebind(intent);
    }
    
    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind");
        
        // Called when the App unbinds from this service.
        // If this method is called due to a configuration change in the App, we do nothing.
        // Otherwise, we make this service a foreground service.
        if (!changingConfiguration && googleApiClient.isConnected()) {
            Log.d(TAG, "Starting foreground service");
            startForeground(NOTIFICATION_ID, getNotification());
        }
        return true; // Ensures onRebind() is called when a client re-binds.
    }
    
    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        handler.removeCallbacksAndMessages(null);
        googleApiClient.disconnect();
        unregisterReceiver(AtNetworkReceiver);
        super.onDestroy();
    }
    
    // Class AtNetworkReceiver: keep the network monitored
    public class AtNetworkReceiver extends BroadcastReceiver {
        
        @Override
        public void onReceive(Context context, Intent intent) {
            
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            
            boolean isConnectedWiFi = networkInfo != null && (ConnectivityManager.TYPE_WIFI == networkInfo.getType()) && networkInfo.isConnectedOrConnecting();
            boolean isConnectedMobile = networkInfo != null && (ConnectivityManager.TYPE_MOBILE == networkInfo.getType()) && networkInfo.isConnectedOrConnecting();
            
            if (isConnectedWiFi) {
                //Log.d(TAG, "WiFi connected " + getResultCode());
                wifiConnected = true;
            } else {
                //Log.d(TAG, "WiFi NOT connected" + getResultCode());
                wifiConnected = false;
            }
        }
    }
    
    // Always checking for location settings
    // Requests location updates from the FusedLocationApi.
    public void requestLocationUpdates() {
        Log.d(TAG, "requestLocationUpdates()");
        LocationServices.SettingsApi.checkLocationSettings(googleApiClient, locationSettingsRequest).setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.d(TAG, "All location settings are satisfied.");
                        startService(new Intent(getApplicationContext(), LocationService.class));
                        // Expliciting Ignoring this warning, handling it just with a Try-Catch
                        try {
                            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, LocationService.this);
                        } catch (SecurityException e) {
                            Log.e(TAG, "SecurityException: " + e);
                        }
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.e(TAG, "RESOLUTION_REQUIRED");
                        // Notify anyone listening for Resolution Broadcasts about the problem to fix.
                        Intent intent = new Intent(ACTION_RESOLUTION);
                        intent.putExtra(EXTRA_STATUS, status);
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.e(TAG, "SETTINGS_CHANGE_UNAVAILABLE");
                        break;
                }
            }
        });
    }
    
    // Removes location updates
    public void removeLocationUpdates() {
        Log.d(TAG, "Removing location updates");
        
        if (googleApiClient != null) {
            try {
                LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, LocationService.this);
                stopSelf();
            } catch (SecurityException e) {
                Log.e(TAG, "Lost location permission. Could not remove updates. " + e);
            }
        }
    }
    
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected()");
        
        // I can start requesting updates.
        // If the user stopped the LocationService then builder.setAlwaysShow(true) fires up
        requestLocationUpdates();
        
        try {
            location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        } catch (SecurityException e) {
            Log.e(TAG, "Lost location permission." + e);
        }
    }
    
    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "onConnectionSuspended()");
    }
    
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed()");
    }
    
    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged(): " + location);
        
        //Store new location in static way
        LocationService.location = location;
        
        // Make sure the data is there before checking!
        SharedPreferences sharedpreferences = getSharedPreferences(Utils.AtSharedPreference, MODE_PRIVATE);
        boolean dbSetupCompleted = sharedpreferences.getBoolean(Utils.keyDbSetupCompleted, false);
        if (dbSetupCompleted) {
            // Attempting to create an AtPoint. If it's not empty, we have a hit!
            // FIXME: we can switch back to "boolean" and use shared preference
            AtPoint atPoint = playerReachedNewLocation(location);
    
            if (Utils.DEBUG_ACTIVE) {
                Intent intentDebug = new Intent(ACTION_DEBUG);
                intentDebug.putExtra(EXTRA_LOCATION, location);
                intentDebug.putExtra(EXTRA_ATPOINT, atPoint);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intentDebug);
        
                Log.d(TAG, "LOCATION: " + location.toString());
            }
    
            if (!atPoint.isEmpty()) {
                // Sending stuff to the other Activities
                Intent intent = new Intent(ACTION_BROADCAST);
                intent.putExtra(EXTRA_LOCATION, location);
                intent.putExtra(EXTRA_ATPOINT, atPoint);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                Log.d(TAG, "onLocationChanged() - Intent broadcasted");
            }
        }
    }
    
    // Handling Player Reach Location
    private AtPoint playerReachedNewLocation(Location location) {
        Log.d(TAG, "playerReachedNewLocation()");
        Log.d(TAG, "Location: " + location.toString());
        
        AtPoint reachedPoint;
        
        // Checking if a point close to the Location exists!
        reachedPoint = AtUtils.playerReachedPoint(location, this);
        
        Log.d(TAG, "reachedPoint:" + reachedPoint.toString());
        
        // Congratulation, you reached the point!
        if ( !reachedPoint.isEmpty()) {
    
            // Sends a vibrate input to the phone
            Utils.phoneVibrate(this);
            
            // Tadaaaa
            Utils.tadaaaaa(this);
            
            // Updating all the local infos
            currentNotificationPointId = reachedPoint.getIdPoint();
        
            // Registering the Shared Pref for the reached point
            final Integer pointId = reachedPoint.getIdPoint();
            String snackText = getResources().getString(R.string.snackbar_text, reachedPoint.getName());
    
            // Saving datas
            SharedPreferences sharedpreferences = getSharedPreferences(Utils.AtSharedPreference, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putInt(Utils.keyPointId, pointId);
            editor.putString(Utils.keyCurrentSnackbar, snackText);
            editor.apply();
            editor.apply();
    
            // I still need to update the object, so i need to do this simple check and can't call the normal function
            currentNotificationTitle = getResources().getString(R.string.notification_title_reached);
            currentNotificationText = getResources().getString(R.string.notification_desc_reached, reachedPoint.getName());
            
            // Update notification content if running as a foreground service.
            if (serviceIsRunningInForeground(this)) {
                notificationManager.notify(NOTIFICATION_ID, getNotification());
            }
            
            // Saving the timestamp: we reached this point!
            reachedPoint = AtUtils.saveReachedStatus(reachedPoint, getApplicationContext());
            
        } else {
            Log.d(TAG, "Reached NOT point: " + location.toString());
            
            // Resetting the current notification if i already reached the point
            SharedPreferences sharedpreferences = getSharedPreferences(Utils.AtSharedPreference, MODE_PRIVATE);
            Integer pointId = sharedpreferences.getInt(Utils.keyPointId, 0); //0 is the default value.
            if(pointId == 0) {
                resetNotification();
                Log.d(TAG, "Notification reset");
            }
        }
        
        return reachedPoint;
    }
    
    // Show the user a notification in case he doesn't have the app on foreground
    private Notification getNotification() {
        Log.d(TAG, "getNotification()");
        
        Integer pointId = currentNotificationPointId;
        Log.d(TAG, "POINT ID PER SERVICE: " + pointId);
        
        Intent intent;
        // The PendingIntent to launch the selected Activity onclick
        PendingIntent pendingIntent;
        Notification notification;
        
        if (pointId != null) {
            Log.d(TAG, "pointID != NULL");
            // I send the user back to the newly reached Point when he opens his app
            intent = new Intent(this, ShowPointActivity.class);
    
            AtPoint atPoint = AtUtils.getPoint(pointId, this);
            Bitmap mainImage = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(atPoint.getImage(), "drawable", getPackageName()));
    
            pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
            
            notification = new NotificationCompat.Builder(this)
                .setContentIntent(pendingIntent)
                .setContentText(currentNotificationText)
                .setContentTitle(currentNotificationTitle)
                .setPriority(Notification.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setSmallIcon(getResources().getIdentifier("ic_star_full", "drawable", getPackageName()))
                .setLargeIcon(mainImage)
                .setTicker(currentNotificationText)
                .setColor(ContextCompat.getColor(this, R.color.colorWhite))
                .setWhen(System.currentTimeMillis()).build();
        } else {
            Log.d(TAG, "pointID WAS NULL");
            intent = new Intent(this, MainActivity.class);
    
            pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
            
            notification = new NotificationCompat.Builder(this)
                .setContentIntent(pendingIntent)
                .setContentText(currentNotificationText)
                .setContentTitle(currentNotificationTitle)
                .setPriority(Notification.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setSmallIcon(getResources().getIdentifier("ic_star_empty", "drawable", getPackageName()))
                .setLargeIcon(null)
                .setTicker(currentNotificationText)
                .setColor(ContextCompat.getColor(this, R.color.colorWhite))
                .setWhen(System.currentTimeMillis()).build();
        }
        
        return notification;
    }
    
    // Sets the location request parameters.
    private void createLocationRequest(String precisionLevel) {
        Log.d(TAG, "createLocationRequest()");
        
        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS_PRECISION_HIGHEST);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS_PRECISION_HIGHEST);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    
    // Class used for the client Binder.  This service runs in the same process as its clients, we don't need to deal with IPC.
    public class LocalBinder extends Binder {
        public LocationService getService() {
            return LocationService.this;
        }
    }
    
    // Returns true if this is a foreground service.
    public boolean serviceIsRunningInForeground(Context context) {
        Log.d(TAG, "serviceIsRunningInForeground()");
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (getClass().getName().equals(service.service.getClassName())) {
                if (service.foreground) {
                    Log.d(TAG, "serviceIsRunningInForeground() - It is");
                    return true;
                }
            }
        }
        Log.d(TAG, "serviceIsRunningInForeground() - It is NOT");
        return false;
    }
    
    public void resetNotification() {
        currentNotificationPointId = null;
        currentNotificationTitle = getResources().getString(R.string.app_name);
        currentNotificationText = getResources().getString(R.string.notification_desc);
    
        // Update notification content if running as a foreground service.
        if (serviceIsRunningInForeground(this)) {
            notificationManager.notify(NOTIFICATION_ID, getNotification());
        }
    
    }
}