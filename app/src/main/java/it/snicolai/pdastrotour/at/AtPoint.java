package it.snicolai.pdastrotour.at;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import it.snicolai.pdastrotour.R;
import it.snicolai.pdastrotour.data.MySQLiteHelper;
import it.snicolai.pdastrotour.utils.AtUtils;

public class AtPoint implements Parcelable {
    private transient final String TAG = "AtPoint";
    private transient Context context;
    
    private int id_point;
    private Integer pos;
    private String name;
    private String title;
    private String address;
    private String description;
    private String image;
    private double lat;
    private double lon;
    private Integer radius;
    private String[] categories;
    // Game
    private String status; // void, reached, completed
    private String event;
    private String time_reached;
    private String time_completed;
    // Questions
    private String question;
    private String answer1;
    private String answer2;
    private String answer3;
    private String answer4;
    private Integer pos_correct;
    // Infos
    private String extras;
    // Related Point
    private Integer parent_id;
    private String reach_title;
    private String reach_description;
    
    // G.Map
    public AtPoint(int id, String name, String title, double lat, double lon, int pos, String status) {
        this.id_point = id;
        this.name = name;
        this.title = title;
        this.lat = lat;
        this.lon = lon;
        this.pos = pos;
        this.status = status;
    }
    
    // Check for point in AtUtils
    public AtPoint(AtPoint atPoint) {
        this.id_point = atPoint.getIdPoint();
        this.pos = atPoint.getPos();
        this.name = atPoint.getName();
        this.title = atPoint.getTitle();
        this.address = atPoint.getAddress();
        this.description = atPoint.getDescription();
        this.image = atPoint.getImage();
        this.lat = atPoint.getLat();
        this.lon = atPoint.getLon();
        this.radius = atPoint.getRadius();
        this.status = atPoint.getStatus();
        this.event = atPoint.getEvent();
        this.time_reached = atPoint.getTimeReached();
        this.time_completed = atPoint.getTimeCompleted();
        this.question = atPoint.getQuestion();
        this.answer1 = atPoint.getAnswer1();
        this.answer2 = atPoint.getAnswer2();
        this.answer3 = atPoint.getAnswer3();
        this.answer4 = atPoint.getAnswer4();
        this.pos_correct = atPoint.getPosCorrect();
        this.categories = atPoint.getCategories();
        this.extras = atPoint.getExtras();
        this.parent_id = atPoint.getParentId();
        this.reach_title = atPoint.getReachTitle();
        this.reach_description = atPoint.getReachDescription();
    }
    
    // From DB - List
    public AtPoint(
        int id, int pos, String name, String title, String address, String description, String image,
        double lat, double lon, Integer radius, String status,
        String time_reached, String time_completed,
        Integer parent_id, Context context
    ) {
        this.id_point = id;
        this.pos = pos;
        this.name = name;
        this.title = title;
        this.address = address;
        this.description = description;
        this.image = image;
        this.lat = lat;
        this.lon = lon;
        this.radius = radius;
        this.status = status;
        this.time_reached = time_reached;
        this.time_completed = time_completed;
        this.parent_id = parent_id;
        this.context = context;
    }
    
    // From DB - Single
    public AtPoint(
        int id, int pos, String name, String title, String address, String description,
        String image, double lat, double lon, Integer radius, String categories,
        String status, String event, String time_reached, String time_completed,
        String question, String answer1, String answer2, String answer3, String answer4, int pos_correct,
        String extras, Integer parent_id, String reach_title, String reach_description, Context context
    ) {
        this.id_point = id;
        this.pos = pos;
        this.name = name;
        this.title = title;
        this.address = address;
        this.description = description;
        this.image = image;
        this.lat = lat;
        this.lon = lon;
        this.radius = radius;
        this.status = status;
        this.event = event;
        this.time_reached = time_reached;
        this.time_completed = time_completed;
        this.question = question;
        this.answer1 = answer1;
        this.answer2 = answer2;
        this.answer3 = answer3;
        this.answer4 = answer4;
        this.pos_correct = pos_correct;
        if (categories != null) {
            this.categories = categories.split(MySQLiteHelper.GROUP_CONCAT_DIVIDER);
        } else {
            this.categories = new String[0];
        }
        this.extras = extras;
        this.parent_id = parent_id;
        this.reach_title = reach_title;
        this.reach_description = reach_description;
        this.context = context;
    }
    
    
    public AtPoint() {
    }
    
    public Integer getIdPoint() {
        return id_point;
    }
    
    public String getName() {
        String pointName = name;
        
        if (context != null && isChild() && !hasBeenReached()) {
            pointName = context.getResources().getString(context.getResources().getIdentifier("point_unreached_name", "string", context.getPackageName()));
        }
        
        return pointName;
    }
    
