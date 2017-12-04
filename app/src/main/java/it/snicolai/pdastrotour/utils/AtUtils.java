package it.snicolai.pdastrotour.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import it.snicolai.pdastrotour.at.AtAchievement;
import it.snicolai.pdastrotour.at.AtCategory;
import it.snicolai.pdastrotour.at.AtPoint;
import it.snicolai.pdastrotour.data.LocalContentProvider;
import it.snicolai.pdastrotour.data.MySQLiteHelper;


// This class provides all general methods needed by the application
public class AtUtils {
    private static final String TAG = "AtUtils";
    
    // Returns the list of playable points
    public static List<AtPoint> getAllPoints(String pointStatus, String childrenStatus, Context context) {
        Log.d(TAG, "getLocalPointsList()");
        List<AtPoint> points = new ArrayList<>();
        AtCategory atCategory = null;
        boolean canAdd = true;
        
        // Getting points from the DB
        String[] columns = new String[]{
            MySQLiteHelper.COLUMN_POINT_ID,
            MySQLiteHelper.COLUMN_POINT_POS,
            MySQLiteHelper.COLUMN_POINTINFO_NAME,
            MySQLiteHelper.COLUMN_POINTINFO_TITLE,
            MySQLiteHelper.COLUMN_POINTINFO_ADDRESS,
            MySQLiteHelper.COLUMN_POINTINFO_DESCRIPTION,
            MySQLiteHelper.COLUMN_POINT_IMAGE,
            MySQLiteHelper.COLUMN_POINT_LAT,
            MySQLiteHelper.COLUMN_POINT_LON,
            MySQLiteHelper.COLUMN_POINT_RADIUS,
            MySQLiteHelper.COLUMN_GAME_STATUS,
            MySQLiteHelper.COLUMN_GAME_TIME_REACHED,
            MySQLiteHelper.COLUMN_GAME_TIME_COMPLETED,
            MySQLiteHelper.COLUMN_REACHPOINT_PARENT_ID,
        };
        
        String selection = "";
        String[] selectionArgs = new String[]{};
        
        // Unless it's an empty string, filter!
        if (!pointStatus.isEmpty()) {
            selection = "(" + MySQLiteHelper.COLUMN_GAME_STATUS + " = ?)";
            selectionArgs = new String[]{String.valueOf(pointStatus)};
        }
        
        Cursor cursor = context.getContentResolver().query(
            LocalContentProvider.URI_POINT,
            columns,
            selection,
            selectionArgs,
            MySQLiteHelper.COLUMN_POINT_POS + " ASC, " + MySQLiteHelper.COLUMN_POINT_ID + " ASC" // returning the points in the correct order (position)
        );
        
        
        // Percorsi
        SharedPreferences sharedpreferences = context.getSharedPreferences(Utils.AtSharedPreference, context.MODE_PRIVATE);
        Integer categoryId = sharedpreferences.getInt(Utils.keyCurrentCategoryId, 0); //0 is the default value.
        
        Log.d(TAG, "PERCORSI: " + categoryId);
        if (categoryId != 0) {
            atCategory = getCategory(categoryId, context);
            Log.d(TAG, "PERCORSI: " + atCategory);
        }
        
        // Cycling on the results
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                if (cursor.isAfterLast()) {
                    break;
                }
                
                canAdd = true;
                
                // Saving the correct column
                int pos_i = cursor.getColumnIndex(MySQLiteHelper.COLUMN_POINT_ID);
                int pos_p = cursor.getColumnIndex(MySQLiteHelper.COLUMN_POINT_POS);
                int pos_n = cursor.getColumnIndex(MySQLiteHelper.COLUMN_POINTINFO_NAME);
                int pos_t = cursor.getColumnIndex(MySQLiteHelper.COLUMN_POINTINFO_TITLE);
                int pos_a = cursor.getColumnIndex(MySQLiteHelper.COLUMN_POINTINFO_ADDRESS);
                int pos_d = cursor.getColumnIndex(MySQLiteHelper.COLUMN_POINTINFO_DESCRIPTION);
                int pos_im = cursor.getColumnIndex(MySQLiteHelper.COLUMN_POINT_IMAGE);
                int pos_la = cursor.getColumnIndex(MySQLiteHelper.COLUMN_POINT_LAT);
                int pos_lo = cursor.getColumnIndex(MySQLiteHelper.COLUMN_POINT_LON);
                int pos_r = cursor.getColumnIndex(MySQLiteHelper.COLUMN_POINT_RADIUS);
                int pos_s = cursor.getColumnIndex(MySQLiteHelper.COLUMN_GAME_STATUS);
                int pos_rt = cursor.getColumnIndex(MySQLiteHelper.COLUMN_GAME_TIME_REACHED);
                int pos_rc = cursor.getColumnIndex(MySQLiteHelper.COLUMN_GAME_TIME_COMPLETED);
                int pos_pid = cursor.getColumnIndex(MySQLiteHelper.COLUMN_REACHPOINT_PARENT_ID);
                
                // Initializing variables
                int id = cursor.getInt(pos_i);
                int p = cursor.getInt(pos_p);
                String n = cursor.getString(pos_n);
                String t = cursor.getString(pos_t);
                String a = cursor.getString(pos_a);
                String d = cursor.getString(pos_d);
                String im = cursor.getString(pos_im);
                Double la = cursor.getDouble(pos_la);
                Double ln = cursor.getDouble(pos_lo);
                int r = cursor.getInt(pos_r);
                String s = cursor.getString(pos_s);
                String rt = cursor.getString(pos_rt);
                String rc = cursor.getString(pos_rc);
                int pid = cursor.getInt(pos_pid);
                
                // Creating the point
                AtPoint atPoint = new AtPoint(id, p, n, t, a, d, im,
                    la, ln, r, s,
                    rt, rc,
                    pid, context);
                //Log.d("NewPoint:", atPoint.toString());
                
                // I want to check on children status
                if (!childrenStatus.isEmpty()) {
                    // Children are ok, but only if parent isn't VOID
                    if (atPoint.isChild()) {
                        AtPoint parent = getPoint(atPoint.getParentId(), context);
                        if (parent.getStatus().equals(MySQLiteHelper.POINT_STATUS_VOID)) {
                            canAdd = false;
                            Log.d("JUMPEDPoint:", atPoint.toString());
                        }
                    }
                }
                
                // Percorsi
                if (atCategory != null) {
                    canAdd = false;
                    for (String idPoint : atCategory.getPointIds()) {
                        if (idPoint.equals(atPoint.getIdPoint().toString())) {
                            canAdd = true;
                            break; // Thanx Cata!
                        }
                    }
                    // Out of there, now we check if this ID is a child of a father that belongs to the category
                    if (!canAdd && atPoint.isChild()) {
                        AtPoint parentPoint = getPoint(atPoint.getParentId(), context);
                        for (String idPoint : atCategory.getPointIds()) {
                            if (idPoint.equals(parentPoint.getIdPoint().toString())) {
                                canAdd = true;
                                break;
                            }
                        }
                    }
                }
                
                // Adding the point to the list
                if (canAdd) {
                    try {
                        points.add(atPoint);
                    } catch (NullPointerException e) {
                        Log.d(TAG, e.toString());
                    }
                }
            }
        } else {
            // cursor is null or empty
            Log.e(TAG, "getLocalPointsList() | Cursor is null or empty");
        }
        cursor.close();
        
