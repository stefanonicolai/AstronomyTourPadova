package it.snicolai.pdastrotour.at;


import android.text.TextUtils;

import it.snicolai.pdastrotour.data.MySQLiteHelper;

public class AtCategory {
    private transient final String TAG = "AtCategory";
    
    private int id_category;
    private String name;
    private String[] pointIds;
    
    AtCategory() {
    }
    
    public AtCategory(int id, String name, String[] pointIds) {
        this.id_category = id;
        this.name = name;
        this.pointIds = pointIds;
    }
    
    public AtCategory(AtCategory atCategory) {
        this.id_category = atCategory.getIdCategory();
        this.name = atCategory.getName();
        this.pointIds = atCategory.getPointIds();
    }
    
    public Integer getIdCategory() {
        return id_category;
    }
    
    public String getName() {
        return name;
    }
    
    public String[] getPointIds() {
        return pointIds;
    }
    
    public String getPointIdsString() {
        String localPoints = "";
        if (pointIds != null) {
            localPoints = TextUtils.join(MySQLiteHelper.GROUP_CONCAT_DIVIDER, pointIds);
        }
        return localPoints;
    }
    
    public String toString() {
        return "AtCategory [" +
            MySQLiteHelper.COLUMN_CATEGORY_ID + ": " + id_category + ", " +
            MySQLiteHelper.COLUMN_CATEGORY_NAME + ": " + name + ", " +
            MySQLiteHelper.GROUP_CONCAT_POINT + ": " + getPointIdsString() + ", " +
            "]";
    }
}
