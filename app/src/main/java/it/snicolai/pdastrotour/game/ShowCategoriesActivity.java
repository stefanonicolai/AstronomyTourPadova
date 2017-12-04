package it.snicolai.pdastrotour.game;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.snicolai.pdastrotour.R;
import it.snicolai.pdastrotour.ReceiverActivity;
import it.snicolai.pdastrotour.at.AtCategory;
import it.snicolai.pdastrotour.utils.AtUtils;
import it.snicolai.pdastrotour.utils.Utils;

public class ShowCategoriesActivity extends ReceiverActivity {
    
    static final String TAG = "ListCategories";
    private List<AtCategory> atCategories;
    private HashMap<Integer, String> dbCatConverter; // FIXME: Ugly as hell, but hey, time's up :-(
    
    @Override
    protected void onCreate(Bundle savedInstanceStatus) {
        Log.d(TAG, "onCreate()");
        setContentView(R.layout.activity_show_categories);
        super.onCreate(savedInstanceStatus);
    
        // Setup Toolbar
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e) {
            Log.e("TAG", e.toString());
        }
    
        TextView howDoesItWorkRoutesTitleDescription = (TextView) findViewById(R.id.howDoesItWorkRoutesTitleDescription);
        howDoesItWorkRoutesTitleDescription.setText(Html.fromHtml(getString(R.string.categories_how_does_it_work_description)));
        
        // Setup Name
        final String screenName = getString(R.string.menu_categories);
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(screenName);
        
        // Maybe there is an active category?
        SharedPreferences sharedpreferences = getSharedPreferences(Utils.AtSharedPreference, MODE_PRIVATE);
        Integer categoryIdSelected = sharedpreferences.getInt(Utils.keyCurrentCategoryId, 0); //0 is the default value.
    
        NestedScrollView nestedScrollViewListPoint = (NestedScrollView) findViewById(R.id.nestedScrollCategories);
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
            nestedScrollViewListPoint.setLayoutParams(layoutParams);
        
            String newFilterTitle = getString(R.string.list_filter_category) + atCategory.getName();
        