    public String getTitle() {
        String pointTitle = title;
        
        if (context != null && isChild() && !hasBeenReached()) {
            AtPoint atPoint;
            atPoint = AtUtils.getPoint(getParentId(), context);
            
            pointTitle = context.getResources().getString(context.getResources().getIdentifier("point_unreached_title", "string", context.getPackageName()));
            pointTitle += "\"" + atPoint.getName() + "\"";
        }
        
        return pointTitle;
    }
    
    public String getAddress() {
        String pointAddress = address;
        
        if (context != null && isChild() && !hasBeenReached()) {
            pointAddress = context.getResources().getString(context.getResources().getIdentifier("point_unreached_address", "string", context.getPackageName()));
        }
        
        return pointAddress;
    }
    
    public String getDescription() {
        String pointDescription = description;
        
        if (context != null && isChild() && !hasBeenReached()) {
            AtPoint atPoint;
            atPoint = AtUtils.getPoint(getParentId(), context);
            
            pointDescription = context.getResources().getString(context.getResources().getIdentifier("point_unreached_description", "string", context.getPackageName()));
            pointDescription += "\"" + atPoint.getName() + "\"";
        }
        
        return pointDescription;
    }
    
    public String getImage() {
        String pointImage = image;
        
        if (context != null && isChild() && !hasBeenReached()) {
            pointImage = "hidden_point";
        }
        
        return pointImage;
    }
    
    public String getCorrectAnswer() {
        String correctAnswer = "";
        switch (getPosCorrect()) {
            case 0:
                correctAnswer = getAnswer1();
                break;
            case 1:
                correctAnswer = getAnswer2();
                break;
            case 2:
                correctAnswer = getAnswer3();
                break;
            case 3:
                correctAnswer = getAnswer4();
                break;
        }
        return correctAnswer;
    }
    
    public double getLat() {
        return lat;
    }
    
    public double getLon() {
        return lon;
    }
    
    public Integer getRadius() {
        return radius;
    }
    
    public Integer getPos() {
        return pos;
    }
    
    public String getStatus() {
        return status;
    }
    
    public String getTimeReached() {
        return time_reached;
    }
    
    public String getTimeCompleted() {
        return time_completed;
    }
    
    public String getQuestion() {
        return question;
    }
    
    public String getAnswer1() {
        return answer1;
    }
    
    public String getAnswer2() {
        return answer2;
    }
    
    public String getAnswer3() {
        return answer3;
    }
    
    public String getAnswer4() {
        return answer4;
    }
    
    public Integer getPosCorrect() {
        return pos_correct;
    }
    
    public String[] getCategories() {
        return categories;
    }
    
    public String getExtras() {
        return extras;
    }
    
    public String getEvent() {
        return event;
    }
    
    public Integer getParentId() {
        return parent_id;
    }
    
    public String getReachTitle() {
        return reach_title;
    }
    
    public String getReachDescription() {
        return "&quot;" + reach_description + "&quot;";
    }
    
    public String toString() {
        return "AtPoint [\n" +
            MySQLiteHelper.COLUMN_POINT_ID + ": " + id_point + ", " +
            MySQLiteHelper.COLUMN_POINT_POS + ": " + pos + ", " +
            MySQLiteHelper.COLUMN_POINTINFO_NAME + ": " + name + ", " +
            MySQLiteHelper.COLUMN_POINTINFO_TITLE + ": " + title + ", " +
            MySQLiteHelper.COLUMN_POINTINFO_ADDRESS + ": " + address + ", " +
            MySQLiteHelper.COLUMN_POINTINFO_DESCRIPTION + ": " + lon + ", " +
            MySQLiteHelper.COLUMN_POINTINFO_EVENT + ": " + event + ", " +
            MySQLiteHelper.COLUMN_POINTINFO_EXTRAS + ": " + extras + ", " +
            MySQLiteHelper.COLUMN_POINT_IMAGE + ": " + lon + ", " +
            MySQLiteHelper.COLUMN_POINT_LAT + ": " + lat + ", " +
            MySQLiteHelper.COLUMN_POINT_LON + ": " + lon + ", " +
            MySQLiteHelper.COLUMN_GAME_STATUS + ": " + status + ", " +
            MySQLiteHelper.COLUMN_GAME_TIME_REACHED + ": " + time_reached + ", " +
            MySQLiteHelper.COLUMN_GAME_TIME_COMPLETED + ": " + time_completed + ", " +
            MySQLiteHelper.COLUMN_QA_QUESTION + ": " + question + ", " +
            MySQLiteHelper.COLUMN_POINTINFO_REACHPOINT_ID + ": " + parent_id + ", " +
            MySQLiteHelper.COLUMN_POINTINFO_REACH_TITLE + ": " + reach_title + ", " +
            MySQLiteHelper.COLUMN_POINTINFO_REACH_DESCRIPTION + ": " + reach_description + " " +
            "]";
    }
    
