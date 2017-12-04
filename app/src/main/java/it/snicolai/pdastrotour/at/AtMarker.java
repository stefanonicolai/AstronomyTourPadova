package it.snicolai.pdastrotour.at;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class AtMarker extends AtPoint {
    private static final String TAG = "AtMarker";
    private Marker marker;
    private GoogleMap map;
    private LatLng coordinates;
    private Context activityContext;

    public AtMarker(Context context, GoogleMap Gmap, int id, String name, String title, Double lat, Double lon, String status, int pos, @Nullable Integer pointId){
        super(id, name, title, lat, lon, pos, status);
        activityContext = context;
        map = Gmap;
        coordinates = new LatLng(lat, lon);
        marker = drawOnMap(pointId);
        //marker.showInfoWindow();
        // Log.d(TAG, " new AtMarker: " + pos + ") " + name+" | " + title);
    }

    private Marker drawOnMap(Integer pointId){
        //Bitmap btmp = BitmapFactory.decodeResource(activityContext.getResources(), icons.get(getPos()));
        // Bitmap resizedBitmap = getMarker(pointId, activityContext);
    
        Marker marker = map.addMarker(new MarkerOptions()
                //.title(getPos() + " | " + getName())
                .title(getName())
                .snippet(getTitle())
                .position(coordinates)
                .anchor((float)0.5, (float)0.5)
                .infoWindowAnchor((float)0.5, (float)0.2)
                //.icon(BitmapDescriptorFactory.fromBitmap(btmp)));
//                .icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap)));
                .icon(bitmapDescriptorFromVector(activityContext, getMarker(pointId, activityContext))));
        // Storing the Point ID
        marker.setTag(getIdPoint().toString());
        
        return marker;
    }

    public void showInfoWindow(){
        marker.showInfoWindow();
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

}
