package it.snicolai.pdastrotour.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

public class LocalContentProvider extends ContentProvider {

    private String TAG = "Provider";
    // database Helper
    private MySQLiteHelper dbHelper;

    private static final String AUTHORITY = "it.snicolai.pdastrotour.provider";

    private static final String BASE_PATH = "localData";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);
    
    public static final Uri URI_POINT = Uri.parse(CONTENT_URI + "/" + MySQLiteHelper.TABLE_POINT);
    public static final Uri URI_POINTINFO = Uri.parse(CONTENT_URI + "/" + MySQLiteHelper.TABLE_POINTINFO);
    public static final Uri URI_GAME = Uri.parse(CONTENT_URI + "/" + MySQLiteHelper.TABLE_GAME);
    public static final Uri URI_QA = Uri.parse(CONTENT_URI + "/" + MySQLiteHelper.TABLE_QA);
    public static final Uri URI_REACHPOINT = Uri.parse(CONTENT_URI + "/" + MySQLiteHelper.TABLE_REACHPOINT);
    public static final Uri URI_CATEGORY = Uri.parse(CONTENT_URI + "/" + MySQLiteHelper.TABLE_CATEGORY);
    public static final Uri URI_CATPOINT = Uri.parse(CONTENT_URI + "/" + MySQLiteHelper.TABLE_CATPOINT);
    public static final Uri URI_ACHIEVEMENT = Uri.parse(CONTENT_URI + "/" + MySQLiteHelper.TABLE_ACHIEVEMENT);
    public static final Uri URI_ACHIPOINT = Uri.parse(CONTENT_URI + "/" + MySQLiteHelper.TABLE_ACHIPOINT);

    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/localData";

    // Creates a UriMatcher object.
    private static final UriMatcher myUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        myUriMatcher.addURI(AUTHORITY, BASE_PATH + "/" + MySQLiteHelper.TABLE_POINT, 1);
        myUriMatcher.addURI(AUTHORITY, BASE_PATH + "/" + MySQLiteHelper.TABLE_GAME, 3);
        myUriMatcher.addURI(AUTHORITY, BASE_PATH + "/" + MySQLiteHelper.TABLE_CATEGORY, 4);
        myUriMatcher.addURI(AUTHORITY, BASE_PATH + "/" + MySQLiteHelper.TABLE_ACHIEVEMENT, 2);
        myUriMatcher.addURI(AUTHORITY, BASE_PATH + "/" + MySQLiteHelper.TABLE_POINTINFO, 5);
        myUriMatcher.addURI(AUTHORITY, BASE_PATH + "/" + MySQLiteHelper.TABLE_QA, 6);
        myUriMatcher.addURI(AUTHORITY, BASE_PATH + "/" + MySQLiteHelper.TABLE_REACHPOINT, 7);
        myUriMatcher.addURI(AUTHORITY, BASE_PATH + "/" + MySQLiteHelper.TABLE_CATPOINT, 8);
        myUriMatcher.addURI(AUTHORITY, BASE_PATH + "/" + MySQLiteHelper.TABLE_ACHIPOINT, 9);
    }

    public LocalContentProvider() {
    }

    @Override
    public String getType(Uri uri) {
        //throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.d(TAG, "insert()");

        SQLiteDatabase sqlDB =  dbHelper.getWritableDatabase();
        long id = 0;
        switch (myUriMatcher.match(uri)) {
            case 1:
                id = sqlDB.insert(MySQLiteHelper.TABLE_POINT, null, values);
                break;
            case 2:
                id = sqlDB.insert(MySQLiteHelper.TABLE_ACHIEVEMENT, null, values);
                break;
            case 3:
                id = sqlDB.insert(MySQLiteHelper.TABLE_GAME, null, values);
                break;
            case 4:
                id = sqlDB.insert(MySQLiteHelper.TABLE_CATEGORY, null, values);
                break;
            case 5:
                id = sqlDB.insert(MySQLiteHelper.TABLE_POINTINFO, null, values);
                break;
            case 6:
                id = sqlDB.insert(MySQLiteHelper.TABLE_QA, null, values);
                break;
            case 7:
                id = sqlDB.insert(MySQLiteHelper.TABLE_REACHPOINT, null, values);
                break;
            case 8:
                id = sqlDB.insert(MySQLiteHelper.TABLE_CATPOINT, null, values);
                break;
            case 9:
                id = sqlDB.insert(MySQLiteHelper.TABLE_ACHIPOINT, null, values);
                break;
            default:
                Log.d(TAG, " Switch-case default!");
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        if(id == -1){
            Log.e(TAG, "error during insert() | id:"+id);
            Toast.makeText(getContext(), "Insertion error! An error occurred", Toast.LENGTH_LONG).show();
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.d(TAG, "update()");

        SQLiteDatabase sqlDB =  dbHelper.getWritableDatabase();
        int count = 0;
        switch (myUriMatcher.match(uri)) {
            case 1:
                count = sqlDB.update(MySQLiteHelper.TABLE_POINT, values, selection, selectionArgs);
                break;
            case 2:
                count = sqlDB.update(MySQLiteHelper.TABLE_ACHIEVEMENT, values, selection, selectionArgs);
                break;
            case 3:
                count = sqlDB.update(MySQLiteHelper.TABLE_GAME, values, selection, selectionArgs);
                break;
            case 4:
                count = sqlDB.update(MySQLiteHelper.TABLE_CATEGORY, values, selection, selectionArgs);
                break;
            default:
                Log.d(TAG, " Switch-case default!");
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        if (count<1) {
            Log.e(TAG, "during update() count = "+count);
            Toast.makeText(getContext(), "Update possible error: no row was updated", Toast.LENGTH_LONG).show();
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.d(TAG, "delete()");

        SQLiteDatabase sqlDB =  dbHelper.getWritableDatabase();
        int count = 0;
        switch (myUriMatcher.match(uri)) {
            case 1:
                count = sqlDB.delete(MySQLiteHelper.TABLE_POINT, selection, selectionArgs);
                break;
            case 2:
                count = sqlDB.delete(MySQLiteHelper.TABLE_ACHIEVEMENT, selection, selectionArgs);
                break;
            case 3:
                count = sqlDB.delete(MySQLiteHelper.TABLE_GAME, selection, selectionArgs);
                break;
            case 4:
                count = sqlDB.delete(MySQLiteHelper.TABLE_CATEGORY, selection, selectionArgs);
                break;
            default:
                Log.d(TAG, " Switch-case default!");
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        if (count < 1){
            Log.e(TAG, "during delete() count = "+count);
            // Toast.makeText(getContext(), "Delete possible error: no row was deleted", Toast.LENGTH_LONG).show();
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return count;
    }

    @Override
    public boolean onCreate() {
        Log.d(TAG, "onCreate()");
        dbHelper = new MySQLiteHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.d(TAG, "Running query ()");

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        switch (myUriMatcher.match(uri)){
            case 1:
                queryBuilder.setTables(MySQLiteHelper.VIEW_POINT);
                break;
            case 2:
                queryBuilder.setTables(MySQLiteHelper.VIEW_ACHIEVEMENTS);
                break;
            case 3:
                queryBuilder.setTables(MySQLiteHelper.TABLE_GAME);
                break;
            case 4:
                queryBuilder.setTables(MySQLiteHelper.VIEW_CATEGORIES);
                break;
            default:
                // Handling deafault case is a good idea
                Log.d(TAG, " Switch-case default!");
                throw new UnsupportedOperationException("Not yet implemented");
        }
        SQLiteDatabase localDB = dbHelper.getWritableDatabase();
        Cursor cursor = queryBuilder.query(localDB, projection, selection, selectionArgs, null, null, sortOrder);

        return cursor;
    }
}
