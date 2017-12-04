package it.snicolai.pdastrotour.at;


import android.text.TextUtils;

import it.snicolai.pdastrotour.R;
import it.snicolai.pdastrotour.data.MySQLiteHelper;

public class AtAchievement {
    private transient final String TAG = "AtAchievement";
    
    private int id_achievement;
    private String name;
    private String[] pointIds;
    int totalVoid, totalReached, totalCompleted;
    private String status;
    
    AtAchievement() {
    }
    
    public AtAchievement(int id, String name, String[] pointIds, int totalVoid, int totalReached, int totalCompleted) {
        this.id_achievement = id;
        this.name = name;
        this.pointIds = pointIds;
        this.totalVoid = totalVoid;
        this.totalReached = totalReached;
        this.totalCompleted = totalCompleted;
        
        // Percentage: (int)(value*(percentage/100.0f));
        
        // Gold: 100% Completed
        if (totalCompleted == pointIds.length) {
            this.status = MySQLiteHelper.ACHIEVEMENT_STATUS_GOLD;
        }
        // Silver: 75% Reached + Completed
        else if ((totalReached + totalCompleted) >= ((int)(pointIds.length * (75/100.0f)))) {
            this.status = MySQLiteHelper.ACHIEVEMENT_STATUS_SILVER;
        }
        // Copper: 40% Reached + Completed
        else if ((totalReached + totalCompleted) >= ((int)(pointIds.length * (40/100.0f)))) {
            this.status = MySQLiteHelper.ACHIEVEMENT_STATUS_COPPER;
        }
        // Empty: not even 40%? Cm'on man!
        else {
            this.status = MySQLiteHelper.ACHIEVEMENT_STATUS_VOID;
        }
    }
    
    public AtAchievement(AtAchievement atAchievement) {
        this.id_achievement = atAchievement.getIdAchievement();
        this.name = atAchievement.getName();
        this.pointIds = atAchievement.getPointIds();
        this.totalVoid = atAchievement.getTotalVoid();
        this.totalReached = atAchievement.getTotalReached();
        this.totalCompleted = atAchievement.getTotalCompleted();
        this.status = atAchievement.getStatus();
    }
    
    public Integer getIdAchievement() {
        return id_achievement;
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
    
    public int getTotalVoid() {
        return totalVoid;
    }
    
    public int getTotalReached() {
        return totalReached;
    }
    
    public int getTotalCompleted() {
        return totalCompleted;
    }
    
    public String getStatus() {
        return status;
    }
    
    public int getTrophyImage() {
        int trophyImageId = R.drawable.ic_trophy_empty;
        
        switch (getStatus()) {
            case MySQLiteHelper.ACHIEVEMENT_STATUS_COPPER:
                trophyImageId = R.drawable.ic_trophy_copper;
                break;
            case MySQLiteHelper.ACHIEVEMENT_STATUS_SILVER:
                trophyImageId = R.drawable.ic_trophy_silver;
                break;
            case MySQLiteHelper.ACHIEVEMENT_STATUS_GOLD:
                trophyImageId = R.drawable.ic_trophy_gold;
                break;
        }
        
        return trophyImageId;
    }
    
    public String toString() {
        return "AtAchievement [" +
            MySQLiteHelper.COLUMN_ACHIEVEMENT_ID + ": " + id_achievement + ", " +
            MySQLiteHelper.COLUMN_ACHIEVEMENT_NAME + ": " + name + ", " +
            MySQLiteHelper.GROUP_CONCAT_POINT + ": " + getPointIdsString() + ", " +
            "totalVoid: " + totalVoid + ", " +
            "totalReached: " + totalReached + ", " +
            "totalCompleted: " + totalCompleted + ", " +
            "status: " + status + ", " +
            "]";
    }
}
