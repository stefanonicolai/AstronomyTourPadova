package it.snicolai.pdastrotour.utils;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Vibrator;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import it.snicolai.pdastrotour.R;


public class Utils {

    private static final String TAG = "Utils";
    
    // App Debug Visible
    public static final boolean DEBUG_ACTIVE = false;
    public static final boolean DEBUG_SCROLLBAR_ACTIVE = false;
    
    // DB first sync
    public static Integer completedInsert = 0;
    
    //
    // Shared Preferences
    //
    public static final String AtSharedPreference = "AtSharedPreference" ;
    // Current Point
    public static final String keyPointId = "keyPointId";
    public static final String keyCurrentSnackbar = "keyCurrentSnackbar";
    // Handling different Categories
    public static final String keyCurrentCategoryId = "keyCurrentCategoryId";
    // Loaded datas
    public static final String keyDbSetupCompleted = "keyDbSetupCompleted";
    public static final String keyReceivedTables = "keyReceivedTables";
    public static final String keyInsertedTables = "keyInsertedTables";
    
    public interface Callable extends Serializable {
        void call();
    }
    
    // Requesting runtime permissions.
    public static final int REQUEST_PERMISSIONS_REQUEST_CODE = 101;

    // Constant used in the location settings dialog.
    public static final int REQUEST_CHECK_SETTINGS = 0x1;
    
    public static boolean completedAllInserts() {
        return completedInsert == 9;
    }
    
    // Start the spinning dialog
    public static void startSpinner(ProgressDialog progressDialog) {
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading Routes...");
        progressDialog.show();
    }
    
    // Close the spinning dialog
    public static void stopSpinner(ProgressDialog progressDialog) {
        progressDialog.hide();
        progressDialog.dismiss();
    }
    
    // Return formatted datetime for remote DB
    public static String getDateTime(String format) {
        if (format == null) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat( format, Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String getPrintableDateTime(String dateTime, String format, String separator) {
        if (format == null) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        String printableDateTime = "";
        String date = "";
        String time = "";

        // TODO: we could print this one abit better, like with the Month's name, but we should import a Calendar and stuff
        // So ye... it will remain as it is
        String desiredOutputDate = "dd/MM/yyyy";
        String desiredOutputTime = "HH:mm";

        if( !dateTime.isEmpty() ) {
            SimpleDateFormat inputFormat = new SimpleDateFormat(format, Locale.getDefault());
            SimpleDateFormat outputFormatDate = new SimpleDateFormat(desiredOutputDate, Locale.getDefault());
            SimpleDateFormat outputFormatTime = new SimpleDateFormat(desiredOutputTime, Locale.getDefault());

            try {
                printableDateTime = outputFormatDate.format(inputFormat.parse(dateTime)) + separator + outputFormatTime.format(inputFormat.parse(dateTime));
            } catch (ParseException e) {
                Log.d(TAG, "getPrintableDateTime ERROR");
                e.printStackTrace();
            }
        }

        return printableDateTime;
    }

    // Makes the phone vibrate
    public static void phoneVibrate(Context context) {
        Log.d(TAG, "phoneVibrate()");
        Vibrator vibrate = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if(vibrate != null) {
            // Vibrate for 500 milliseconds
            vibrate.vibrate(500);
        }
    }
    
    public static void tadaaaaa(Context context) {
        Log.d(TAG, "tadaaaaa");
        MediaPlayer mp = MediaPlayer.create(context.getApplicationContext(), R.raw.tada);
        mp.start();
    }

    // Auxiliary method checking whether connectivity is available or not
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    // Might want to go through wifi only - e.g., setting enabled and
    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && (ConnectivityManager.TYPE_WIFI == networkInfo.getType()) && networkInfo.isConnected();
    }

    // Snackbar helper, 5 lines height
    public static Snackbar showSnackbar(View view, String message, String action, View.OnClickListener listener) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE).setActionTextColor(view.getContext().getResources().getColor(R.color.fab_color)).setAction(action, listener);

        View snackbarView = snackbar.getView();
        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setMaxLines(5);//this is your max line as your want
        snackbar.show();

        return snackbar;
    }

    // Check for ACCESS_FINE_LOCATION permissions
    public static boolean checkPermissions(Activity activity) {
        Log.d(TAG, "checkPermissions()");

        boolean permissions = true;

        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (
                ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
            ) {
                permissions = false;
            }
        }

        return permissions;
    }
    
    // Requests for permissions in realtime
    public static void requestPermissions(final Activity activity, final View content, final int requestPermissions, String snackbarContent) {
        Log.d(TAG, "requestPermissions()");

        boolean shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.d(TAG, "requestPermissions() - shouldProvideRationale");
            //Utils.showSnackbar(findViewById(android.R.id.content), "Location permissions are needed to play the game. Please, enable them!", "Enable", new View.OnClickListener() {
            showSnackbar(content, snackbarContent, "Enable", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Request permission
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, requestPermissions);
                }
            });
        } else {
            Log.d(TAG, "requestPermissions() - NOT shouldProvideRationale");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given status or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, requestPermissions);
        }
    }
}
