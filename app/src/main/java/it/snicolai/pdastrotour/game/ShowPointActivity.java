package it.snicolai.pdastrotour.game;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.transition.Transition;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.gordonwong.materialsheetfab.MaterialSheetFab;

import java.util.HashMap;
import java.util.List;

import it.snicolai.pdastrotour.R;
import it.snicolai.pdastrotour.ReceiverActivity;
import it.snicolai.pdastrotour.at.AtAchievement;
import it.snicolai.pdastrotour.at.AtFab;
import it.snicolai.pdastrotour.at.AtPoint;
import it.snicolai.pdastrotour.data.MySQLiteHelper;
import it.snicolai.pdastrotour.utils.AtUtils;
import it.snicolai.pdastrotour.utils.Utils;

public class ShowPointActivity extends ReceiverActivity {
    
    static final String TAG = "ShowPointActivity";
    private AtPoint atPoint;
    private HashMap<Integer, Integer> checkButtonsId;
    private MaterialSheetFab materialSheetFab;
    private AtFab atFab;
    private RelativeLayout fabQuestionId;
    public static final String VIEW_NAME_HEADER_IMAGE = "header_image";
    private ImageView imageView;
    private Integer imageViewId;
    private Boolean isRunningTransition = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceStatus) {
        Log.d(TAG, "onCreate()");
        setContentView(R.layout.activity_show_point);
        super.onCreate(savedInstanceStatus);
        
        Integer pointId;
        
        // Getting the ID & other data from the previous activity
        Bundle extras = getIntent().getExtras();
        
        // List -> single point
        if (extras != null) {
            pointId = Integer.valueOf(extras.getString("POINT_ID"));
            String transition = extras.getString("RUNNING_TRANSITION");
            if (transition != null && transition.equals(this.VIEW_NAME_HEADER_IMAGE))
                isRunningTransition = true;
            Log.d(TAG, "PointId: " + pointId);
            Log.d(TAG, "PointId: " + extras.toString());
        } else {
            // Maybe we come from the Service. Check the SharedPreference
            SharedPreferences sharedpreferences = getSharedPreferences(Utils.AtSharedPreference, MODE_PRIVATE);
            pointId = sharedpreferences.getInt(Utils.keyPointId, 0); //0 is the default value.
    
            // Still... Well, we just go back to the awesome List i guess
            if (pointId == 0) {
                Log.d(TAG, "PointId was empty again, going back o/");
                super.onBackPressed();
                //supportFinishAfterTransition();
                finish();
            }
        }
        
        // I'm here only if i actually have a pointId!
        atPoint = AtUtils.getPoint(pointId, this);
        Log.d(TAG, "atPoint: " + atPoint);
        
        // For some weird reason -sometimes- the "killActivity" doesn't work. SO well, better safe than sorry i guess.
        if (atPoint != null) {
    
            // Resetting the Shared Pref so i don't have old values stored there forever
            SharedPreferences sharedpreferences = getSharedPreferences(Utils.AtSharedPreference, MODE_PRIVATE);
            pointId = sharedpreferences.getInt(Utils.keyPointId, 0); //0 is the default value.
            if(pointId == atPoint.getIdPoint()) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putInt(Utils.keyPointId, 0);
                editor.apply();
            }
            
            //Log.d(TAG, "DEBUG_PUNTO: " + atPoint.toString());
    
            // Connecting to the view
            //ImageView atPointImage = (ImageView) findViewById(R.id.imagePointImage);
            ImageView imageViewPointStar = (ImageView) findViewById(R.id.imageViewPointStar);
            TextView atPointName = (TextView) findViewById(R.id.textPointName);
            TextView atPointTitle = (TextView) findViewById(R.id.textPointTitle);
            TextView atPointDescription = (TextView) findViewById(R.id.textPointDescription);
            TextView atPointAddress = (TextView) findViewById(R.id.textViewPointAddress);
            // Extras
            TextView textPointExtrasTitle = (TextView) findViewById(R.id.textPointExtrasTitle);
            TextView textPointExtras = (TextView) findViewById(R.id.textPointExtras);
    
            // Setup Toolbar
            final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            try {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            } catch (NullPointerException e) {
                Log.e("TAG", e.toString());
            }
    
            // Setup Name
            final String screenName = atPoint.getName();
            final CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
            AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
            appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                boolean isShow = false;
                int scrollRange = -1;
        
                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    if (scrollRange == -1) {
                        scrollRange = appBarLayout.getTotalScrollRange();
                    }
                    if (scrollRange + verticalOffset == 0) {
                        collapsingToolbar.setTitle(screenName);
                        isShow = true;
                    } else if(isShow) {
                        collapsingToolbar.setTitle(" ");//carefull there should a space between double quote otherwise it wont work
                        isShow = false;
                    }
                }
            });
            // Rewrite default usage
            collapsingToolbar.setTitle(" ");

            if (isRunningTransition){
                // RUN THE ANIMATION FOR THE HEADER IMAGE
                imageView = (ImageView) findViewById(R.id.backdrop);
                ViewCompat.setTransitionName(imageView, VIEW_NAME_HEADER_IMAGE);
                imageViewId = getResources().getIdentifier(atPoint.getImage(), "drawable", getPackageName());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && addTransitionListener()) {
                    // If we're running on Lollipop and we have added a listener to the shared element
                    // transition, load the thumbnail. The listener will load the full-size image when
                    // the transition is complete.
                    loadThumbnail();
                } else {
                    // If all other cases we should just load the full-size image now
                    loadFullSizeImage();
                }
            }
            else{   // DEFAULT BEHAVIOR FOR LOADING THE HEADER IMAGE
                loadBackdrop(R.id.backdrop, getResources().getIdentifier(atPoint.getImage(), "drawable", getPackageName()));
            }

            // Creating the Floating Action Button
            if (atPoint.getEvent().equals(MySQLiteHelper.POINT_EVENT_QA) || atPoint.getEvent().equals(MySQLiteHelper.POINT_EVENT_REACH)) {
                View sheetView = null;
                View overlay = null;
                int sheetColor = getResources().getColor(R.color.colorGreyLight);
                int fabColor = getResources().getColor(R.color.fab_color);
                
                // QA
                if (atPoint.getEvent().equals(MySQLiteHelper.POINT_EVENT_QA)) {
                    atFab = (AtFab) findViewById(R.id.question_Fab);
                    atFab.setEnabled(true);
                    sheetView = findViewById(R.id.cardViewQuestion);
                    overlay = findViewById(R.id.overlayQa);
                    
                    TextView questionView = (TextView) findViewById(R.id.questionText);
                    questionView.setText(atPoint.getQuestion());
                    
                    TextView textViewAnswer1 = (TextView) findViewById(R.id.textViewAnswer1);
                    TextView textViewAnswer2 = (TextView) findViewById(R.id.textViewAnswer2);
                    TextView textViewAnswer3 = (TextView) findViewById(R.id.textViewAnswer3);
                    TextView textViewAnswer4 = (TextView) findViewById(R.id.textViewAnswer4);
                    textViewAnswer1.setText("1) " + atPoint.getAnswer1());
                    textViewAnswer2.setText("2) " + atPoint.getAnswer2());
                    textViewAnswer3.setText("3) " + atPoint.getAnswer3());
                    textViewAnswer4.setText("4) " + atPoint.getAnswer4());
                }
                // Reach
                else if (atPoint.getEvent().equals(MySQLiteHelper.POINT_EVENT_REACH)) {
                    atFab = (AtFab) findViewById(R.id.fabReachPoint);
                    atFab.setEnabled(true);
                    sheetView = findViewById(R.id.cardViewReachPoint);
                    overlay = findViewById(R.id.overlayReachPoint);
                    
                    TextView textViewReachPointTitle = (TextView) findViewById(R.id.textViewReachPointTitle);
                    TextView textViewIntroduction = (TextView) findViewById(R.id.textViewIntroduction);
                    TextView textViewDescription = (TextView) findViewById(R.id.textViewDescription);
                    textViewReachPointTitle.setText(atPoint.getReachTitle());
                    textViewIntroduction.setText(getString(R.string.point_reach_point_title));
                    textViewDescription.setText(Html.fromHtml(atPoint.getReachDescription()));
                }
                
                // Initialize the stuff
                materialSheetFab = new MaterialSheetFab<>(atFab, sheetView, overlay, sheetColor, fabColor);
            }
            
            // Debug
            TextView textPointStatus = (TextView) findViewById(R.id.textPointStatus);
            TextView textPointTimeReached = (TextView) findViewById(R.id.textPointTimeReached);
            TextView textPointTimeCompleted = (TextView) findViewById(R.id.textPointTimeCompleted);
            TextView textPointParent = (TextView) findViewById(R.id.textPointParent);
            TextView atPointCategories = (TextView) findViewById(R.id.textPointCategories);
            TextView atPointAchievements = (TextView) findViewById(R.id.textPointAchievements);
            
    
            // Star and Name
            imageViewPointStar.setImageDrawable(getResources().getDrawable(atPoint.getImageStar()));
            atPointName.setText(atPoint.getName());
            atPointTitle.setText(atPoint.getTitle());
            atPointDescription.setText(Html.fromHtml(atPoint.getDescription()));
            // Extras
            if (atPoint.getEvent().equals(MySQLiteHelper.POINT_EVENT_QA)) {
                textPointExtrasTitle.setText(R.string.point_extras_qa_title);
                textPointExtras.setText(Html.fromHtml(atPoint.getQuestion() + "<br/><b>" + atPoint.getCorrectAnswer() + "</b>"));
            } else {
                textPointExtrasTitle.setText(R.string.point_extras_title);
                textPointExtras.setText(atPoint.getExtras());
            }
            
            // Go to Gmap
            atPointAddress.setText(atPoint.getAddress());
            
            // Check visibility for Map or Parent buttons
            if (atPoint.isChild() && !atPoint.hasBeenReached()) {
                View buttonGoToMap = (View) findViewById(R.id.buttonGoToMap);
                buttonGoToMap.setVisibility(View.GONE);
                atPointAddress.setVisibility(View.GONE);
                
                AtPoint atParent = AtUtils.getPoint(atPoint.getParentId(), this);
                
                // Go To Parent
                Button buttonGoToParent = (Button) findViewById(R.id.buttonGoToParent);
                buttonGoToParent.setText(getResources().getString(R.string.point_goto_parent) + " " + atParent.getName());
                buttonGoToParent.setVisibility(View.VISIBLE);
            }
            
            // Fab
            fabQuestionId = findViewById(R.id.fabQuestionId);
            
            // Achievements
            atPointCategories.setText(TextUtils.join(MySQLiteHelper.GROUP_CONCAT_DIVIDER, atPoint.getCategories()));
            String achievements = " ";
            List<AtAchievement> atAchievements = AtUtils.getAllAchievements(getApplicationContext());
    
            for (AtAchievement singleAchievement : atAchievements) {
                String[] pointIds = singleAchievement.getPointIds();
                for (String pointIdLocal : pointIds) {
                    if (Integer.valueOf(pointIdLocal) == atPoint.getIdPoint()) {
                        achievements += singleAchievement.getName() + ", ";
                    }
                }
            }
            if(!achievements.equals(" ")) {
                TextView textPointDescriptionTitle = (TextView) findViewById(R.id.textPointDescriptionTitle);
                textPointDescriptionTitle.setVisibility(View.VISIBLE);
                achievements = achievements.substring(0, achievements.length() - 2) + ".";
                atPointAchievements.setVisibility(View.VISIBLE);
            }
            atPointAchievements.setText(Html.fromHtml(getString(R.string.point_related_achievements, achievements)));
            
            if(Utils.DEBUG_ACTIVE) {
                // Debug
                textPointStatus.setText(atPoint.getStatus());
                textPointTimeReached.setText(atPoint.getTimeReached());
                textPointTimeCompleted.setText(atPoint.getTimeCompleted());
    
                String parentName = "nessun genitore per questo punto";
                Integer parentId = atPoint.getParentId();
                if (parentId != 0) {
                    AtPoint debuggolo = AtUtils.getPoint(parentId, this);
                    parentName = debuggolo.getName();
                }
                textPointParent.setText(parentName);
    
                // DEBUG
    
                // Show the Debug!
                LinearLayout linearLayoutShowPointDebug1 = (LinearLayout) findViewById(R.id.linearLayoutShowPointDebug1);
                LinearLayout linearLayoutShowPointDebug2 = (LinearLayout) findViewById(R.id.linearLayoutShowPointDebug2);
                linearLayoutShowPointDebug1.setVisibility(View.VISIBLE);
                linearLayoutShowPointDebug2.setVisibility(View.VISIBLE);
            }
            
        } else {
            killActivity();
        }
    }
    
    @Override
    public void onResume() {
        Log.d(TAG, "onResume()");
        super.onResume();
        
        // Unlocked Extras?
        if (atPoint.getStatus().equals(MySQLiteHelper.POINT_STATUS_COMPLETED) &&
            (atPoint.getEvent().equals(MySQLiteHelper.POINT_EVENT_QA) || atPoint.getEvent().equals(MySQLiteHelper.POINT_EVENT_EXTRAS))
            ) {
            LinearLayout linearLayoutExtras = (LinearLayout) findViewById(R.id.linearLayoutExtras);
            linearLayoutExtras.setVisibility(View.VISIBLE);
        }
        
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
                        Intent intent = new Intent(ShowPointActivity.this, ShowPointActivity.class);
                        Bundle dataBundle = new Bundle();
                        dataBundle.putString("POINT_ID", pointId.toString());
                        intent.putExtras(dataBundle);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(intent);
                        finish();
                    }
                }
            );
        } else {
            if(localSnackbar != null) {
                localSnackbar.dismiss();
            }
        }
        
        // Events handling
        if (atPoint.getStatus().equals(MySQLiteHelper.POINT_STATUS_REACHED)) {
            Log.d(TAG, "Events handling: " + atPoint.getStatus());
            String toastText = "";
            
            switch (atPoint.getEvent()) {
                // Go to Point
                case MySQLiteHelper.POINT_EVENT_REACH:
                    View fabReachPointId = (View) findViewById(R.id.fabReachPointId);
                    fabReachPointId.setVisibility(View.VISIBLE);
                    // No direct update on the DB here, i'm already handling it in the child point
                    break;
                // Do Question
                case MySQLiteHelper.POINT_EVENT_QA:
                    View fabQuestionId = (View) findViewById(R.id.fabQuestionId);
                    fabQuestionId.setVisibility(View.VISIBLE);
                    break;
                // Show Extra Infos
                case MySQLiteHelper.POINT_EVENT_EXTRAS:
                    // Show extras, set completed on this point
                    LinearLayout linearLayoutExtras = (LinearLayout) findViewById(R.id.linearLayoutExtras);
                    linearLayoutExtras.setVisibility(View.VISIBLE);
                    AtUtils.saveCompletedStatus(atPoint, this);
                    toastText = getString(R.string.point_event_completed_extras);
                    Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_LONG).show();
                    
                    // Saving the status + reloading
                    AtUtils.saveCompletedStatus(atPoint, this);
                    
                    // Mayube we have a childPoint with some extras?
                    if (atPoint.isChild()) {
                        Log.d(TAG, "atPoint.isChild()");
                        AtUtils.setParentCompleted(atPoint, this);
                    }
    
                    // Refreshing the screen
                    //supportFinishAfterTransition();
                    finish();
                    startActivity(getIntent());
                    
                    break;
                // Empty state, so i just complete this point
                default:
                    AtUtils.saveCompletedStatus(atPoint, this);
                    toastText = getString(R.string.point_event_completed_void);
                    
                    // Maybe this was a child point? Need to update his parent too!
                    if (atPoint.isChild()) {
                        Log.d(TAG, "atPoint.isChild()");
                        AtUtils.setParentCompleted(atPoint, this);
                        toastText = getString(R.string.point_event_completed_reach);
                    }
                    
                    Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_LONG).show();
    
                    // Refreshing the screen
                    //supportFinishAfterTransition();
                    finish();
                    startActivity(getIntent());
                    break;
            }
        }
    }
    
    // Inner activities: just go back, don't show the Menu. A swipe is more than enough!
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //NavUtils.navigateUpFromSameTask(this);
        onBackPressed();
        return true;
    }

    /**
     * Load the item's thumbnail image into our {@link ImageView}.
     */
    private void loadThumbnail() {
        Log.d(TAG, "loadImage Thumbnail !!");
        Glide
                .with(this)
                .load(imageViewId)
                .override(120,80)
                .centerCrop()
                .into(imageView);
    }

    /**
     * Load the item's full-size image into our {@link ImageView}.
     */
    private void loadFullSizeImage() {
        Log.d(TAG, "loadImage full size!!");
        imageView.setImageDrawable(getResources().getDrawable(imageViewId));
        /*Glide
                .with(this)
                .load(imageViewId)
                .centerCrop()
                .into(imageView); */
    }

    // Listener che gestisce le transazioni, e penso sia attento anche verso quale activity rimanda la app.
    private boolean addTransitionListener() {
        final Transition transition = getWindow().getSharedElementEnterTransition();

        if (transition != null) {
            // There is an entering shared element transition so add a listener to it
            transition.addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionEnd(Transition transition) {
                    // As the transition has ended, we can now load the full-size image
                    loadFullSizeImage();

                    // Make sure we remove ourselves as a listener
                    transition.removeListener(this);
                }

                @Override
                public void onTransitionStart(Transition transition) {
                    // No-op
                }

                @Override
                public void onTransitionCancel(Transition transition) {
                    // Make sure we remove ourselves as a listener
                    transition.removeListener(this);
                }

                @Override
                public void onTransitionPause(Transition transition) {
                    // No-op
                }

                @Override
                public void onTransitionResume(Transition transition) {
                    // No-op
                }
            });
            return true;
        }
        // If we reach here then we have not added a listener
        return false;
    }
    
    public void goToShowMap(View view) {
        Log.d(TAG, "goToShowMap()");
        
        Integer pointId = atPoint.getIdPoint();
        
        Intent intent = new Intent(this, ShowMapActivity.class);
        Bundle dataBundle = new Bundle();
        dataBundle.putInt("POINT_ID", pointId);
        dataBundle.putBoolean("POINT_HIGHLIGHT", true);
        intent.putExtras(dataBundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    
    public void goToParent(View view) {
        Log.d(TAG, "goToParent()");
        
        String parentId = atPoint.getParentId().toString();
        
        Intent intent = new Intent(this, ShowPointActivity.class);
        Bundle dataBundle = new Bundle();
        dataBundle.putString("POINT_ID", parentId);
        intent.putExtras(dataBundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    
    public void setStatus(View view) {
        Log.d(TAG, "setStatus()");
        
        switch (view.getId()) {
            
            case R.id.buttonSetStatusVoid:
                atPoint = AtUtils.saveNotReachedStatus(atPoint, this);
                break;
            
            case R.id.buttonSetStatusReached:
                atPoint = AtUtils.saveReachedStatus(atPoint, this);
                break;
            
            case R.id.buttonSetStatusCompleted:
                atPoint = AtUtils.saveCompletedStatus(atPoint, this);
                break;
        }
        //supportFinishAfterTransition();
        finish();
        startActivity(getIntent());
    }
    
    public void confirmAnswer(View view) {
        Integer checkedAnswer = Integer.valueOf((String) view.getTag());
        
        if (atPoint == null || checkedAnswer == -1)
            return;
        // sbianco tutte le altre view delle risposte
        View answer1 = (View) findViewById(R.id.textViewAnswer1);
        View answer2 = (View) findViewById(R.id.textViewAnswer2);
        View answer3 = (View) findViewById(R.id.textViewAnswer3);
        View answer4 = (View) findViewById(R.id.textViewAnswer4);
        answer1.setBackgroundColor(getResources().getColor(R.color.cardview_shadow_end_color));
        answer2.setBackgroundColor(getResources().getColor(R.color.cardview_shadow_end_color));
        answer3.setBackgroundColor(getResources().getColor(R.color.cardview_shadow_end_color));
        answer4.setBackgroundColor(getResources().getColor(R.color.cardview_shadow_end_color));

        if (atPoint.getPosCorrect() == checkedAnswer) {
            startColorAnimation(view, R.color.colorGreen);
            if (materialSheetFab != null) {
                // Hiding stuff
                fabQuestionId.postDelayed(new Runnable() {
                    public void run() {
                        fabQuestionId.setVisibility(View.GONE);
                    }
                }, 1500);
                
                // Here i update the DB
                AtUtils.saveCompletedStatus(atPoint, this);
                
                // Keep the player in the loop
                String toastText = getString(R.string.point_event_completed_qa);
                Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_LONG).show();
                
                // Refreshing the screen
                //supportFinishAfterTransition();
                finish();
                startActivity(getIntent());
            }
            Utils.phoneVibrate(this);
            Utils.tadaaaaa(this);
        } else {
            startColorAnimation(view, R.color.colorRed);
            //view.setOnClickListener(null);
        }
    }
    
    private void startColorAnimation(View view, Integer colorEnd) {
        int colorStart = R.color.colorGreyLight;
        
        ColorDrawable[] color = {new ColorDrawable(getResources().getColor(colorStart)), new ColorDrawable(getResources().getColor(colorEnd))};
        TransitionDrawable trans = new TransitionDrawable(color);
        //This will work also on old devices. The latest API says you have to use setBackground instead.
        view.setBackgroundDrawable(trans);
        trans.startTransition(500);
    }
    
    private void killActivity() {
        super.onBackPressed();
        //supportFinishAfterTransition();
        finish();
    }
}
