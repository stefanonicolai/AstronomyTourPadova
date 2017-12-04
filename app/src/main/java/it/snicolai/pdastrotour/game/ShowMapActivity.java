package it.snicolai.pdastrotour.game;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import it.snicolai.pdastrotour.R;
import it.snicolai.pdastrotour.ReceiverActivity;
import it.snicolai.pdastrotour.game.fragment.GmapFragment;
import it.snicolai.pdastrotour.utils.Utils;

public class ShowMapActivity extends ReceiverActivity {
    
    static final String TAG = "ShowMapActivity";
    
    @Override
    protected void onCreate(Bundle savedInstanceStatus) {
        Log.d(TAG, "SHOWMAP . onCreate()");
        setContentView(R.layout.activity_show_map);
        super.onCreate(savedInstanceStatus);
        
        // Getting the ID from the previous
        Bundle extras = this.getIntent().getExtras();
        
        GmapFragment gmapFragment = new GmapFragment();
        gmapFragment.setArguments(extras);
        
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, gmapFragment).commit();
        
        // Setup Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarSmall);
        setSupportActionBar(toolbar);
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e) {
            Log.e("TAG", e.toString());
        }
//
        // Setup Name
        String screenName = getString(R.string.menu_map);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        if (appBarLayout.getLayoutParams() != null) {
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
            AppBarLayout.Behavior appBarLayoutBehaviour = new AppBarLayout.Behavior();
            appBarLayoutBehaviour.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
                @Override
                public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
                    return false;
                }
            });
            layoutParams.setBehavior(appBarLayoutBehaviour);
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(screenName);
    }
    
    @Override
    protected void onResume() {
        Log.d(TAG, "onResume()");
        super.onResume();
    
        // Constant SnackBar
        SharedPreferences sharedpreferences = getSharedPreferences(Utils.AtSharedPreference, MODE_PRIVATE);
        final Integer pointId = sharedpreferences.getInt(Utils.keyPointId, 0); //0 is the default value.
        final String snackText = sharedpreferences.getString(Utils.keyCurrentSnackbar, "");
        if (pointId != 0) {
            Log.d(TAG, "sharedpreferences keyPointId:" + pointId);
            localSnackbar = Utils.showSnackbar(findViewById(android.R.id.content), snackText, getString(R.string.snackbar_goto_point),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        localSnackbar.dismiss();
                        Intent intent = new Intent(ShowMapActivity.this, ShowPointActivity.class);
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
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }
    
    // Inner activities: just go back, don't show the Menu. A swipe is more than enough!
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        NavUtils.navigateUpFromSameTask(this);
        return true;
    }
}
