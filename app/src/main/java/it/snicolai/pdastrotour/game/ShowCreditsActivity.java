package it.snicolai.pdastrotour.game;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import it.snicolai.pdastrotour.R;
import it.snicolai.pdastrotour.ReceiverActivity;
import it.snicolai.pdastrotour.utils.Utils;

public class ShowCreditsActivity extends ReceiverActivity {
    static final transient String TAG = "ShowCreditsActivity";
    
    @Override
    protected void onCreate(Bundle savedInstanceStatus) {
        Log.d(TAG, "onCreate()");
        setContentView(R.layout.activity_show_credits);
        super.onCreate(savedInstanceStatus);
        
        // Setup Toolbar
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e) {
            Log.e("TAG", e.toString());
        }
        
        // Setup Name
        final String screenName = getString(R.string.menu_credits);
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(screenName);
        
        loadBackdrop(R.id.backdrop, R.drawable.header_credits);
        
        // Add some style to that string!
        TextView textViewCredits = (TextView) findViewById(R.id.credits_body1);
        textViewCredits.setMovementMethod(LinkMovementMethod.getInstance());
        textViewCredits.setText(Html.fromHtml(
            
            getString(R.string.credits_body1)));
        TextView credits_texts_2 = (TextView) findViewById(R.id.credits_texts_2);
        credits_texts_2.setMovementMethod(LinkMovementMethod.getInstance());
        credits_texts_2.setText(Html.fromHtml(
            
            getString(R.string.credits_texts_2)));
    }
    
    @Override
    public void onResume() {
        Log.d(TAG, "onResume()");
        super.onResume();
        
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
                        Intent intent = new Intent(ShowCreditsActivity.this, ShowPointActivity.class);
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
    
    // Inner activities: just go back, don't show the Menu. A swipe is more than enough!
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        NavUtils.navigateUpFromSameTask(this);
        return true;
    }
}

