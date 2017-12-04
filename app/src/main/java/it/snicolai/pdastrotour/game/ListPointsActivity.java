package it.snicolai.pdastrotour.game;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.snicolai.pdastrotour.R;
import it.snicolai.pdastrotour.ReceiverActivity;
import it.snicolai.pdastrotour.at.AtCategory;
import it.snicolai.pdastrotour.at.AtPoint;
import it.snicolai.pdastrotour.utils.AtUtils;
import it.snicolai.pdastrotour.utils.Utils;

public class ListPointsActivity extends ReceiverActivity {
    
    static final String TAG = "ListPointsActivity";
    private ArrayList<HashMap<String, String>> arrayListMapListPoints;
    private List<AtPoint> atPoints;
    
    private RecyclerView recyclerViewPoints;
    private RecyclerView.Adapter adapterPoints;
    private RecyclerView.LayoutManager linearLayoutManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceStatus) {
        Log.d(TAG, "onCreate()");
        setContentView(R.layout.activity_list_points);
        super.onCreate(savedInstanceStatus);
        
        // Recover Widgets
        recyclerViewPoints = (RecyclerView) findViewById(R.id.recyclerViewPoints);
        recyclerViewPoints.setNestedScrollingEnabled(false); // Smooth scrolling!
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewPoints.setLayoutManager(linearLayoutManager);
        
        // Setup Toolbar
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e) {
            Log.e("TAG", e.toString());
        }
        
        loadBackdrop(R.id.backdrop, R.drawable.header_list);
    }
    
    @Override
    protected void onStart() {
        Log.d(TAG, "onStart()");
        super.onStart();
    }
    
    @Override
    protected void onResume() {
        Log.d(TAG, "onResume()");
        super.onResume();
        
        
        // Setup Content
        atPoints = AtUtils.getAllPoints("", "", getApplicationContext());
        Log.d(TAG, "ATPOINTS: " + atPoints.toString());
        arrayListMapListPoints = new ArrayList<HashMap<String, String>>();
        loadArrayListRoute(arrayListMapListPoints, atPoints);
        
        //Set Adapter
        adapterPoints = new ListPointsAdapter(this, arrayListMapListPoints);
        recyclerViewPoints.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerViewPoints.setAdapter(adapterPoints);
        adapterPoints.notifyDataSetChanged();
        
        // Setup Name
        String screenName = getString(R.string.menu_list);
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
    
        // Maybe there is an active category?
        SharedPreferences sharedpreferences = getSharedPreferences(Utils.AtSharedPreference, MODE_PRIVATE);
        Integer categoryIdSelected = sharedpreferences.getInt(Utils.keyCurrentCategoryId, 0); //0 is the default value.
        collapsingToolbar.setTitle(screenName);
        
        NestedScrollView nestedScrollView = (NestedScrollView) findViewById(R.id.nestedScrollViewListPoint);
        CoordinatorLayout.LayoutParams layoutParams = new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT, CoordinatorLayout.LayoutParams.MATCH_PARENT);
        TextView textViewCategoryFilterTitle = (TextView) findViewById(R.id.textViewCategoryFilterTitle);
    
