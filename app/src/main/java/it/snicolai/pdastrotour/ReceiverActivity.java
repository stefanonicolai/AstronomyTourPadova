package it.snicolai.pdastrotour;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.text.DateFormat;
import java.util.Date;

import it.snicolai.pdastrotour.at.AtPoint;
import it.snicolai.pdastrotour.game.ListAchievementsActivity;
import it.snicolai.pdastrotour.game.ListPointsActivity;
import it.snicolai.pdastrotour.game.ShowCategoriesActivity;
import it.snicolai.pdastrotour.game.ShowCreditsActivity;
import it.snicolai.pdastrotour.game.ShowInfosActivity;
import it.snicolai.pdastrotour.game.ShowMapActivity;
import it.snicolai.pdastrotour.game.ShowPointActivity;
import it.snicolai.pdastrotour.utils.LocationService;
import it.snicolai.pdastrotour.utils.Utils;

// Receiver for broadcasts sent by {@link LocationService}.
public class ReceiverActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
//public class ReceiverActivity extends ActionBarActivity {
    
    private static final String TAG = "ReceiverActivity";
    
    // Shared DrawerLayout trough every Activity
    protected DrawerLayout mDrawerLayout;
    
    // The BroadcastReceiver used to listen from broadcasts from the service.
    protected AtReceiver atReceiver;
    
    // A reference to the service used to get location updates.
    protected LocationService mService = null;
    
    // Tracks the bound status of the service.
    protected boolean mBound = false;
    
    // Tracks if the location is enabled or not
    protected boolean locationActive = true;
    
    protected Snackbar localSnackbar;
    
    private Integer debugLines = 0;
    
    // ---------------------------------------------------------------------------------------------
    // ACTIVITY OVERRIDE
    // ---------------------------------------------------------------------------------------------
    
    
    @Override
    protected void onCreate(Bundle savedInstanceStatus) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceStatus);
        
        // Service connection
        atReceiver = new AtReceiver();
        
        // Drawer Layout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        
        // Left Menu
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }
        setNavigationViewListner();
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        // Bind to the service.
        bindService(new Intent(this, LocationService.class), mServiceConnection, Context.BIND_AUTO_CREATE);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
        // Checking for network connection
