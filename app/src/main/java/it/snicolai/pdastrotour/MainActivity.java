package it.snicolai.pdastrotour;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.List;

import it.snicolai.pdastrotour.at.AtAchievement;
import it.snicolai.pdastrotour.at.AtCategory;
import it.snicolai.pdastrotour.game.ListAchievementsActivity;
import it.snicolai.pdastrotour.game.ListPointsActivity;
import it.snicolai.pdastrotour.game.ShowCategoriesActivity;
import it.snicolai.pdastrotour.game.ShowCreditsActivity;
import it.snicolai.pdastrotour.game.ShowInfosActivity;
import it.snicolai.pdastrotour.game.ShowMapActivity;
import it.snicolai.pdastrotour.game.ShowPointActivity;
import it.snicolai.pdastrotour.utils.AtUtils;
import it.snicolai.pdastrotour.utils.Utils;

public class MainActivity extends ReceiverActivity {
    
    private static final String TAG = "MainActivity";
    
    private AlphaAnimation inAnimation;
    private AlphaAnimation outAnimation;
    private FrameLayout progressBarHolder;
    
    public static class SpinnerAsyncTask extends AsyncTask<Void, Void, Void> {
        
        // Prevent memory leaks!
        private WeakReference<MainActivity> activityReference;
        private boolean canRelease = false;
    
        // Only retain a weak reference to the activity
        public SpinnerAsyncTask(MainActivity context) {
            activityReference = new WeakReference<>(context);
        }
        
        @Override
        protected void onPreExecute() {
            Log.d(TAG, "SPINNER: onPreExecute");
            super.onPreExecute();
            MainActivity activity = activityReference.get();
            if (activity == null) {
                Log.d(TAG, "SPINNER: onPreExecute NULL");
                return;
            }
            if(!canRelease) {
                activity.inAnimation = new AlphaAnimation(0f, 1f);
                activity.inAnimation.setDuration(200);
                activity.progressBarHolder.setAnimation(activity.inAnimation);
                activity.progressBarHolder.setVisibility(View.VISIBLE);
                Log.d(TAG, "SPINNER: onPreExecute VISIBLE");
            }
        }
        
        @Override
        protected void onPostExecute(Void aVoid) {
            //Log.d(TAG, "SPINNER: onPostExecute");
            super.onPostExecute(aVoid);
            MainActivity activity = activityReference.get();
            if (activity == null) {
                //Log.d(TAG, "SPINNER: onPostExecute NULL");
                return;
            }
            if(canRelease) {
                activity.outAnimation = new AlphaAnimation(1f, 0f);
                activity.outAnimation.setDuration(200);
                activity.progressBarHolder.setAnimation(activity.outAnimation);
                activity.progressBarHolder.setVisibility(View.GONE);
                //Log.d(TAG, "SPINNER: onPostExecute GONE");
            }
        }
        
        @Override
        protected Void doInBackground(Void... params) {
            MainActivity activity = activityReference.get();
            if (activity == null) {
                return null;
            }
            
            // FIXME: *REALLY* ugly section as it doesn't consider fails in retreiving datas
            List<AtAchievement> atAchievements = AtUtils.getAllAchievements(activity);
            Log.d(TAG, "SPINNER atAchievements");
            Integer shameCounter = 4;
            for(int i=0; i < shameCounter; i++) {
                if (!Utils.completedAllInserts()) {
                    try {
                        Thread.sleep(4000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                    }
                } else {
                    canRelease = true;
                    break;
                }
                //Log.e(TAG, "SPINNER - round: " + i);
            }
            // FIXME: ugly as *uck, but eh, ye... it's late. At least it doesn't get stuck
            if(!canRelease) {
                Log.e(TAG, "NOT can release, i keep going tho");
                canRelease = true;
            }
            
            if(Utils.completedAllInserts()) {
                SharedPreferences sharedpreferences = activity.getSharedPreferences(Utils.AtSharedPreference, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putBoolean(Utils.keyDbSetupCompleted, true);
                editor.apply();
            }
            
            return null;
        }
    }
    
    // ---------------------------------------------------------------------------------------------
    // OVERRIDE METHODS
    // ---------------------------------------------------------------------------------------------
    
    @Override
    protected void onCreate(Bundle savedInstanceStatus) {
        Log.d(TAG, "onCreate()");
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceStatus);
        
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setHomeAsUpIndicator(R.drawable.ic_menu);
            ab.setDisplayHomeAsUpEnabled(true);
        }
        
