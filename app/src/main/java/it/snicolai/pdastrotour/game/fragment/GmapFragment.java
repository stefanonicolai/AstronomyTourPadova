package it.snicolai.pdastrotour.game.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.List;

import it.snicolai.pdastrotour.MainActivity;
import it.snicolai.pdastrotour.R;
import it.snicolai.pdastrotour.at.AtMarker;
import it.snicolai.pdastrotour.at.AtPoint;
import it.snicolai.pdastrotour.data.MySQLiteHelper;
import it.snicolai.pdastrotour.game.ShowPointActivity;
import it.snicolai.pdastrotour.utils.AtUtils;
import it.snicolai.pdastrotour.utils.Utils;

public class GmapFragment extends Fragment implements
    OnMapReadyCallback,
    GoogleMap.OnMapClickListener,
    GoogleMap.OnInfoWindowClickListener,
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener,
    LocationListener {
    
    static final String TAG = "GmapFragment";
    static final LatLng PADOVA = new LatLng(45.407795, 11.8742508);         // Coordinates Palazzo della Ragione (in the city center of Padova)
    
    private Integer pointId = null;
    private LatLng initialCameraLocation;
    private List<AtMarker> currentPoints;
    private AtPoint currentPoint;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    protected LocationSettingsRequest mLocationSettingsRequest;
    
    private GoogleMap mMap;
    
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if(bundle != null) {
            pointId = getArguments().getInt("POINT_ID");
        }
    
        // setup the GeoLocation Service
        setupGeoLocation();
        
        return inflater.inflate(R.layout.fragment_gmaps, container,false);
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        MapFragment fragment = (MapFragment)getChildFragmentManager().findFragmentById(R.id.map);
        fragment.getMapAsync(this);
    
        if (!mGoogleApiClient.isConnected()) {
            Log.d(TAG, "NOT googleApiClient.isConnected() - background");
            mGoogleApiClient.connect();
        }
        startLocationUpdates();
    }
    
    @Override
    public void onResume() {
        Log.d(TAG, "onResume()");
        super.onResume();
        
        // Checking for network connection
//        if (!Utils.isNetworkConnected(getActivity())) {
//            Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_LONG).show();
//
//            // Network isn't connected, we send the Player back to the MainActivity
//            Intent mainActivity = new Intent(getActivity(), MainActivity.class);
//            startActivity(mainActivity);
//            getActivity().finish();
//        }
        
        // Checking for permissions
        if (!Utils.checkPermissions(getActivity())) {
            Log.d(TAG, "onResume checkPermissions");
            Utils.requestPermissions(getActivity(), getActivity().findViewById(android.R.id.content), Utils.REQUEST_PERMISSIONS_REQUEST_CODE, getResources().getString(R.string.shop_map_location_sensor));
        } else {
            Log.d(TAG, "onResume checkPermissions - ELSE");
        }
    }
    
    @Override
    public void onPause() {
        Log.d(TAG, "onPause()");
        super.onPause();
    }
    
    
    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    
        // We want the service to keep checking for locations even if the app is in the background
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
        mGoogleApiClient.disconnect();
    }
    
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady()");
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
    
        currentPoints = createPoints(pointId);