    // Checking if the point is valid
    public boolean isValid() {
        boolean isValid = false;
        if (
            this.getLat() != 0 &&
                this.getLon() != 0
            ) {
            isValid = true;
        }
        return isValid;
    }
    
    public boolean isEmpty() {
        boolean isEmpty = false;
        
        if (
            this.getName() == null &&
                this.getLon() == 0 &&
                this.getLat() == 0
            ) {
            isEmpty = true;
        }
        
        return isEmpty;
    }
    
    public boolean isChild() {
        boolean isChild = false;
        
        if (getParentId() != null && getParentId() != 0) {
            isChild = true;
        }
        
        return isChild;
    }
    
    public boolean hasBeenReached() {
        boolean hasBeenReached = false;
        
        if (getStatus().equals(MySQLiteHelper.POINT_STATUS_REACHED) || getStatus().equals(MySQLiteHelper.POINT_STATUS_COMPLETED)) {
            hasBeenReached = true;
        }
        
        return hasBeenReached;
    }
    
    public void setReached(String dateTime) {
        status = MySQLiteHelper.POINT_STATUS_REACHED;
        time_reached = dateTime;
    }
    
    public boolean hasBeenCompleted() {
        boolean hasBeenCompleted = false;
        
        if (getStatus().equals(MySQLiteHelper.POINT_STATUS_COMPLETED)) {
            hasBeenCompleted = true;
        }
        
        return hasBeenCompleted;
    }
    
    public void setCompleted(String dateTime) {
        status = MySQLiteHelper.POINT_STATUS_COMPLETED;
        time_completed = dateTime;
    }
    
    public void setNotReached() {
        status = MySQLiteHelper.POINT_STATUS_VOID;
        time_reached = null;
        time_completed = null;
    }
    
    // Function called mainly inside "ListPoints" to show what level of completion the point has (none, reached, completed)
    public int getImageStar() {
        int imageStarId;
        
        if (getStatus() == null || getStatus().isEmpty()) {
            imageStarId = R.drawable.ic_star_empty;
        } else if (getStatus().equals(MySQLiteHelper.POINT_STATUS_COMPLETED)) {
            imageStarId = R.drawable.ic_star_full;
        } else if (getStatus().equals(MySQLiteHelper.POINT_STATUS_REACHED)) {
            imageStarId = R.drawable.ic_star_half;
        } else {
            imageStarId = R.drawable.ic_star_empty;
        }
        
        return imageStarId;
    }
    
    // Function called mainly from "ShowMap" to show what points are reached and what are still reachable
    public Integer getMarker(Integer pointId, Context context) {
    
        Integer marker;
    
        if (pointId == getIdPoint()) {
            marker = R.drawable.ic_place_thisone;
        } else if (getStatus().equals(MySQLiteHelper.POINT_STATUS_COMPLETED)) {
            marker = R.drawable.ic_place_completed;
        } else if (getStatus().equals(MySQLiteHelper.POINT_STATUS_REACHED)) {
            marker = R.drawable.ic_place_reached;
        } else {
            marker = R.drawable.ic_place_empty;
        }
        
        return marker;
    }
    
    
    // ---------------------------------------------------------------------------------------------
    // PARCELING SECTION
    // ---------------------------------------------------------------------------------------------
    
    protected AtPoint(Parcel in) {
        id_point = in.readInt();
        pos = in.readByte() == 0x00 ? null : in.readInt();
        name = in.readString();
        title = in.readString();
        description = in.readString();
        image = in.readString();
        lat = in.readDouble();
        lon = in.readDouble();
        status = in.readString();
        time_reached = in.readString();
    }
    
    @Override
    public int describeContents() {
        return 0;
    }
    
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id_point);
        if (pos == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(pos);
        }
        dest.writeString(name);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(image);
        dest.writeDouble(lat);
        dest.writeDouble(lon);
        dest.writeString(status);
        dest.writeString(time_reached);
    }
    
    @SuppressWarnings("unused")
    public static final Parcelable.Creator<AtPoint> CREATOR = new Parcelable.Creator<AtPoint>() {
        @Override
        public AtPoint createFromParcel(Parcel in) {
            return new AtPoint(in);
        }
        
        @Override
        public AtPoint[] newArray(int size) {
            return new AtPoint[size];
        }
    };
}