        // Setup Name
        final String screenName = getString(R.string.app_name);
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) collapsingToolbar.getLayoutParams();
        params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP); // list other flags here by |
        collapsingToolbar.setLayoutParams(params);
        collapsingToolbar.setTitle(screenName);
        
        loadBackdrop(R.id.backdrop, R.drawable.header_main);
    }
    
    @Override
    protected void onStart() {
        super.onStart();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
        // Need to make sure i have all the datas ready on the first launch!
        SharedPreferences sharedpreferences = getSharedPreferences(Utils.AtSharedPreference, MODE_PRIVATE);
        boolean dbSetupCompleted = sharedpreferences.getBoolean(Utils.keyDbSetupCompleted, false);
        if (!dbSetupCompleted) {
            if (Utils.isNetworkConnected(this)) {
                // Load the data!
                progressBarHolder = (FrameLayout) findViewById(R.id.progressBarHolder);
                ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
                progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.fab_color), android.graphics.PorterDuff.Mode.SRC_ATOP);
                
                new SpinnerAsyncTask(this).execute();
                activateMainIcons(true);
            } else {
                showNoConnectionSnacky();
                activateMainIcons(false);
            }
        } else {
            // Normal onResume()
            Integer categoryIdSelected = sharedpreferences.getInt(Utils.keyCurrentCategoryId, 0); //0 is the default value.
            ImageView imageViewCategory = (ImageView) findViewById(R.id.imageViewShowCategories);
            if (categoryIdSelected != 0) {
                DrawableCompat.setTint(imageViewCategory.getDrawable(), ContextCompat.getColor(this, R.color.fab_color));
                
                AtCategory atCategory = AtUtils.getCategory(categoryIdSelected, this);
                TextView textViewCategory = (TextView) findViewById(R.id.textViewShowCategories);
                textViewCategory.setText(getString(R.string.menu_categories) + ": " + atCategory.getName());
                textViewCategory.setTextColor(ContextCompat.getColor(this, R.color.fab_color));
            } else {
                DrawableCompat.setTint(imageViewCategory.getDrawable(), ContextCompat.getColor(this, R.color.colorPrimary));
                TextView textViewCategory = (TextView) findViewById(R.id.textViewShowCategories);
                textViewCategory.setText(getString(R.string.menu_categories));
                textViewCategory.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
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
                            Intent intent = new Intent(MainActivity.this, ShowPointActivity.class);
                            Bundle dataBundle = new Bundle();
                            dataBundle.putString("POINT_ID", pointId.toString());
                            intent.putExtras(dataBundle);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    }
                );
            } else {
                if (localSnackbar != null) {
                    localSnackbar.dismiss();
                }
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
    
    
    // ---------------------------------------------------------------------------------------------
    // LEFT MENU
    // ---------------------------------------------------------------------------------------------
    
    
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
    
    // Overriding: no FINISH on the MainActivity!!!
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent = null;
        SharedPreferences sharedpreferences = getSharedPreferences(Utils.AtSharedPreference, MODE_PRIVATE);
        boolean dbSetupCompleted = sharedpreferences.getBoolean(Utils.keyDbSetupCompleted, false);
        
        if (!dbSetupCompleted) {
            return false;
        }
        
        switch (item.getItemId()) {
            
            case R.id.nav_home:
//                intent = new Intent(this, MainActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
                break;
            case R.id.nav_list:
                intent = new Intent(this, ListPointsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.nav_map:
                intent = new Intent(getApplicationContext(), ShowMapActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.nav_categories:
                intent = new Intent(this, ShowCategoriesActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.nav_achievements:
                intent = new Intent(this, ListAchievementsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.nav_info_empty:
                intent = new Intent(this, ShowInfosActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.nav_info_credits:
                intent = new Intent(this, ShowCreditsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }
        
        // Close navigation drawer
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    
    // Handling the different UI buttons
    public void onClick(View view) {
        SharedPreferences sharedpreferences = getSharedPreferences(Utils.AtSharedPreference, MODE_PRIVATE);
        boolean dbSetupCompleted = sharedpreferences.getBoolean(Utils.keyDbSetupCompleted, false);
        if (dbSetupCompleted) {
            switch (view.getId()) {
                
                case R.id.buttonShowList:
                    // Show the list of points
                    Intent intentList = new Intent(this, ListPointsActivity.class);
                    startActivity(intentList);
                    break;
                
                case R.id.buttonShowMap:
                    // Show the map with all the points
                    Intent intentMap = new Intent(getApplicationContext(), ShowMapActivity.class);
                    startActivity(intentMap);
                    break;
                
                case R.id.buttonShowAchievements:
                    // Shows Player's Achievements
                    Intent intentAchievements = new Intent(this, ListAchievementsActivity.class);
                    startActivity(intentAchievements);
                    break;
                
                case R.id.buttonShowCategories:
                    // Shows Player's Achievements
                    Intent intentCategories = new Intent(this, ShowCategoriesActivity.class);
                    startActivity(intentCategories);
                    break;
            }
        }
    }
    
    // No connection snackbar
    private void showNoConnectionSnacky() {
        String snackText = getString(R.string.network_error_snack);
        
        localSnackbar = Utils.showSnackbar(findViewById(android.R.id.content), snackText, getString(R.string.network_error_snack_button),
            new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    localSnackbar.dismiss();
                    // Refresh MainActivity
                    finish();
                    startActivity(getIntent());
                }
            }
        );
    }
    
    private void activateMainIcons(boolean enabled) {
        
        Integer color = R.color.colorPrimary;
        if (!enabled) {
            color = R.color.colorSilver;
        }
        
        ImageView imageViewShowList = (ImageView) findViewById(R.id.imageViewShowList);
        ImageView imageViewShowMap = (ImageView) findViewById(R.id.imageViewShowMap);
        ImageView imageViewCategory = (ImageView) findViewById(R.id.imageViewShowCategories);
        ImageView imageViewAchievements = (ImageView) findViewById(R.id.imageViewAchievements);
        TextView textViewShowList = (TextView) findViewById(R.id.textViewShowList);
        TextView textViewShowMap = (TextView) findViewById(R.id.textViewShowMap);
        TextView textViewCategory = (TextView) findViewById(R.id.textViewShowCategories);
        TextView textViewShowAchievements = (TextView) findViewById(R.id.textViewShowAchievements);
        DrawableCompat.setTint(imageViewShowList.getDrawable(), ContextCompat.getColor(this, color));
        DrawableCompat.setTint(imageViewShowMap.getDrawable(), ContextCompat.getColor(this, color));
        DrawableCompat.setTint(imageViewCategory.getDrawable(), ContextCompat.getColor(this, color));
        DrawableCompat.setTint(imageViewAchievements.getDrawable(), ContextCompat.getColor(this, color));
        textViewShowList.setTextColor(ContextCompat.getColor(this, color));
        textViewShowMap.setTextColor(ContextCompat.getColor(this, color));
        textViewCategory.setTextColor(ContextCompat.getColor(this, color));
        textViewShowAchievements.setTextColor(ContextCompat.getColor(this, color));
    }
}