//        Log.d(TAG, "PERCORSI: listaFinale " + points.size() + "\n" + points.toString());
        
        return points;
    }
    
    // Returns a single AtPoint having that specific ID
    public static AtPoint getPoint(Integer pointId, Context context) {
        Log.d(TAG, "getPoint()");
        
        AtPoint atPoint = null;
        
        // Getting points from the DB
        String[] columns = new String[]{
            MySQLiteHelper.COLUMN_POINT_ID,
            MySQLiteHelper.COLUMN_POINT_POS,
            MySQLiteHelper.COLUMN_POINTINFO_NAME,
            MySQLiteHelper.COLUMN_POINTINFO_TITLE,
            MySQLiteHelper.COLUMN_POINTINFO_ADDRESS,
            MySQLiteHelper.COLUMN_POINTINFO_DESCRIPTION,
            MySQLiteHelper.COLUMN_POINT_IMAGE,
            MySQLiteHelper.COLUMN_POINT_LAT,
            MySQLiteHelper.COLUMN_POINT_LON,
            MySQLiteHelper.COLUMN_POINT_RADIUS,
            MySQLiteHelper.COLUMN_GAME_STATUS,
            MySQLiteHelper.COLUMN_POINTINFO_EVENT,
            MySQLiteHelper.COLUMN_GAME_TIME_REACHED,
            MySQLiteHelper.COLUMN_GAME_TIME_COMPLETED,
            MySQLiteHelper.COLUMN_QA_QUESTION,
            MySQLiteHelper.COLUMN_QA_ANSWER_1,
            MySQLiteHelper.COLUMN_QA_ANSWER_2,
            MySQLiteHelper.COLUMN_QA_ANSWER_3,
            MySQLiteHelper.COLUMN_QA_ANSWER_4,
            MySQLiteHelper.COLUMN_QA_POS_CORRECT,
            MySQLiteHelper.GROUP_CONCAT_CATEGORY,
            MySQLiteHelper.COLUMN_POINTINFO_EXTRAS,
            MySQLiteHelper.COLUMN_REACHPOINT_PARENT_ID,
            MySQLiteHelper.COLUMN_POINTINFO_REACH_TITLE,
            MySQLiteHelper.COLUMN_POINTINFO_REACH_DESCRIPTION,
        };
        
        String selection = "(" + MySQLiteHelper.COLUMN_POINT_ID + " = ?)";
        String[] selectionArgs = new String[]{pointId.toString()};
        
        Cursor cursor = context.getContentResolver().query(
            LocalContentProvider.URI_POINT,
            columns,
            selection,
            selectionArgs,
            ""
        );
        
        // Cycling on the results
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToNext();
            
            // Saving the correct column
            int pos_i = cursor.getColumnIndex(MySQLiteHelper.COLUMN_POINT_ID);
            int pos_p = cursor.getColumnIndex(MySQLiteHelper.COLUMN_POINT_POS);
            int pos_n = cursor.getColumnIndex(MySQLiteHelper.COLUMN_POINTINFO_NAME);
            int pos_t = cursor.getColumnIndex(MySQLiteHelper.COLUMN_POINTINFO_TITLE);
            int pos_a = cursor.getColumnIndex(MySQLiteHelper.COLUMN_POINTINFO_ADDRESS);
            int pos_d = cursor.getColumnIndex(MySQLiteHelper.COLUMN_POINTINFO_DESCRIPTION);
            int pos_im = cursor.getColumnIndex(MySQLiteHelper.COLUMN_POINT_IMAGE);
            int pos_la = cursor.getColumnIndex(MySQLiteHelper.COLUMN_POINT_LAT);
            int pos_lo = cursor.getColumnIndex(MySQLiteHelper.COLUMN_POINT_LON);
            int pos_r = cursor.getColumnIndex(MySQLiteHelper.COLUMN_POINT_RADIUS);
            int pos_s = cursor.getColumnIndex(MySQLiteHelper.COLUMN_GAME_STATUS);
            int pos_e = cursor.getColumnIndex(MySQLiteHelper.COLUMN_POINTINFO_EVENT);
            int pos_rt = cursor.getColumnIndex(MySQLiteHelper.COLUMN_GAME_TIME_REACHED);
            int pos_rc = cursor.getColumnIndex(MySQLiteHelper.COLUMN_GAME_TIME_COMPLETED);
            int pos_q_q = cursor.getColumnIndex(MySQLiteHelper.COLUMN_QA_QUESTION);
            int pos_q_a1 = cursor.getColumnIndex(MySQLiteHelper.COLUMN_QA_ANSWER_1);
            int pos_q_a2 = cursor.getColumnIndex(MySQLiteHelper.COLUMN_QA_ANSWER_2);
            int pos_q_a3 = cursor.getColumnIndex(MySQLiteHelper.COLUMN_QA_ANSWER_3);
            int pos_q_a4 = cursor.getColumnIndex(MySQLiteHelper.COLUMN_QA_ANSWER_4);
            int pos_q_pc = cursor.getColumnIndex(MySQLiteHelper.COLUMN_QA_POS_CORRECT);
            int pos_cat = cursor.getColumnIndex(MySQLiteHelper.GROUP_CONCAT_CATEGORY);
            int pos_ex = cursor.getColumnIndex(MySQLiteHelper.COLUMN_POINTINFO_EXTRAS);
            int pos_rpc = cursor.getColumnIndex(MySQLiteHelper.COLUMN_REACHPOINT_PARENT_ID);
            int pos_rpt = cursor.getColumnIndex(MySQLiteHelper.COLUMN_POINTINFO_REACH_TITLE);
            int pos_rpd = cursor.getColumnIndex(MySQLiteHelper.COLUMN_POINTINFO_REACH_DESCRIPTION);
            
            // Initializing variables
            int id = cursor.getInt(pos_i);
            int p = cursor.getInt(pos_p);
            String n = cursor.getString(pos_n);
            String t = cursor.getString(pos_t);
            String a = cursor.getString(pos_a);
            String d = cursor.getString(pos_d);
            String im = cursor.getString(pos_im);
            Double la = cursor.getDouble(pos_la);
            Double ln = cursor.getDouble(pos_lo);
            Integer r = cursor.getInt(pos_r);
            String s = cursor.getString(pos_s);
            String e = cursor.getString(pos_e);
            String rt = cursor.getString(pos_rt);
            String rc = cursor.getString(pos_rc);
            String q = cursor.getString(pos_q_q);
            String a1 = cursor.getString(pos_q_a1);
            String a2 = cursor.getString(pos_q_a2);
            String a3 = cursor.getString(pos_q_a3);
            String a4 = cursor.getString(pos_q_a4);
            int q_pc = cursor.getInt(pos_q_pc);
            String cat = cursor.getString(pos_cat);
            String ex = cursor.getString(pos_ex);
            int rpc = cursor.getInt(pos_rpc);
            String rpt = cursor.getString(pos_rpt);
            String rpd = cursor.getString(pos_rpd);
            
            // Creating the point
            atPoint = new AtPoint(id, p, n, t, a, d,
                im, la, ln, r, cat,
                s, e, rt, rc,
                q, a1, a2, a3, a4, q_pc,
                ex, rpc, rpt, rpd, context);
            Log.d("NewPoint:", atPoint.toString());
        } else {
            // cursor is null or empty
            Log.e(TAG, "getLocalPointsList() | Cursor is null or empty");
        }
        cursor.close();
        //Log.d(TAG, "getLocalPointsList() -> Points List: " + points.toString());
        
        return atPoint;
    }
    
    
    // Returns the list of playable points
    public static List<AtCategory> getAllCategories(Context context) {
        Log.d(TAG, "getAllCategories()");
        List<AtCategory> categories = new ArrayList<>();
        
        // Getting points from the DB
        String[] columns = new String[]{
            MySQLiteHelper.COLUMN_CATEGORY_ID,
            MySQLiteHelper.COLUMN_CATEGORY_NAME,
            MySQLiteHelper.GROUP_CONCAT_POINT,
        };
        
        String selection = "";
        String[] selectionArgs = new String[]{};
        
        
        Cursor cursor = context.getContentResolver().query(
            LocalContentProvider.URI_CATEGORY,
            columns,
            selection,
            selectionArgs,
            MySQLiteHelper.COLUMN_CATEGORY_ID + " ASC" // returning the points in the correct order (position)
        );
        
        // Cycling on the results
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                if (cursor.isAfterLast()) {
                    break;
                }
                
                // Saving the correct column
                int pos_i = cursor.getColumnIndex(MySQLiteHelper.COLUMN_CATEGORY_ID);
                int pos_n = cursor.getColumnIndex(MySQLiteHelper.COLUMN_CATEGORY_NAME);
                int pos_p = cursor.getColumnIndex(MySQLiteHelper.GROUP_CONCAT_POINT);
                
                // Initializing variables
                int id = cursor.getInt(pos_i);
                String n = cursor.getString(pos_n);
                String p = cursor.getString(pos_p);
                
                if (p != null) {
                    String[] pointIds = p.split(MySQLiteHelper.GROUP_CONCAT_DIVIDER);
                    Log.d("PERCORSI - p:", p.toString());
                    Log.d("PERCORSI - pointIds:", pointIds.toString());
                    
                    // Creating the point
                    AtCategory atCategory = new AtCategory(id, n, pointIds);
                    Log.d("PERCORSI - NewCategory:", atCategory.toString());
                    
                    // Adding the point to the list
                    try {
                        categories.add(atCategory);
                    } catch (NullPointerException e) {
                        Log.d(TAG, e.toString());
                    }
                }
            }
        } else {
            // cursor is null or empty
            Log.e(TAG, "getLocalPointsList() | Cursor is null or empty");
        }
        cursor.close();
        //Log.d(TAG, "getLocalPointsList() -> Points List: " + points.toString());
        
        return categories;
    }
    
    // Returns a single AtPoint having that specific ID
    public static AtCategory getCategory(Integer categoryId, Context context) {
        Log.d(TAG, "getCategory()");
        
        AtCategory atCategory = null;
        
        // Getting points from the DB
        String[] columns = new String[]{
            MySQLiteHelper.COLUMN_CATEGORY_ID,
            MySQLiteHelper.COLUMN_CATEGORY_NAME,
            MySQLiteHelper.GROUP_CONCAT_POINT,
        };
        
        String selection = "(" + MySQLiteHelper.COLUMN_CATEGORY_ID + " = ?)";
        String[] selectionArgs = new String[]{categoryId.toString()};
        
        Cursor cursor = context.getContentResolver().query(
            LocalContentProvider.URI_CATEGORY,
            columns,
            selection,
            selectionArgs,
            ""
        );
        
        // Cycling on the results
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                if (cursor.isAfterLast()) {
                    break;
                }
                
                // Saving the correct column
                int pos_i = cursor.getColumnIndex(MySQLiteHelper.COLUMN_CATEGORY_ID);
                int pos_n = cursor.getColumnIndex(MySQLiteHelper.COLUMN_CATEGORY_NAME);
                int pos_p = cursor.getColumnIndex(MySQLiteHelper.GROUP_CONCAT_POINT);
                
                // Initializing variables
                int id = cursor.getInt(pos_i);
                String n = cursor.getString(pos_n);
                String p = cursor.getString(pos_p);
                
                if (p != null) {
                    String[] pointIds = p.split(MySQLiteHelper.GROUP_CONCAT_DIVIDER);
                    
                    // Creating the point
                    atCategory = new AtCategory(id, n, pointIds);
                    //Log.d("NewCategory:", atCategory.toString());
                }
            }
        } else {
            // cursor is null or empty
            Log.e(TAG, "getLocalPointsList() | Cursor is null or empty");
        }
        cursor.close();
        //Log.d(TAG, "getLocalPointsList() -> Points List: " + points.toString());
        
        return atCategory;
    }
    
    
    // Returns the list of Achievements
    public static List<AtAchievement> getAllAchievements(Context context) {
        Log.d(TAG, "getAllAchievements()");
        List<AtAchievement> achievements = new ArrayList<>();
        
        // Getting points from the DB
        String[] columns = new String[]{
            MySQLiteHelper.COLUMN_ACHIEVEMENT_ID,
            MySQLiteHelper.COLUMN_ACHIEVEMENT_NAME,
            MySQLiteHelper.GROUP_CONCAT_POINT,
        };
        
        String selection = "";
        String[] selectionArgs = new String[]{};
        
        Cursor cursor = context.getContentResolver().query(
            LocalContentProvider.URI_ACHIEVEMENT,
            columns,
            selection,
            selectionArgs,
            MySQLiteHelper.COLUMN_ACHIEVEMENT_ID + " ASC" // returning the points in the correct order (position)
        );
        
        // Cycling on the results
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                if (cursor.isAfterLast()) {
                    break;
                }
                
                // Saving the correct column
                int pos_i = cursor.getColumnIndex(MySQLiteHelper.COLUMN_ACHIEVEMENT_ID);
                int pos_n = cursor.getColumnIndex(MySQLiteHelper.COLUMN_ACHIEVEMENT_NAME);
                int pos_p = cursor.getColumnIndex(MySQLiteHelper.GROUP_CONCAT_POINT);
                
                // Initializing variables
                int id = cursor.getInt(pos_i);
                String n = cursor.getString(pos_n);
                String p = cursor.getString(pos_p);
                
                if (p != null) {
                    String[] pointIds = p.split(MySQLiteHelper.GROUP_CONCAT_DIVIDER);
                    
                    // Calculating the status
                    int totalVoid = 0;
                    int totalReached = 0;
                    int totalCompleted = 0;
                    AtPoint atPoint = null;
                    for (String pointId : pointIds) {
                        atPoint = AtUtils.getPoint(Integer.valueOf(pointId), context);
                        switch (atPoint.getStatus()) {
                            case MySQLiteHelper.POINT_STATUS_REACHED:
                                totalReached++;
                                break;
                            case MySQLiteHelper.POINT_STATUS_COMPLETED:
                                totalCompleted++;
                                break;
                            default:
                                totalVoid++;
                                break;
                        }
                        atPoint = null; // Memory
                    }
                    
                    // Creating the point
                    AtAchievement atAchievement = new AtAchievement(id, n, pointIds, totalVoid, totalReached, totalCompleted);
                    //Log.d("NewAchievement:", atAchievement.toString());
                    
                    // Adding the point to the list
                    try {
                        achievements.add(atAchievement);
                    } catch (NullPointerException e) {
                        Log.d(TAG, e.toString());
                    }
                }
            }
        } else {
            // cursor is null or empty
            Log.e(TAG, "getLocalPointsList() | Cursor is null or empty");
        }
        cursor.close();
        //Log.d(TAG, "getLocalPointsList() -> Points List: " + points.toString());
        
        return achievements;
    }
    
    
    // Reset the point to void state
    public static AtPoint saveNotReachedStatus(AtPoint atPoint, Context context) {
        Log.d(TAG, "saveNotReachedStatus()");
        
        // Saving the status
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_GAME_STATUS, MySQLiteHelper.POINT_STATUS_VOID);
        values.put(MySQLiteHelper.COLUMN_GAME_TIME_REACHED, "");
        values.put(MySQLiteHelper.COLUMN_GAME_TIME_COMPLETED, "");
        String selection = "(" + MySQLiteHelper.COLUMN_GAME_POINT_ID + " = ?)";
        String[] selectionArgs = new String[]{atPoint.getIdPoint().toString()};
        
        Integer result = context.getContentResolver().update(LocalContentProvider.URI_GAME, values, selection, selectionArgs);
        
        if (result < 1) {
            Log.e(TAG, "saveReachedStatus() | No row updated! Result: " + result);
            Log.e(TAG, "saveReachedStatus() | Point: " + atPoint);
            Toast.makeText(context, "An error occured during the save of the reached time!", Toast.LENGTH_LONG).show();
        }
        
        atPoint.setNotReached();
        
        // Unset Parent too
        if (atPoint.isChild()) {
            selectionArgs = new String[]{atPoint.getParentId().toString()};
            result = context.getContentResolver().update(LocalContentProvider.URI_GAME, values, selection, selectionArgs);
            
            if (result < 1) {
                Log.e(TAG, "saveReachedStatus() | No PARENT row updated! Result: " + result);
                Log.e(TAG, "saveReachedStatus() | PARENT ID: " + atPoint.getParentId());
                Toast.makeText(context, "An error occured during the save of the reached time!", Toast.LENGTH_LONG).show();
            }
        }
        
        return atPoint;
    }
    
    // Updates the AtPoint field reachedPoint with the current timestamp
    public static AtPoint saveReachedStatus(AtPoint atPoint, Context context) {
        Log.d(TAG, "saveReachedStatus()");
        
        // Saving the status
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_GAME_STATUS, MySQLiteHelper.POINT_STATUS_REACHED);
        values.put(MySQLiteHelper.COLUMN_GAME_TIME_REACHED, Utils.getDateTime(null));
        String selection = "(" + MySQLiteHelper.COLUMN_GAME_POINT_ID + " = ?)";
        String[] selectionArgs = new String[]{atPoint.getIdPoint().toString()};
        
        Integer result = context.getContentResolver().update(LocalContentProvider.URI_GAME, values, selection, selectionArgs);
        
        if (result < 1) {
            Log.e(TAG, "saveReachedStatus() | No row updated! Result: " + result);
            Log.e(TAG, "saveReachedStatus() | Point: " + atPoint);
            Toast.makeText(context, "An error occured during the save of the reached time!", Toast.LENGTH_LONG).show();
        }
        
        atPoint.setReached(Utils.getDateTime(null));
        
        return atPoint;
    }
    
    // Updates the AtPoint field completedPoint with the current timestamp
    public static AtPoint saveCompletedStatus(AtPoint atPoint, Context context) {
        Log.d(TAG, "saveCompletedStatus() " + atPoint.getName());
        
        // Saving the status & timestamp
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_GAME_STATUS, MySQLiteHelper.POINT_STATUS_COMPLETED);
        values.put(MySQLiteHelper.COLUMN_GAME_TIME_COMPLETED, Utils.getDateTime(null));
        String selection = "(" + MySQLiteHelper.COLUMN_GAME_POINT_ID + " = ?)";
        String[] selectionArgs = new String[]{atPoint.getIdPoint().toString()};
        
        Integer result = context.getContentResolver().update(LocalContentProvider.URI_GAME, values, selection, selectionArgs);
        
        if (result < 1) {
            Log.e(TAG, "saveReachedStatus() | No row updated! Result: " + result);
            Log.e(TAG, "saveReachedStatus() | Point: " + atPoint);
            Toast.makeText(context, "An error occured during the save of the reached time!", Toast.LENGTH_LONG).show();
        }
        
        atPoint.setCompleted(Utils.getDateTime(null));
        
        return atPoint;
    }
    
    public static void setParentCompleted(AtPoint atPoint, Context context) {
        Log.d(TAG, "setParentCompleted() " + atPoint.getName());
        if (atPoint.isChild()) {
            AtPoint parent = AtUtils.getPoint(atPoint.getParentId(), context);
            AtUtils.saveCompletedStatus(parent, context);
        }
    }
    
    // Returns if the player reached the next point in his list or not
    public static AtPoint playerReachedPoint(Location location, Context context) {
        Log.d(TAG, "playerReachedPoint()");
        
        List<AtPoint> remainingPoints = AtUtils.getAllPoints(MySQLiteHelper.POINT_STATUS_VOID, MySQLiteHelper.POINT_STATUS_VOID, context);
        AtPoint reachedPoint = new AtPoint();
        
        final int size = remainingPoints.size();
        for (int i = 0; i < size; i++) {
            if (checkIfLocationIsPoint(location, remainingPoints.get(i))) {
                reachedPoint = new AtPoint(remainingPoints.get(i));
                break;
            }
        }
        
        return reachedPoint;
    }
    
    // Check if a couple of coordinates are (close to) a Point!
    public static boolean checkIfLocationIsPoint(Location location, AtPoint atPoint) {
        Log.d(TAG, "checkIfLocationIsPoint");
        
        double percentageRadius = (int)(atPoint.getRadius()*(LocationService.getToleranceRadius()/100.0f));
        
        boolean isPoint = false;
        double toleranceRadius = percentageRadius + atPoint.getRadius();
        double minimumAccuracy = LocationService.getMinimumAccuracy();
        double locationAccuracy = 0;
        
        // Minimum precision meters required to enable the check
        if (location.hasAccuracy() && location.getAccuracy() <= minimumAccuracy) {
            locationAccuracy = location.getAccuracy();
        } else {
            // Since there is no accuracy it means the localization won't be as precise as it should be.
            toleranceRadius += minimumAccuracy;
        }
        
        
        double nextLat = atPoint.getLat();
        double nextLon = atPoint.getLon();
        double lat = location.getLatitude();
        double lon = location.getLongitude();
        float[] results = new float[1];
        
        // Calculating the distance with a class Location method
        location.distanceBetween(lat, lon, nextLat, nextLon, results);
        double distance = results[0];
        
        // Updating the current distance from the Goal
        //LocationService.setDistanceFromGoal(distance);
        
        // Logging  - Only for 1 Point or i go CRAZY
        
        /*
            Prato Della Valle: 45.398436, 11.876532
            
            Punto > 100m 45.397682, 11.878960
            Punto >= 100m  45.397926, 11.877744
            Punto < 100m 45.398619, 11.877774
            
            Colonna Segno Zod: 45.407837, 11.873059
            
            Punto > 5m: 45.407751, 11.873092
            Punto >= 5m: 45.407878, 11.873215
            Punto < 5m:  45.407819, 11.873115
        
        
        */
        if( Utils.DEBUG_ACTIVE ) {
            // Better to filter for just one point, or it's kinda spammy
            if (atPoint.getIdPoint() == 24) {
                Log.d(TAG, "\nDEBUG_LOCALIZATION");
                Log.d(TAG, "\nDEBUG_LOCALIZATION Coordinates Player: " + lat + ", " + lon);
                Log.d(TAG, "\nDEBUG_LOCALIZATION Coordinates Point: " + nextLat + ", " + nextLon);
                Log.d(TAG, "\nDEBUG_LOCALIZATION Accuracy: " + locationAccuracy);
                Log.d(TAG, "\nDEBUG_LOCALIZATION Service min.acc = " + LocationService.getMinimumAccuracy());
                Log.d(TAG, "\nDEBUG_LOCALIZATION Tolerance Service: " + LocationService.getToleranceRadius());
                Log.d(TAG, "\nDEBUG_LOCALIZATION Tolerance Point: " + atPoint.getRadius());
                Log.d(TAG, "\nDEBUG_LOCALIZATION AddedRadius%: " + percentageRadius);
                Log.d(TAG, "\nDEBUG_LOCALIZATION ToleranceRadius: " + toleranceRadius);
                Log.d(TAG, "\nDEBUG_LOCALIZATION Distance R0 " + results[0]);
                Log.d(TAG, "\nDEBUG_LOCALIZATION toleranceRadius > distance?");
            }
        }
    
    
        if (distance < toleranceRadius) {
            Log.i(TAG, "Point reached!!");
            isPoint = true;
            if( Utils.DEBUG_ACTIVE ) {
                Log.d(TAG, "\nDEBUG_LOCALIZATION YEP");
            }
        }
        
        return isPoint;
    }
}
