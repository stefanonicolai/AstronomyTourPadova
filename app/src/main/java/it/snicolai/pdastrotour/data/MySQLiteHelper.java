package it.snicolai.pdastrotour.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;

import it.snicolai.pdastrotour.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MySQLiteHelper extends SQLiteOpenHelper {
    
    private static final String TAG = "MySQLiteHelper";
    
    private Context context;
    
    private static final String DATABASE_NAME = "pdastrotour.db";
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 2;
    
    MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }
    
    // ---------------------------------------------------------------------------------------------
    // STATUS
    // ---------------------------------------------------------------------------------------------
    
    // Status Point
    public static final String POINT_STATUS_VOID = "void";
    public static final String POINT_STATUS_REACHED = "reached";
    public static final String POINT_STATUS_COMPLETED = "completed";
    
    // Status Achievement
    public static final String ACHIEVEMENT_STATUS_VOID = "void";
    public static final String ACHIEVEMENT_STATUS_COPPER = "copper";
    public static final String ACHIEVEMENT_STATUS_SILVER = "silver";
    public static final String ACHIEVEMENT_STATUS_GOLD = "gold";
    
    // Event
    public static final String POINT_EVENT_EXTRAS = "extras";
    public static final String POINT_EVENT_QA = "qa";
    public static final String POINT_EVENT_REACH = "reach";
    
    // Group Concat Columns
    public static final String GROUP_CONCAT_CATEGORY = "categories";
    public static final String GROUP_CONCAT_POINT = "points";
    public static final String GROUP_CONCAT_DIVIDER = ", ";
    
    // ---------------------------------------------------------------------------------------------
    // TABLES
    // ---------------------------------------------------------------------------------------------
    
    // Point Table
    public static final String TABLE_POINT = "point";
    public static final String COLUMN_POINT_ID = "id_point";
    public static final String COLUMN_POINT_POS = "pos";
    public static final String COLUMN_POINT_IMAGE = "image";
    public static final String COLUMN_POINT_LAT = "lat";
    public static final String COLUMN_POINT_LON = "lon";
    public static final String COLUMN_POINT_RADIUS = "radius";
    
    // PointInfo Table
    public static final String TABLE_POINTINFO = "pointinfo";
    public static final String COLUMN_POINTINFO_ID = "id_pointinfo";
    public static final String COLUMN_POINTINFO_POINT_ID = "fk_id_point";
    public static final String COLUMN_POINTINFO_NAME = "name";
    public static final String COLUMN_POINTINFO_TITLE = "title";
    public static final String COLUMN_POINTINFO_ADDRESS = "address";
    public static final String COLUMN_POINTINFO_DESCRIPTION = "description";
    public static final String COLUMN_POINTINFO_EVENT = "event";
    public static final String COLUMN_POINTINFO_EXTRAS = "extras";
    public static final String COLUMN_POINTINFO_QA_ID = "fk_qa_id";
    public static final String COLUMN_POINTINFO_REACHPOINT_ID = "fk_reachpoint_id";
    public static final String COLUMN_POINTINFO_REACH_TITLE = "reach_title";
    public static final String COLUMN_POINTINFO_REACH_DESCRIPTION = "reach_description";
    
    // Game
    public static final String TABLE_GAME = "game";
    public static final String COLUMN_GAME_ID = "id_game";
    public static final String COLUMN_GAME_POINT_ID = "fk_id_point";
    public static final String COLUMN_GAME_STATUS = "status";
    public static final String COLUMN_GAME_TIME_REACHED = "time_reached";
    public static final String COLUMN_GAME_TIME_COMPLETED = "time_completed";
    
    // Q & A Table
    public static final String TABLE_QA = "qa";
    public static final String COLUMN_QA_ID = "id_qa";
    public static final String COLUMN_QA_QUESTION = "question";
    public static final String COLUMN_QA_ANSWER_1 = "answer_1";
    public static final String COLUMN_QA_ANSWER_2 = "answer_2";
    public static final String COLUMN_QA_ANSWER_3 = "answer_3";
    public static final String COLUMN_QA_ANSWER_4 = "answer_4";
    public static final String COLUMN_QA_POS_CORRECT = "pos_correct";
    
    // Reach Point Table
    public static final String TABLE_REACHPOINT = "reach_point";
    public static final String COLUMN_REACHPOINT_ID = "id_reachpoint";
    public static final String COLUMN_REACHPOINT_PARENT_ID = "fk_parent_id";
    
    // Category
    public static final String TABLE_CATEGORY = "category";
    public static final String COLUMN_CATEGORY_ID = "id_category";
    public static final String COLUMN_CATEGORY_NAME = "name";
    
    // CatPoint
    public static final String TABLE_CATPOINT = "catpoint";
    public static final String COLUMN_CATPOINT_ID = "id_catpoint";
    public static final String COLUMN_CATPOINT_POINT_ID = "fk_id_point";
    public static final String COLUMN_CATPOINT_CATEGORY_ID = "fk_id_category";
    
    // Achievement
    public static final String TABLE_ACHIEVEMENT = "achievement";
    public static final String COLUMN_ACHIEVEMENT_ID = "id_achievement";
    public static final String COLUMN_ACHIEVEMENT_NAME = "name";
    
    // AchiPoint
    public static final String TABLE_ACHIPOINT = "achipoint";
    public static final String COLUMN_ACHIPOINT_ID = "id_achipoint";
    public static final String COLUMN_ACHIPOINT_POINT_ID = "fk_id_point";
    public static final String COLUMN_ACHIPOINT_ACHIEVMENT_ID = "fk_id_achievement";
    
    
    // ---------------------------------------------------------------------------------------------
    // CREATE TABLES
    // ---------------------------------------------------------------------------------------------
    
    // Point Table Creation
    private static final String TABLE_POINT_CREATION = "CREATE TABLE IF NOT EXISTS "
        + TABLE_POINT + " ("
        //+ COLUMN_POINT_ID + " integer primary key autoincrement, " // FIXME: hardcoded data
        + COLUMN_POINT_ID + " integer primary key, "
        + COLUMN_POINT_POS + " integer(4), "
        + COLUMN_POINT_IMAGE + " varchar(40), "
        + COLUMN_POINT_LAT + " numeric(11,8), "
        + COLUMN_POINT_LON + " numeric(11,8), "
        + COLUMN_POINT_RADIUS + " numeric(3,2) "
        + ");";
    private static final String DROP_TABLE_POINT = "DROP TABLE IF EXISTS " + TABLE_POINT;
    
    
    // PointInfo Table Creation
    private static final String TABLE_POINTINFO_CREATION = "CREATE TABLE IF NOT EXISTS "
        + TABLE_POINTINFO + " ("
        + COLUMN_POINTINFO_ID + " integer primary key autoincrement, "
        + COLUMN_POINTINFO_POINT_ID + " integer, "
        + COLUMN_POINTINFO_NAME + " varchar(40), "
        + COLUMN_POINTINFO_TITLE + " varchar(40), "
        + COLUMN_POINTINFO_ADDRESS + " varchar(60), "
        + COLUMN_POINTINFO_DESCRIPTION + " varchar(240), "
        + COLUMN_POINTINFO_EVENT + " varchar(20), "
        + COLUMN_POINTINFO_EXTRAS + " varchar(240), "
        + COLUMN_POINTINFO_QA_ID + " integer, "
        + COLUMN_POINTINFO_REACHPOINT_ID + " integer, "
        + COLUMN_POINTINFO_REACH_TITLE + " varchar(100), "
        + COLUMN_POINTINFO_REACH_DESCRIPTION + " varchar(250) "
        + ");";
    private static final String DROP_TABLE_POINTINFO = "DROP TABLE IF EXISTS " + TABLE_POINTINFO;
    
    
    // Game Table Creation
    private static final String TABLE_GAME_CREATION = "CREATE TABLE IF NOT EXISTS "
        + TABLE_GAME + " ("
        + COLUMN_GAME_ID + " integer primary key autoincrement, "
        + COLUMN_GAME_POINT_ID + " integer, "
        + COLUMN_GAME_STATUS + " varchar(20), "
        + COLUMN_GAME_TIME_REACHED + " datetime, "
        + COLUMN_GAME_TIME_COMPLETED + " datetime "
        + ");";
    private static final String INDEX_GAME_STATUS = "CREATE INDEX " + COLUMN_GAME_STATUS + "_idx ON " + TABLE_GAME + " (" + COLUMN_GAME_STATUS + ");";
    private static final String INDEX_GAME_POINT_ID = "CREATE INDEX " + COLUMN_GAME_POINT_ID + "_idx ON " + TABLE_GAME + " (" + COLUMN_GAME_POINT_ID + ");";
    private static final String DROP_TABLE_GAME = "DROP TABLE IF EXISTS " + TABLE_GAME;
    
    
    // Q & A Table Creation
    private static final String TABLE_QA_CREATION = "CREATE TABLE IF NOT EXISTS "
        + TABLE_QA + " ("
        //+ COLUMN_QA_ID + " integer primary key autoincrement, " // FIXME: hardcoded data
        + COLUMN_QA_ID + " integer primary key, "
        + COLUMN_QA_QUESTION + " varchar(40), "
        + COLUMN_QA_ANSWER_1 + " varchar(100), "
        + COLUMN_QA_ANSWER_2 + " varchar(100), "
        + COLUMN_QA_ANSWER_3 + " varchar(100), "
        + COLUMN_QA_ANSWER_4 + " varchar(100), "
        + COLUMN_QA_POS_CORRECT + " integer(1) "
        + ");";
    private static final String DROP_TABLE_QA = "DROP TABLE IF EXISTS " + TABLE_QA;
    
    
    // ReachPoint Table Creation
    private static final String TABLE_REACHPOINT_CREATION = "CREATE TABLE IF NOT EXISTS "
        + TABLE_REACHPOINT + " ("
        //+ COLUMN_REACHPOINT_ID + " integer primary key autoincrement, " // FIXME: hardcoded data
        + COLUMN_REACHPOINT_ID + " integer primary key, "
        + COLUMN_REACHPOINT_PARENT_ID + " integer "
        + ");";
    private static final String DROP_TABLE_REACHPOINT = "DROP TABLE IF EXISTS " + TABLE_REACHPOINT;
    
    
    // Category Table Creation
    private static final String TABLE_CATEGORY_CREATION = "CREATE TABLE IF NOT EXISTS "
        + TABLE_CATEGORY + " ("
        + COLUMN_CATEGORY_ID + " integer primary key autoincrement, "
        + COLUMN_CATEGORY_NAME + " varchar(20) "
        + ");";
    private static final String DROP_TABLE_CATEGORY = "DROP TABLE IF EXISTS " + TABLE_CATEGORY;
    
    
    // CatPoint Table Creation
    private static final String TABLE_CATPOINT_CREATION = "CREATE TABLE IF NOT EXISTS "
        + TABLE_CATPOINT + " ("
        + COLUMN_CATPOINT_ID + " integer primary key autoincrement, "
        + COLUMN_CATPOINT_POINT_ID + " integer, "
        + COLUMN_CATPOINT_CATEGORY_ID + " integer "
        + ");";
    private static final String DROP_TABLE_CATPOINT = "DROP TABLE IF EXISTS " + TABLE_CATPOINT;
    
    
    // Achievement Table Creation
    private static final String TABLE_ACHIEVEMENT_CREATION = "CREATE TABLE IF NOT EXISTS "
        + TABLE_ACHIEVEMENT + " ("
        //+ COLUMN_ACHIEVEMENT_ID + " integer primary key autoincrement, " // FIXME: hardcoded data
        + COLUMN_ACHIEVEMENT_ID + " integer primary key, "
        + COLUMN_ACHIEVEMENT_NAME + " varchar(40) "
        + ");";
    private static final String DROP_TABLE_ACHIEVEMENT = "DROP TABLE IF EXISTS " + TABLE_ACHIEVEMENT;
    
    
    // AchiPoint Table Creation
    private static final String TABLE_ACHIPOINT_CREATION = "CREATE TABLE IF NOT EXISTS "
        + TABLE_ACHIPOINT + " ("
        + COLUMN_ACHIPOINT_ID + " integer primary key autoincrement, "
        + COLUMN_ACHIPOINT_POINT_ID + " integer, "
        + COLUMN_ACHIPOINT_ACHIEVMENT_ID + " integer "
        + ");";
    private static final String DROP_TABLE_ACHIPOINT = "DROP TABLE IF EXISTS " + TABLE_ACHIPOINT;
    
    
    // ---------------------------------------------------------------------------------------------
    // CREATE VIEWS
    // ---------------------------------------------------------------------------------------------
    
    // Point View
    public static final String VIEW_POINT = "view_point";
    
    private static final String VIEW_POINT_CREATION = "CREATE VIEW "
        + VIEW_POINT + " AS "
        + "SELECT "
        // Fields
        + TABLE_POINT + "." + COLUMN_POINT_ID + ", "
        + TABLE_POINT + "." + COLUMN_POINT_POS + ", "
        + TABLE_POINT + "." + COLUMN_POINT_IMAGE + ", "
        + TABLE_POINT + "." + COLUMN_POINT_LAT + ", "
        + TABLE_POINT + "." + COLUMN_POINT_LON + ", "
        + TABLE_POINT + "." + COLUMN_POINT_RADIUS + ", "
        + TABLE_POINTINFO + "." + COLUMN_POINTINFO_NAME + ", "
        + TABLE_POINTINFO + "." + COLUMN_POINTINFO_TITLE + ", "
        + TABLE_POINTINFO + "." + COLUMN_POINTINFO_ADDRESS + ", "
        + TABLE_POINTINFO + "." + COLUMN_POINTINFO_DESCRIPTION + ", "
        + TABLE_POINTINFO + "." + COLUMN_POINTINFO_EVENT + ", "
        + TABLE_POINTINFO + "." + COLUMN_POINTINFO_EXTRAS + ", "
        + TABLE_GAME + "." + COLUMN_GAME_STATUS + ", "
        + TABLE_GAME + "." + COLUMN_GAME_TIME_REACHED + ", "
        + TABLE_GAME + "." + COLUMN_GAME_TIME_COMPLETED + ", "
        + TABLE_QA + "." + COLUMN_QA_QUESTION + ", "
        + TABLE_QA + "." + COLUMN_QA_ANSWER_1 + ", "
        + TABLE_QA + "." + COLUMN_QA_ANSWER_2 + ", "
        + TABLE_QA + "." + COLUMN_QA_ANSWER_3 + ", "
        + TABLE_QA + "." + COLUMN_QA_ANSWER_4 + ", "
        + TABLE_QA + "." + COLUMN_QA_POS_CORRECT + ", "
        + TABLE_REACHPOINT + "." + COLUMN_REACHPOINT_PARENT_ID + ", "
        + TABLE_POINTINFO + "." + COLUMN_POINTINFO_REACH_TITLE + ", "
        + TABLE_POINTINFO + "." + COLUMN_POINTINFO_REACH_DESCRIPTION + ", "
        + "GROUP_CONCAT(" + TABLE_CATEGORY + "." + COLUMN_CATEGORY_NAME + ", \"" + GROUP_CONCAT_DIVIDER + "\") AS " + GROUP_CONCAT_CATEGORY + " "
        // Join
        + "FROM " + TABLE_POINT + " "
        + "LEFT JOIN " + TABLE_POINTINFO + " "
        + "ON " + TABLE_POINT + "." + COLUMN_POINT_ID + " = " + TABLE_POINTINFO + "." + COLUMN_POINTINFO_POINT_ID + " "
        + "LEFT JOIN " + TABLE_GAME + " "
        + "ON " + TABLE_POINT + "." + COLUMN_POINT_ID + " = " + TABLE_GAME + "." + COLUMN_GAME_POINT_ID + " "
        + "LEFT JOIN " + TABLE_QA + " "
        + "ON " + TABLE_POINTINFO + "." + COLUMN_POINTINFO_QA_ID + " = " + TABLE_QA + "." + COLUMN_QA_ID + " "
        + "LEFT JOIN " + TABLE_REACHPOINT + " "
        + "ON " + TABLE_POINTINFO + "." + COLUMN_POINTINFO_REACHPOINT_ID + " = " + TABLE_REACHPOINT + "." + COLUMN_REACHPOINT_ID + " "
        // MultiJoin on Categories
        + "LEFT JOIN " + TABLE_CATPOINT + " "
        + "ON " + TABLE_POINT + "." + COLUMN_POINT_ID + " = " + TABLE_CATPOINT + "." + COLUMN_CATPOINT_POINT_ID + " "
        + "LEFT JOIN " + TABLE_CATEGORY + " "
        + "ON " + TABLE_CATPOINT + "." + COLUMN_CATPOINT_CATEGORY_ID + " = " + TABLE_CATEGORY + "." + COLUMN_CATEGORY_ID + " "
        // GroupBy
        + "GROUP BY " + COLUMN_POINT_ID + " "
        // Order
        + "ORDER BY " + TABLE_POINT + "." + COLUMN_POINT_POS + " ASC"
        + ";";
    private static final String DROP_VIEW_POINT = "DROP VIEW IF EXISTS " + VIEW_POINT;
    
    // Category View
    public static final String VIEW_CATEGORIES = "view_categories";
    
    private static final String VIEW_CATEGORIES_CREATION = "CREATE VIEW "
        + VIEW_CATEGORIES + " AS "
        + "SELECT "
        // Fields
        + TABLE_CATEGORY + "." + COLUMN_CATEGORY_ID + ", "
        + TABLE_CATEGORY + "." + COLUMN_CATEGORY_NAME + ", "
        + "GROUP_CONCAT(" + TABLE_POINT + "." + COLUMN_POINT_ID + ", \"" + GROUP_CONCAT_DIVIDER + "\") AS " + GROUP_CONCAT_POINT + " "
        // MultiJoin on Point
        + "FROM " + TABLE_CATEGORY + " "
        + "LEFT JOIN " + TABLE_CATPOINT + " "
        + "ON " + TABLE_CATEGORY + "." + COLUMN_CATEGORY_ID + " = " + TABLE_CATPOINT + "." + COLUMN_CATPOINT_CATEGORY_ID + " "
        + "LEFT JOIN " + TABLE_POINT + " "
        + "ON " + TABLE_CATPOINT + "." + COLUMN_CATPOINT_POINT_ID + " = " + TABLE_POINT + "." + COLUMN_POINT_ID + " "
        // GroupBy
        + "GROUP BY " + COLUMN_CATEGORY_ID + " "
        // Order
        + "ORDER BY " + TABLE_CATEGORY + "." + COLUMN_CATEGORY_ID + " ASC"
        + ";";
    private static final String DROP_VIEW_CATEGORIES = "DROP VIEW IF EXISTS " + VIEW_CATEGORIES;
    
    
    // Achievement View
    public static final String VIEW_ACHIEVEMENTS = "view_achievements";
    
    private static final String VIEW_ACHIEVEMENTS_CREATION = "CREATE VIEW "
        + VIEW_ACHIEVEMENTS + " AS "
        + "SELECT "
        // Fields
        + TABLE_ACHIEVEMENT + "." + COLUMN_ACHIEVEMENT_ID + ", "
        + TABLE_ACHIEVEMENT + "." + COLUMN_ACHIEVEMENT_NAME + ", "
        + "GROUP_CONCAT(" + TABLE_POINT + "." + COLUMN_POINT_ID + ", \"" + GROUP_CONCAT_DIVIDER + "\") AS " + GROUP_CONCAT_POINT + " "
        // MultiJoin on Point
        + "FROM " + TABLE_ACHIEVEMENT + " "
        + "LEFT JOIN " + TABLE_ACHIPOINT + " "
        + "ON " + TABLE_ACHIEVEMENT + "." + COLUMN_ACHIEVEMENT_ID + " = " + TABLE_ACHIPOINT + "." + COLUMN_ACHIPOINT_ACHIEVMENT_ID + " "
        + "LEFT JOIN " + TABLE_POINT + " "
        + "ON " + TABLE_ACHIPOINT + "." + COLUMN_ACHIPOINT_POINT_ID + " = " + TABLE_POINT + "." + COLUMN_POINT_ID + " "
        // GroupBy
        + "GROUP BY " + COLUMN_ACHIEVEMENT_ID + " "
        // Order
        + "ORDER BY " + TABLE_ACHIEVEMENT + "." + COLUMN_ACHIEVEMENT_ID + " ASC"
        + ";";
    private static final String DROP_VIEW_ACHIEVEMENTS = "DROP VIEW IF EXISTS " + VIEW_ACHIEVEMENTS;
    
    
    // ---------------------------------------------------------------------------------------------
    // METHODS
    // ---------------------------------------------------------------------------------------------
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        //Log.d(TAG, "SPINNER: onCreate()");
        
        // Create Tables
        //Log.d(TAG, " onCreate() -> \n" + TABLE_POINT_CREATION);
        //Log.d(TAG, " onCreate() -> \n" + TABLE_POINTINFO_CREATION);
        //Log.d(TAG, " onCreate() -> \n" + TABLE_GAME_CREATION);
        //Log.d(TAG, " onCreate() -> \n" + TABLE_QA_CREATION);
        //Log.d(TAG, " onCreate() -> \n" + TABLE_REACHPOINT_CREATION);
        //Log.d(TAG, " onCreate() -> \n" + TABLE_CATEGORY_CREATION);
        //Log.d(TAG, " onCreate() -> \n" + TABLE_CATPOINT_CREATION);
        //Log.d(TAG, " onCreate() -> \n" + TABLE_ACHIEVEMENT_CREATION);
        //Log.d(TAG, " onCreate() -> \n" + TABLE_ACHIPOINT_CREATION);
        
        db.execSQL(TABLE_POINT_CREATION);
        db.execSQL(TABLE_POINTINFO_CREATION);
        db.execSQL(TABLE_GAME_CREATION);
        db.execSQL(TABLE_QA_CREATION);
        db.execSQL(TABLE_REACHPOINT_CREATION);
        db.execSQL(TABLE_CATEGORY_CREATION);
        db.execSQL(TABLE_CATPOINT_CREATION);
        db.execSQL(TABLE_ACHIEVEMENT_CREATION);
        db.execSQL(TABLE_ACHIPOINT_CREATION);
        
        // Create Indexes // FIXME: needed? Where can i best place them?
        //Log.d(TAG, " onCreate() -> \n" + INDEX_GAME_STATUS);
        //Log.d(TAG, " onCreate() -> \n" + INDEX_GAME_POINT_ID);
        
        db.execSQL(INDEX_GAME_STATUS);
        db.execSQL(INDEX_GAME_POINT_ID);
        
        // Create Views
        //Log.d(TAG, " onCreate() -> \n" + VIEW_POINT_CREATION);
        //Log.d(TAG, " onCreate() -> \n" + VIEW_CATEGORIES_CREATION);
        //Log.d(TAG, " onCreate() -> \n" + VIEW_ACHIEVEMENTS_CREATION);
        
        db.execSQL(VIEW_POINT_CREATION);
        db.execSQL(VIEW_CATEGORIES_CREATION);
        db.execSQL(VIEW_ACHIEVEMENTS_CREATION);
        
        //
        // Insert default datas
        //
        
        // Getting the remote datas trough RetroFit
        RetrofitApi retrofitApi = RetrofitApiClient.getClient(false).create(RetrofitApi.class);
        
        //
        // Points
        //
        Call<RetrofitResponsePoint> callPoint = retrofitApi.retreiveAtPoints();
        callPoint.enqueue(new Callback<RetrofitResponsePoint>() {
            
            @Override
            public void onResponse(Call<RetrofitResponsePoint> callPoint, Response<RetrofitResponsePoint> response) {
                ////Log.d(TAG, "retrofitResponse()");
                
                RetrofitResponsePoint retrofitResponse = response.body();
                
                ArrayList<RetrofitResponsePoint.Data> points = retrofitResponse.getDatas();
                //Log.d(TAG, "callPoint: " + points);
                
                for (RetrofitResponsePoint.Data data : points) {
                    ContentValues values = new ContentValues();
                    values.put(MySQLiteHelper.COLUMN_POINT_ID, data.getIdPoint());
                    values.put(MySQLiteHelper.COLUMN_POINT_POS, data.getPos());
                    values.put(MySQLiteHelper.COLUMN_POINT_IMAGE, data.getImage());
                    values.put(MySQLiteHelper.COLUMN_POINT_LAT, data.getLat());
                    values.put(MySQLiteHelper.COLUMN_POINT_LON, data.getLon());
                    values.put(MySQLiteHelper.COLUMN_POINT_RADIUS, data.getRadius());
                    
                    Uri uselessReturnOverrideFunction = context.getContentResolver().insert(
                        LocalContentProvider.URI_POINT,
                        values
                    );
                }
                
                Utils.completedInsert++;
            }
            
            @Override
            public void onFailure(Call<RetrofitResponsePoint> callPoint, Throwable t) {
                Log.e(TAG, "RESPONSE onFailure: " + t.toString());
            }
        });
        
        //
        // PointInfos
        //
        Call<RetrofitResponsePointInfo> callPointInfo = retrofitApi.retreiveAtPointInfos();
        callPointInfo.enqueue(new Callback<RetrofitResponsePointInfo>() {
            
            @Override
            public void onResponse(Call<RetrofitResponsePointInfo> callPointInfo, Response<RetrofitResponsePointInfo> response) {
                ////Log.d(TAG, "retrofitResponse()");
                
                RetrofitResponsePointInfo retrofitResponse = response.body();
                
                ArrayList<RetrofitResponsePointInfo.Data> points = retrofitResponse.getDatas();
                //Log.d(TAG, "callPointInfo: " + points);
                
                for (RetrofitResponsePointInfo.Data data : points) {
                    ContentValues values = new ContentValues();
                    
                    values.put(MySQLiteHelper.COLUMN_POINTINFO_POINT_ID, data.getFkIdPoint());
                    values.put(MySQLiteHelper.COLUMN_POINTINFO_NAME, data.getName());
                    values.put(MySQLiteHelper.COLUMN_POINTINFO_TITLE, data.getTitle());
                    values.put(MySQLiteHelper.COLUMN_POINTINFO_ADDRESS, data.getAddress());
                    values.put(MySQLiteHelper.COLUMN_POINTINFO_DESCRIPTION, data.getDescription());
                    values.put(MySQLiteHelper.COLUMN_POINTINFO_EVENT, data.getEvent());
                    values.put(MySQLiteHelper.COLUMN_POINTINFO_EXTRAS, data.getExtras());
                    values.put(MySQLiteHelper.COLUMN_POINTINFO_QA_ID, data.getFkQaId());
                    values.put(MySQLiteHelper.COLUMN_POINTINFO_REACHPOINT_ID, data.getFkReachpointId());
                    values.put(MySQLiteHelper.COLUMN_POINTINFO_REACH_TITLE, data.getReachTitle());
                    values.put(MySQLiteHelper.COLUMN_POINTINFO_REACH_DESCRIPTION, data.getReachDescription());
                    
                    Uri uselessReturnOverrideFunction = context.getContentResolver().insert(
                        LocalContentProvider.URI_POINTINFO,
                        values
                    );
                }
    
                Utils.completedInsert++;
            }
            
            @Override
            public void onFailure(Call<RetrofitResponsePointInfo> callPointInfo, Throwable t) {
                Log.e(TAG, "RESPONSE onFailure: " + t.toString());
            }
        });
        
        //
        // Game
        //
        Call<RetrofitResponseGame> callGame = retrofitApi.retreiveAtGames();
        callGame.enqueue(new Callback<RetrofitResponseGame>() {
            
            @Override
            public void onResponse(Call<RetrofitResponseGame> callGame, Response<RetrofitResponseGame> response) {
                ////Log.d(TAG, "retrofitResponse()");
                
                RetrofitResponseGame retrofitResponse = response.body();
                
                ArrayList<RetrofitResponseGame.Data> points = retrofitResponse.getDatas();
                //Log.d(TAG, "callGame: " + points);
                
                for (RetrofitResponseGame.Data data : points) {
                    ContentValues values = new ContentValues();
                    
                    values.put(MySQLiteHelper.COLUMN_GAME_POINT_ID, data.getFkIdPoint());
                    values.put(MySQLiteHelper.COLUMN_GAME_STATUS, data.getStatus());
                    values.put(MySQLiteHelper.COLUMN_GAME_TIME_REACHED, data.getTimeReached());
                    values.put(MySQLiteHelper.COLUMN_GAME_TIME_COMPLETED, data.getTimeCompleted());
                    
                    Uri uselessReturnOverrideFunction = context.getContentResolver().insert(
                        LocalContentProvider.URI_GAME,
                        values
                    );
                }
    
                Utils.completedInsert++;
            }
            
            @Override
            public void onFailure(Call<RetrofitResponseGame> callGame, Throwable t) {
                Log.e(TAG, "RESPONSE onFailure: " + t.toString());
            }
        });
        
        //
        // Qa
        //
        Call<RetrofitResponseQa> callQa = retrofitApi.retreiveAtQas();
        callQa.enqueue(new Callback<RetrofitResponseQa>() {
            
            @Override
            public void onResponse(Call<RetrofitResponseQa> callQa, Response<RetrofitResponseQa> response) {
                ////Log.d(TAG, "retrofitResponse()");
    
                RetrofitResponseQa retrofitResponse = response.body();
                
                ArrayList<RetrofitResponseQa.Data> points = retrofitResponse.getDatas();
                //Log.d(TAG, "callQa: " + points);
                
                for (RetrofitResponseQa.Data data : points) {
                    ContentValues values = new ContentValues();
                    
                    values.put(MySQLiteHelper.COLUMN_QA_ID, data.getIdQa());
                    values.put(MySQLiteHelper.COLUMN_QA_QUESTION, data.getQuestion());
                    values.put(MySQLiteHelper.COLUMN_QA_ANSWER_1, data.getAnswer1());
                    values.put(MySQLiteHelper.COLUMN_QA_ANSWER_2, data.getAnswer2());
                    values.put(MySQLiteHelper.COLUMN_QA_ANSWER_3, data.getAnswer3());
                    values.put(MySQLiteHelper.COLUMN_QA_ANSWER_4, data.getAnswer4());
                    values.put(MySQLiteHelper.COLUMN_QA_POS_CORRECT, data.getPosCorrect());
                    
                    Uri uselessReturnOverrideFunction = context.getContentResolver().insert(
                        LocalContentProvider.URI_QA,
                        values
                    );
                }
    
                Utils.completedInsert++;
            }
            
            @Override
            public void onFailure(Call<RetrofitResponseQa> callQa, Throwable t) {
                Log.e(TAG, "RESPONSE onFailure: " + t.toString());
            }
        });
        
        //
        // ReachPoint
        //
        Call<RetrofitResponseReachPoint> callReachPoint = retrofitApi.retreiveAtReachPoints();
        callReachPoint.enqueue(new Callback<RetrofitResponseReachPoint>() {
            
            @Override
            public void onResponse(Call<RetrofitResponseReachPoint> callReachPoint, Response<RetrofitResponseReachPoint> response) {
                ////Log.d(TAG, "retrofitResponse()");
    
                RetrofitResponseReachPoint retrofitResponse = response.body();
                
                ArrayList<RetrofitResponseReachPoint.Data> points = retrofitResponse.getDatas();
                //Log.d(TAG, "callReachPoint: " + points);
                
                for (RetrofitResponseReachPoint.Data data : points) {
                    ContentValues values = new ContentValues();
                    
                    values.put(MySQLiteHelper.COLUMN_REACHPOINT_ID, data.getIdReachpoint());
                    values.put(MySQLiteHelper.COLUMN_REACHPOINT_PARENT_ID, data.getFkParentId());
                    
                    Uri uselessReturnOverrideFunction = context.getContentResolver().insert(
                        LocalContentProvider.URI_REACHPOINT,
                        values
                    );
                }
    
                Utils.completedInsert++;
            }
            
            @Override
            public void onFailure(Call<RetrofitResponseReachPoint> callReachPoint, Throwable t) {
                Log.e(TAG, "RESPONSE onFailure: " + t.toString());
            }
        });
        
        //
        // Categories
        //
        Call<RetrofitResponseCategory> callCategories = retrofitApi.retreiveAtCategories();
        callCategories.enqueue(new Callback<RetrofitResponseCategory>() {
            
            @Override
            public void onResponse(Call<RetrofitResponseCategory> callCategories, Response<RetrofitResponseCategory> response) {
                ////Log.d(TAG, "retrofitResponse()");
    
                RetrofitResponseCategory retrofitResponse = response.body();
                
                ArrayList<RetrofitResponseCategory.Data> points = retrofitResponse.getDatas();
                //Log.d(TAG, "callCategories: " + points);
                
                for (RetrofitResponseCategory.Data data : points) {
                    ContentValues values = new ContentValues();
                    
                    values.put(MySQLiteHelper.COLUMN_CATEGORY_ID, data.getIdCategory());
                    values.put(MySQLiteHelper.COLUMN_CATEGORY_NAME, data.getName());
                    
                    Uri uselessReturnOverrideFunction = context.getContentResolver().insert(
                        LocalContentProvider.URI_CATEGORY,
                        values
                    );
                }
    
                Utils.completedInsert++;
            }
            
            @Override
            public void onFailure(Call<RetrofitResponseCategory> callCategories, Throwable t) {
                Log.e(TAG, "RESPONSE onFailure: " + t.toString());
            }
        });
        
        //
        // CatPoints
        //
        Call<RetrofitResponseCatPoint> callCatPoints = retrofitApi.retreiveAtCatPoints();
        callCatPoints.enqueue(new Callback<RetrofitResponseCatPoint>() {
            
            @Override
            public void onResponse(Call<RetrofitResponseCatPoint> callCatPoints, Response<RetrofitResponseCatPoint> response) {
                ////Log.d(TAG, "retrofitResponse()");
    
                RetrofitResponseCatPoint retrofitResponse = response.body();
                
                ArrayList<RetrofitResponseCatPoint.Data> points = retrofitResponse.getDatas();
                //Log.d(TAG, "callCatPoints: " + points);
                
                for (RetrofitResponseCatPoint.Data data : points) {
                    ContentValues values = new ContentValues();
                    
                    values.put(MySQLiteHelper.COLUMN_CATPOINT_POINT_ID, data.getFkIdPoint());
                    values.put(MySQLiteHelper.COLUMN_CATPOINT_CATEGORY_ID, data.getFkIdCategory());
                    
                    Uri uselessReturnOverrideFunction = context.getContentResolver().insert(
                        LocalContentProvider.URI_CATPOINT,
                        values
                    );
                }
    
                Utils.completedInsert++;
            }
            
            @Override
            public void onFailure(Call<RetrofitResponseCatPoint> callCatPoints, Throwable t) {
                Log.e(TAG, "RESPONSE onFailure: " + t.toString());
            }
        });
        
        //
        // Achievements
        //
        Call<RetrofitResponseAchievement> callAchievements = retrofitApi.retreiveAtAchievements();
        callAchievements.enqueue(new Callback<RetrofitResponseAchievement>() {
            
            @Override
            public void onResponse(Call<RetrofitResponseAchievement> callAchievements, Response<RetrofitResponseAchievement> response) {
                ////Log.d(TAG, "retrofitResponse()");
    
                RetrofitResponseAchievement retrofitResponse = response.body();
                
                ArrayList<RetrofitResponseAchievement.Data> points = retrofitResponse.getDatas();
                //Log.d(TAG, "callAchievements: " + points);
                
                for (RetrofitResponseAchievement.Data data : points) {
                    ContentValues values = new ContentValues();
                    
                    values.put(MySQLiteHelper.COLUMN_ACHIEVEMENT_ID, data.getIdAchievement());
                    values.put(MySQLiteHelper.COLUMN_ACHIEVEMENT_NAME, data.getName());
                    
                    Uri uselessReturnOverrideFunction = context.getContentResolver().insert(
                        LocalContentProvider.URI_ACHIEVEMENT,
                        values
                    );
                }
    
                Utils.completedInsert++;
            }
            
            @Override
            public void onFailure(Call<RetrofitResponseAchievement> callAchievements, Throwable t) {
                Log.e(TAG, "RESPONSE onFailure: " + t.toString());
            }
        });
        
        //
        // AchiPoints
        //
        Call<RetrofitResponseAchiPoint> callAchiPoints = retrofitApi.retreiveAtAchiPoints();
        callAchiPoints.enqueue(new Callback<RetrofitResponseAchiPoint>() {
            
            @Override
            public void onResponse(Call<RetrofitResponseAchiPoint> callAchiPoints, Response<RetrofitResponseAchiPoint> response) {
                ////Log.d(TAG, "retrofitResponse()");
    
                RetrofitResponseAchiPoint retrofitResponse = response.body();
                
                ArrayList<RetrofitResponseAchiPoint.Data> points = retrofitResponse.getDatas();
                //Log.d(TAG, "callAchiPoints: " + points);
                
                for (RetrofitResponseAchiPoint.Data data : points) {
                    ContentValues values = new ContentValues();
                    
                    values.put(MySQLiteHelper.COLUMN_ACHIPOINT_POINT_ID, data.getFkIdPoint());
                    values.put(MySQLiteHelper.COLUMN_ACHIPOINT_ACHIEVMENT_ID, data.getFkIdAchievement());
                    
                    Uri uselessReturnOverrideFunction = context.getContentResolver().insert(
                        LocalContentProvider.URI_ACHIPOINT,
                        values
                    );
                }
    
                Utils.completedInsert++;
            }
            
            @Override
            public void onFailure(Call<RetrofitResponseAchiPoint> callAchiPoints, Throwable t) {
                Log.e(TAG, "RESPONSE onFailure: " + t.toString());
            }
        });
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is to simply to discard the data and start over
        Log.w(MySQLiteHelper.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        
        // Drop Tables
        db.execSQL(DROP_TABLE_POINT);
        db.execSQL(DROP_TABLE_POINTINFO);
        db.execSQL(DROP_TABLE_GAME);
        db.execSQL(DROP_TABLE_QA);
        db.execSQL(DROP_TABLE_REACHPOINT);
        db.execSQL(DROP_TABLE_CATEGORY);
        db.execSQL(DROP_TABLE_CATPOINT);
        db.execSQL(DROP_TABLE_ACHIEVEMENT);
        db.execSQL(DROP_TABLE_ACHIPOINT);
        
        // Drop Views
        db.execSQL(DROP_VIEW_POINT);
        db.execSQL(DROP_VIEW_CATEGORIES);
        db.execSQL(DROP_VIEW_ACHIEVEMENTS);
        
        // Recreate
        onCreate(db);
    }
    
}