            textViewCategoryFilterTitle.setText(newFilterTitle);
            textViewCategoryFilterTitle.setVisibility(View.VISIBLE);
        } else {
        
            layoutParams.setMargins(0, 0, 0, 0);
            layoutParams.setBehavior(new AppBarLayout.ScrollingViewBehavior()); // Do i REALLY need to re-define the behaviour? :-|
            nestedScrollViewListPoint.setLayoutParams(layoutParams);
            textViewCategoryFilterTitle.setText("");
            textViewCategoryFilterTitle.setVisibility(View.GONE);
        }
    
        dbCatConverter = new HashMap<Integer, String>();
        
        // FIXME: Ugly as hell, but hey, time's up :-(
        atCategories = AtUtils.getAllCategories(getApplicationContext());
        Log.d(TAG, "CATEGORIES: " + atCategories.toString());
        for (final AtCategory singleCategory : atCategories) {
            if(singleCategory != null) {
                dbCatConverter.put(singleCategory.getIdCategory(), singleCategory.getName());
            }
        }
        
        loadBackdrop(R.id.backdrop, R.drawable.header_categories);
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
        
        atCategories = AtUtils.getAllCategories(getApplicationContext());
        Log.d(TAG, "CATEGORIES: " + atCategories.toString());
        
        SharedPreferences sharedpreferences = getSharedPreferences(Utils.AtSharedPreference, MODE_PRIVATE);
        
        // Populating the View
        for(Map.Entry<Integer, String> entry : dbCatConverter.entrySet()) {
            Integer categoryId = entry.getKey();
            String categoryName = entry.getValue();
            
            int rId = getResources().getIdentifier("textViewCategories" + categoryId.toString(), "id", getPackageName());
            TextView currCategory = (TextView) findViewById(rId);
            currCategory.setText(categoryName);
    
            // Maybe it's a selected one?
            Integer categoryIdSelected = sharedpreferences.getInt(Utils.keyCurrentCategoryId, 0); //0 is the default value.
            int rVcId = getResources().getIdentifier("imageViewCategories" + categoryId.toString(), "id", getPackageName());
            ImageView imageViewCategory = (ImageView) findViewById(rVcId);
            
            if( categoryIdSelected == categoryId ) {
                imageViewCategory.setBackground(getResources().getDrawable(R.drawable.layout_selected));
            } else {
                imageViewCategory.setBackground(getResources().getDrawable(R.drawable.layout_not_selected));
            }
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
                        Intent intent = new Intent(ShowCategoriesActivity.this, ShowPointActivity.class);
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
    
    public void onClick(View view) {
    
        SharedPreferences sharedpreferences = getSharedPreferences(Utils.AtSharedPreference, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        ImageView imageViewAll = (ImageView) findViewById(R.id.imageViewCategoriesAll);
        ImageView imageViewCategories4 = (ImageView) findViewById(R.id.imageViewCategories4);
        ImageView imageViewCategories5 = (ImageView) findViewById(R.id.imageViewCategories5);
        ImageView imageViewCategories6 = (ImageView) findViewById(R.id.imageViewCategories6);
        ImageView imageViewCategories7 = (ImageView) findViewById(R.id.imageViewCategories7);
    
        // FIXME: Ugly as hell, but hey, time's up :-(
        switch (view.getId()) {
        
            case R.id.categoriesAll:
                // All Categories
                editor.putInt(Utils.keyCurrentCategoryId, 0);
                editor.apply();
                // Graphic
                imageViewAll.setBackground(getResources().getDrawable(R.drawable.layout_selected));
                imageViewCategories4.setBackground(getResources().getDrawable(R.drawable.layout_not_selected));
                imageViewCategories5.setBackground(getResources().getDrawable(R.drawable.layout_not_selected));
                imageViewCategories6.setBackground(getResources().getDrawable(R.drawable.layout_not_selected));
                imageViewCategories7.setBackground(getResources().getDrawable(R.drawable.layout_not_selected));
                break;
        
            case R.id.categories4:
                // Cat 4
                editor.putInt(Utils.keyCurrentCategoryId, 4);
                editor.apply();
                // Graphic
                imageViewAll.setBackground(getResources().getDrawable(R.drawable.layout_not_selected));
                imageViewCategories4.setBackground(getResources().getDrawable(R.drawable.layout_selected));
                imageViewCategories5.setBackground(getResources().getDrawable(R.drawable.layout_not_selected));
                imageViewCategories6.setBackground(getResources().getDrawable(R.drawable.layout_not_selected));
                imageViewCategories7.setBackground(getResources().getDrawable(R.drawable.layout_not_selected));
                break;
        
            case R.id.categories5:
                // Cat 4
                editor.putInt(Utils.keyCurrentCategoryId, 5);
                editor.apply();
                // Graphic
                imageViewAll.setBackground(getResources().getDrawable(R.drawable.layout_not_selected));
                imageViewCategories4.setBackground(getResources().getDrawable(R.drawable.layout_not_selected));
                imageViewCategories5.setBackground(getResources().getDrawable(R.drawable.layout_selected));
                imageViewCategories6.setBackground(getResources().getDrawable(R.drawable.layout_not_selected));
                imageViewCategories7.setBackground(getResources().getDrawable(R.drawable.layout_not_selected));
                break;
        
            case R.id.categories6:
                // Cat 4
                editor.putInt(Utils.keyCurrentCategoryId, 6);
                editor.apply();
                // Graphic
                imageViewAll.setBackground(getResources().getDrawable(R.drawable.layout_not_selected));
                imageViewCategories4.setBackground(getResources().getDrawable(R.drawable.layout_not_selected));
                imageViewCategories5.setBackground(getResources().getDrawable(R.drawable.layout_not_selected));
                imageViewCategories6.setBackground(getResources().getDrawable(R.drawable.layout_selected));
                imageViewCategories7.setBackground(getResources().getDrawable(R.drawable.layout_not_selected));
                break;
        
            case R.id.categories7:
                // Cat 4
                editor.putInt(Utils.keyCurrentCategoryId, 7);
                editor.apply();
                // Graphic
                imageViewAll.setBackground(getResources().getDrawable(R.drawable.layout_not_selected));
                imageViewCategories4.setBackground(getResources().getDrawable(R.drawable.layout_not_selected));
                imageViewCategories5.setBackground(getResources().getDrawable(R.drawable.layout_not_selected));
                imageViewCategories6.setBackground(getResources().getDrawable(R.drawable.layout_not_selected));
                imageViewCategories7.setBackground(getResources().getDrawable(R.drawable.layout_selected));
                break;
        }
        
        // Send to List
        Intent intentList = new Intent(getApplicationContext(), ListPointsActivity.class);
        startActivity(intentList);
        finish();
    }
}
