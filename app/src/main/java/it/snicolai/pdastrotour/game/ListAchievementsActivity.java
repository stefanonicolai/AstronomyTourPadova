package it.snicolai.pdastrotour.game;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.snicolai.pdastrotour.R;
import it.snicolai.pdastrotour.ReceiverActivity;
import it.snicolai.pdastrotour.at.AtAchievement;
import it.snicolai.pdastrotour.utils.AtUtils;
import it.snicolai.pdastrotour.utils.Utils;

public class ListAchievementsActivity extends ReceiverActivity {
    
    static final String TAG = "ListAchievements";
    private ArrayList<HashMap<String, String>> arrayListMapAchievement;
    private List<AtAchievement> atAchievements;
    
    private RecyclerView recyclerViewAchievements;
    private RecyclerView.Adapter adapterAchievements;
    private RecyclerView.LayoutManager linearLayoutManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceStatus) {
        Log.d(TAG, "onCreate()");
        setContentView(R.layout.activity_list_achievements);
        super.onCreate(savedInstanceStatus);
        
        // Recover Widgets
        recyclerViewAchievements = (RecyclerView) findViewById(R.id.recyclerViewAchievements);
        recyclerViewAchievements.setNestedScrollingEnabled(false); // Smooth scrolling!
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewAchievements.setLayoutManager(linearLayoutManager);
    
        //adapterAchievements.notifyDataSetChanged();
    
        loadBackdrop(R.id.backdrop, R.drawable.header_achievements);
        
        
        // Setup Toolbar
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e) {
            Log.e("TAG", e.toString());
        }
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
        atAchievements = AtUtils.getAllAchievements(getApplicationContext());
        arrayListMapAchievement = new ArrayList<HashMap<String, String>>();
        loadArrayListAchievement(arrayListMapAchievement, atAchievements);
    
        //Set Adapter
        adapterAchievements = new ListAchievementsAdapter(this, arrayListMapAchievement);
        recyclerViewAchievements.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerViewAchievements.setAdapter(adapterAchievements);
    
        // Setup Name
        final String screenName = getString(R.string.menu_achievements);
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(screenName);
        
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
                        Intent intent = new Intent(ListAchievementsActivity.this, ShowPointActivity.class);
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
    
    // Load value about a list of Achievement in the array of Map
    private void loadArrayListAchievement(ArrayList<HashMap<String, String>> arrayListMapAchievement, List<AtAchievement> atAchievements) {
        HashMap<String, String> mapAchievement = null;
        
        for (AtAchievement singleAchievement : atAchievements) {
            mapAchievement = new HashMap<>();
            mapAchievement.put("achievementId", String.valueOf(singleAchievement.getIdAchievement()));
            mapAchievement.put("achievementName", singleAchievement.getName());
            mapAchievement.put("achievementTotalVoid", String.valueOf(singleAchievement.getTotalVoid()));
            mapAchievement.put("achievementTotalReached", String.valueOf(singleAchievement.getTotalReached()));
            mapAchievement.put("achievementTotalCompleted", String.valueOf(singleAchievement.getTotalCompleted()));
            mapAchievement.put("achievementStatus", String.valueOf(singleAchievement.getStatus()));
            mapAchievement.put("achievementTrophy", String.valueOf(singleAchievement.getTrophyImage()));
            arrayListMapAchievement.add(mapAchievement);
            
            Log.d(TAG, "STO CREANDO LA RIGA: " + mapAchievement);
        }
    }
}