//        if (!Utils.isNetworkConnected(this)) {
//            Toast.makeText(getApplicationContext(), "It looks like your internet connection is off. Please turn it on and try again", Toast.LENGTH_LONG).show();
//        }
        
        if (!Utils.checkPermissions(this)) {
            Log.d(TAG, "onResume checkPermissions");
            Utils.requestPermissions(this, findViewById(android.R.id.content), Utils.REQUEST_PERMISSIONS_REQUEST_CODE, getString(R.string.location_permission_request));
        } else {
            Log.d(TAG, "onResume checkPermissions - ELSE");
        }
        
        Log.d(TAG, "onResume() - registering Receiver");
        
        initializeReceiver(this);
    }
    
    @Override
    protected void onPause() {
        Log.d(TAG, "onPause()");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(atReceiver);
        super.onPause();
    }
    
    @Override
    protected void onStop() {
        Log.d(TAG, "onStop()");
        if (mBound) {
            // Unbind from the service. This signals to the service that this activity is no longer
            // in the foreground
            unbindService(mServiceConnection);
            mBound = false;
        }
        
        super.onStop();
    }
    
    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
        super.onDestroy();
    }
    
    
    // ---------------------------------------------------------------------------------------------
    // Drawer Navigation - Left Menu
    // ---------------------------------------------------------------------------------------------
    
    // Setup Drawer Content
    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
            new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem menuItem) {
                    menuItem.setChecked(true);
                    mDrawerLayout.closeDrawers();
                    return true;
                }
            }
        );
    }
    
    // Setup Left Menu Listener
    protected void setNavigationViewListner() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }
    
    // Parallax effect in child activities
    protected void loadBackdrop(Integer backdropId, Integer imageId) {
        final ImageView imageView = (ImageView) findViewById(R.id.backdrop);
        Glide
            .with(this)
            .load(imageId)
            .centerCrop()
            .into(imageView);
    }
    
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        Intent intent = null;
        String currentActivity = this.getClass().getSimpleName();
        
        //Log.d(TAG, "Left Menu Activity: " + currentActivity);
        
        switch (item.getItemId()) {
            
            case R.id.nav_home:
                // Handled differently inside MainActivity
                intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                break;
            case R.id.nav_list:
                if (!currentActivity.equals("ListPointsActivity")) {
                    intent = new Intent(this, ListPointsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
                break;
            case R.id.nav_map:
                if (!currentActivity.equals("ShowMapActivity")) {
                    intent = new Intent(getApplicationContext(), ShowMapActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
                break;
            case R.id.nav_categories:
                if (!currentActivity.equals("ShowCategoriesActivity")) {
                    intent = new Intent(this, ShowCategoriesActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
                break;
            case R.id.nav_achievements:
                if (!currentActivity.equals("ListAchievementsActivity")) {
                    intent = new Intent(this, ListAchievementsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
                break;
            case R.id.nav_info_empty:
                if (!currentActivity.equals("ShowInfosActivity")) {
                    intent = new Intent(this, ShowInfosActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
                break;
            case R.id.nav_info_credits:
                if (!currentActivity.equals("ShowCreditsActivity")) {
                    intent = new Intent(this, ShowCreditsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
                break;
        }
        
        // Close navigation drawer
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: " + item.getItemId());
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    
    // ---------------------------------------------------------------------------------------------
    // LOCALIZATION SERVICE METHODS
    // ---------------------------------------------------------------------------------------------
    
    // Monitors the status of the connection to the service.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected()");
            LocationService.LocalBinder binder = (LocationService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }
        
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected()");
            mService = null;
            mBound = false;
        }
    };
    
    
    // Receiver for broadcasts sent by {@link LocationService}.
    protected class AtReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "BroadcastReceiver: getting this ACTION: " + intent.getAction());
            
            try {
                switch (intent.getAction()) {
                    
                    case LocationService.ACTION_BROADCAST:
                        
                        final AtPoint atPoint = (AtPoint) intent.getParcelableExtra(LocationService.EXTRA_ATPOINT);
                        
                        if (!atPoint.isEmpty()) {
                            Log.d(TAG, "atPoint: " + atPoint.toString());
//
                            final Integer pointId = atPoint.getIdPoint();
                            String snackText = getResources().getString(R.string.snackbar_text, atPoint.getName());
                            
                            // Saving datas
//                            SharedPreferences sharedpreferences = getSharedPreferences(Utils.AtSharedPreference, MODE_PRIVATE);
//                            SharedPreferences.Editor editor = sharedpreferences.edit();
//                            editor.putInt(Utils.keyPointId, pointId);
//                            editor.putString(Utils.keyCurrentSnackbar, snackText);
//                            editor.apply();
                            //Log.d(TAG, "sharedpreferences keyPointId:" + pointId);
                            localSnackbar = Utils.showSnackbar(findViewById(android.R.id.content),
                                snackText, context.getString(R.string.snackbar_goto_point), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        localSnackbar.dismiss();
                                        Intent intent = new Intent(ReceiverActivity.this, ShowPointActivity.class);
                                        Bundle dataBundle = new Bundle();
                                        dataBundle.putString("POINT_ID", pointId.toString());
                                        intent.putExtras(dataBundle);
                                        startActivity(intent);
                                    }
                                });
                            //playerReachedNewLocation();
                        }
                        break;
                    
                    case LocationService.ACTION_DEBUG:
                        
                        TextView serviceDebug = (TextView) findViewById(R.id.serviceDebug);
                        
                        if (Utils.DEBUG_ACTIVE && Utils.DEBUG_SCROLLBAR_ACTIVE && serviceDebug != null) {
                            
                            ScrollView scrollViewDebug = (ScrollView) findViewById(R.id.scrollViewDebug);
                            scrollViewDebug.setVisibility(View.VISIBLE);
                            
                            String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                            
                            final Location location = (Location) intent.getParcelableExtra(LocationService.EXTRA_LOCATION);
                            
                            if (debugLines < 30) {
                                debugLines++;
                                serviceDebug.append("\n" + "\n" +
                                    location.getLatitude() + ", " + location.getLongitude() + " - acc: " + location.getAccuracy() + " - prov: " + location.getProvider() + "\n" +
                                    "Udpated: " + currentDateTimeString);
                            } else {
                                debugLines = 0;
                                serviceDebug.setText(
                                    location.getLatitude() + ", " + location.getLongitude() + " - acc: " + location.getAccuracy() + " - prov: " + location.getProvider() + "\n" +
                                        "Udpated: " + currentDateTimeString
                                );
                            }
                        }
                        //Log.d(TAG, "CIAONE  RICEVUTO!");
                        
                        break;
                    
                    case LocationService.ACTION_RESOLUTION:
                        Status status = intent.getParcelableExtra(LocationService.EXTRA_STATUS);
                        switch (status.getStatusCode()) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the result in onActivityResult().
                                    status.startResolutionForResult(ReceiverActivity.this, Utils.REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException e) {
                                    Log.e(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = getString(R.string.location_settings_problem);
                                Log.e(TAG, errorMessage);
                                Toast.makeText(ReceiverActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                                break;
                        }
                        break;
                    
                    case LocationService.ACTION_SNACKBAR_DISMISS:
                        if (localSnackbar != null) {
                            localSnackbar.dismiss();
                        }
                        break;
                }
            } catch (NullPointerException e) {
                Log.e(TAG, e.toString());
            }
        }
    }
    
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult()");
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == Utils.REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "PERMISSION_NOT_GRANTED");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "PERMISSION_GRANTED");
                
                locationActive = true;
                Log.d(TAG, "requestPermissions() - locationActive = true");
                mService.requestLocationUpdates();
                
            } else {
                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless.
                Utils.showSnackbar(findViewById(android.R.id.content), "Location permissions are needed to play the game. Please, enable them!", "Settings", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Build intent that displays the App settings screen.
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
                        intent.setData(uri);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        // No finish() here !
                    }
                });
            }
        }
    }
    
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult()");
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case Utils.REQUEST_CHECK_SETTINGS:
                
                // Stopping the Receiver & the Service itself
                // Need this to handle real-time changes in the Location permissions
                LocalBroadcastManager.getInstance(this).unregisterReceiver(atReceiver);
                
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i(TAG, "RESULT_OK");
                        
                        // Need to manually rebind the Listner otherwise i won't get any location update
                        if (mBound) {
                            unbindService(mServiceConnection);
                            mBound = false;
                        }
                        if (mService != null) {
                            mService.removeLocationUpdates();
                        }
                        
                        // Need to re-enable the Service and the Receiver i just stopped
                        atReceiver = new AtReceiver();
                        bindService(new Intent(this, LocationService.class), mServiceConnection, Context.BIND_AUTO_CREATE);
                        if (!Utils.checkPermissions(this)) {
                            Log.d(TAG, "onResume checkPermissions");
                            Utils.requestPermissions(this, findViewById(android.R.id.content), Utils.REQUEST_PERMISSIONS_REQUEST_CODE, getString(R.string.location_permission_request));
                        }
                        
                        // Restarting the Receiver
                        initializeReceiver(this);
                        
                        // Keep the user int he loop
                        Toast.makeText(this, getString(R.string.google_location_service_ok), Toast.LENGTH_LONG).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i(TAG, "RESULT_CANCELED");
                        
                        Toast.makeText(this, getString(R.string.google_location_service_ko), Toast.LENGTH_LONG).show();
                        finish();
                        break;
                }
                break;
        }
    }
    
    
    public void initializeReceiver(Context context) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LocationService.ACTION_BROADCAST);
        intentFilter.addAction(LocationService.ACTION_DEBUG);
        intentFilter.addAction(LocationService.ACTION_RESOLUTION);
        intentFilter.addAction(LocationService.ACTION_SNACKBAR_DISMISS);
        LocalBroadcastManager.getInstance(context).registerReceiver(atReceiver, intentFilter);
    }
}