//        if (currentPoints.size() != 0) {
//            LatLng init = currentPoints.get(currentPoints.size() - 1).getCoordinates();     // I take the last point
//            if (init != null)
//                initialCameraLocation = init;             // set the last added point as the initial camera location
//        }
    
        if (initialCameraLocation == null) {
            // set as default initialLocation the centre of Padova city
            initialCameraLocation = PADOVA;
        }
    
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialCameraLocation, 15));        // zoom level = 15 (streets)
        Log.d(TAG, "CAMERA: " + 0);
        
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
    
            @Override
            public void onInfoWindowClick(Marker marker) {
                String pointId = (String) marker.getTag();
    
                if(pointId != null) {
                    Intent intent = new Intent(getActivity(), ShowPointActivity.class);
                    Bundle dataBundle = new Bundle();
                    dataBundle.putString("POINT_ID", pointId);
                    intent.putExtras(dataBundle);
                    startActivity(intent);
                }
            }
        });
    
        try {
            mMap.setMyLocationEnabled(true);
        } catch (SecurityException e) {
            Log.e(TAG, "Lost location permission." + e);
        }
    }
    
    @Override
    public void onLocationChanged(Location location) {
        // We don't track User's location changes, just for now
    }
    
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected(), GooglePlay services connected");
        
        try {
            Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            
            // First we check if we come from a Point Activity
            if( pointId != null ) {
                AtPoint target = AtUtils.getPoint(pointId, getActivity());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(target.getLat(), target.getLon()), 16));
            }
            // Then ..
            else if (lastLocation != null && mMap != null && (currentPoints == null || currentPoints.size() == 0)) {
                initialCameraLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialCameraLocation, 15));
            }
            else {
                moveCameraDefault();
                //Toast.makeText(getApplicationContext(), "Please enable the location sensor, in order to have a better experience", Toast.LENGTH_LONG).show();
            }
            Log.d(TAG, "onConnected() ---> current location: " + lastLocation);
        } catch (SecurityException e) {
            Log.e(TAG, "Lost location permission." + e);
        }
    }
    
    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended(), GooglePlay services connection suspended");
    }
    
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed()");
        Toast.makeText(getActivity(), "onConnectionFailed()", Toast.LENGTH_SHORT).show();
        
        Utils.showSnackbar(getActivity().findViewById(android.R.id.content), "Connection to Google Play Service failed. Please, check your connection or update the service.",
            "Back to the main menu", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Back to MainActivity, we need to "reaload" this page after the update takes place.
                    Intent intentMain = new Intent(getActivity(), MainActivity.class);
                    startActivity(intentMain);
                    getActivity().finish();
                }
            });
    }
    
    @Override
    public void onMapClick(LatLng latLngPoint) {
//        Log.d(TAG, "onMapClick" + latLngPoint.toString());
//        puntoAttuale = latLngPoint;
//        mMap.animateCamera(CameraUpdateFactory.newLatLng(puntoAttuale));
    }
    
    @Override
    public void onInfoWindowClick(Marker marker) {
        Log.d(TAG, "onInfoWindowClick()");
        
        String pointId = (String) marker.getTag();
        
        if(pointId != null) {
            Intent intent = new Intent(getActivity(), ShowPointActivity.class);
            Bundle dataBundle = new Bundle();
            dataBundle.putString("POINT_ID", pointId);
            intent.putExtras(dataBundle);
            startActivity(intent);
        }
    }
    
    // Removes location updates from the FusedLocationApi.
    protected void stopLocationUpdates() {
        Log.d(TAG, "stopLocationUpdates()");
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }
    
    // Requests location updates from the FusedLocationApi.
    protected void startLocationUpdates() {
        Log.d(TAG, "startLocationUpdates()");
        // Always checking for location settings
        LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, mLocationSettingsRequest).setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.d(TAG, "All location settings are satisfied.");
                        // Expliciting Ignoring this warning, handling it just with a Try-Catch
                        try {
                            Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                            if (lastLocation != null) {
                                initialCameraLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                            }
                            Log.d(TAG, "onRequestPermissionsResult() ---> current loc: " + lastLocation);
                        } catch (SecurityException e) {
                            Log.e(TAG, "Lost location permission." + e);
                        }
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.d(TAG, "Location settings are not satisfied. Attempting to upgrade " + "location settings ");
                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result in onActivityResult().
                            status.startResolutionForResult(getActivity(), Utils.REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.d(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        String errorMessage = "Location settings are inadequate, and cannot be fixed here. Fix in Settings.";
                        Log.e(TAG, errorMessage);
                        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
                        //break;
                }
            }
        });
    }
    
    
    private void setupGeoLocation() {
        Log.d(TAG, "setupGeoLocation()");
        
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
            .addApi(LocationServices.API)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .build();
        mGoogleApiClient.connect();
        
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10 * 60 * 1000);        // 10 minutes, in milliseconds
        mLocationRequest.setFastestInterval(60 * 1000);  // 60 second, in milliseconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); // We wanto to center the Player on his actual position
        
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true); // "This app wants to change your device settings for GPS location"
        mLocationSettingsRequest = builder.build();
    }
    
    // Takes the current points from the local DB created before
    public List<AtMarker> createPoints(Integer pointId) {
        
        List<AtMarker> list = new ArrayList<AtMarker>();
        List<AtPoint> points = AtUtils.getAllPoints("", MySQLiteHelper.POINT_STATUS_COMPLETED, getActivity());
        //Log.d(TAG, "createPoints() ---> letti " + points.size() + " punti per CREATE");
        for (int i = 0; i < points.size(); i++) {
            list.add(i, new AtMarker(getActivity(), mMap, points.get(i).getIdPoint(), points.get(i).getName(), points.get(i).getTitle(), points.get(i).getLat(), points.get(i).getLon(), points.get(i).getStatus(), points.get(i).getPos(), pointId));
        }
        //Log.d(TAG, "createPoints() ---> inseriti " + list.size() + " AtMarker nella lista");
        return list;
    }
    
    public void moveCameraDefault() {
        Log.d(TAG, "moveCameraDefault()");
//        if (currentPoints != null && currentPoints.size() != 0) {
//            LatLng lastPoint = currentPoints.get(currentPoints.size() - 1).getCoordinates();
//            if (lastPoint != null) {
//                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastPoint, 15));
//                Log.d(TAG, "moveCameraDefault() ---> moved to " + lastPoint);
//            }
//        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(PADOVA, 15));        // zoom level = 15 (streets)
    }
}