//        MarginLayoutParams nestedLayoutParams = null;
        
        if( categoryIdSelected != 0 ) {
            // Convert PX -> DP
            int marginTop = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                40,
                getResources().getDisplayMetrics()
            );
            
            AtCategory atCategory = AtUtils.getCategory(categoryIdSelected, this);
            layoutParams.setMargins(0, marginTop, 0, 0);
            layoutParams.setBehavior(new AppBarLayout.ScrollingViewBehavior()); // Do i REALLY need to re-define the behaviour? :-|
            nestedScrollView.setLayoutParams(layoutParams);
            
            String newFilterTitle = getString(R.string.list_filter_category) + atCategory.getName();
    
            textViewCategoryFilterTitle.setText(newFilterTitle);
            textViewCategoryFilterTitle.setVisibility(View.VISIBLE);
        } else {
            
            layoutParams.setMargins(0, 0, 0, 0);
            layoutParams.setBehavior(new AppBarLayout.ScrollingViewBehavior()); // Do i REALLY need to re-define the behaviour? :-|
            nestedScrollView.setLayoutParams(layoutParams);
            textViewCategoryFilterTitle.setText("");
            textViewCategoryFilterTitle.setVisibility(View.GONE);
        }
        
        // Constant SnackBar
        final Integer pointId = sharedpreferences.getInt(Utils.keyPointId, 0); //0 is the default value.
        final String snackText = sharedpreferences.getString(Utils.keyCurrentSnackbar, "");
        if (pointId != 0) {
            Log.d(TAG, "sharedpreferences keyPointId:" + pointId);
            localSnackbar = Utils.showSnackbar(findViewById(android.R.id.content), snackText, getString(R.string.snackbar_goto_point),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        localSnackbar.dismiss();
                        Intent intent = new Intent(ListPointsActivity.this, ShowPointActivity.class);
                        Bundle dataBundle = new Bundle();
                        dataBundle.putString("POINT_ID", pointId.toString());
                        intent.putExtras(dataBundle);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }
            );
        } else {
            if(localSnackbar != null) {
                localSnackbar.dismiss();
            }
        }
    }
    
    @Override
    protected void onPause() {
        Log.d(TAG, "onPause()");
        super.onPause();
    }
    
    @Override
    protected void onStop() {
        Log.d(TAG, "onStop()");
        super.onStop();
    }
    
    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
        super.onDestroy();
    }
    
    // Inner activities: just go back, don't show the Menu. A swipe is more than enough!
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        NavUtils.navigateUpFromSameTask(this);
        return true;
    }
//
//    @Override
//    public void onBackPressed() {
//        NavUtils.navigateUpFromSameTask(this);
//    }
    
    public void showPoint(View view) {
        Log.d(TAG, "showPoint()");
        Log.d(TAG, "View id: " + view.getId());
        
        // Save the current position
//        View parentRow = (View) view.getParent();
//        ListView listView = (ListView) parentRow.getParent();
//
//        // Storing the element position
//        ReceiverActivity.reachedPointPos = listView.getPositionForView(parentRow);
        
        TextView textViewPointId = (TextView) view.findViewById(R.id.textViewPointId);
        String pointId = (String) textViewPointId.getText();
        
        Intent intent = new Intent(this, ShowPointActivity.class);
        Bundle dataBundle = new Bundle();
        dataBundle.putString("POINT_ID", pointId);
        dataBundle.putString("RUNNING_TRANSITION", ShowPointActivity.VIEW_NAME_HEADER_IMAGE);
        intent.putExtras(dataBundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, (View) view.findViewById(R.id.imageViewPointImage), ShowPointActivity.VIEW_NAME_HEADER_IMAGE);
        startActivity(intent, options.toBundle());
    }
    
    // Load value about a list of Point in the array of Map
    private void loadArrayListRoute(ArrayList<HashMap<String, String>> arrayListMapListPoints, List<AtPoint> atPoints) {
        HashMap<String, String> mapPoint = null;
        
        for (AtPoint singlePoint : atPoints) {
            mapPoint = new HashMap<>();
            mapPoint.put("pointId", String.valueOf(singlePoint.getIdPoint()));
            mapPoint.put("pointStatus", String.valueOf(singlePoint.getStatus()));
            mapPoint.put("pointName", singlePoint.getName());
            mapPoint.put("pointTitle", String.valueOf(singlePoint.getTitle()));
            mapPoint.put("pointAddress", String.valueOf(singlePoint.getAddress()));
            mapPoint.put("pointImage", String.valueOf(singlePoint.getImage()));
            mapPoint.put("descriptionRoute", String.valueOf(singlePoint.getLon()));
            mapPoint.put("pointStar", String.valueOf(singlePoint.getImageStar()));
            arrayListMapListPoints.add(mapPoint);
        }
    }
}
