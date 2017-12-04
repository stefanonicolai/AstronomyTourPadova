package it.snicolai.pdastrotour.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class RetrofitResponseAchiPoint extends RetrofitResponse {
    
    @SerializedName("datas")
    @Expose
    private ArrayList<Data> data;
    
    public RetrofitResponseAchiPoint(String st, String me, ArrayList<Data> gr) {
        super(st, me);
        data = gr;
    }
    
    public ArrayList<Data> getDatas() {
        return data;
    }
    
    public class Data {
    
        @SerializedName("fk_id_point")
        @Expose
        private String fkIdPoint;
        @SerializedName("fk_id_achievement")
        @Expose
        private String fkIdAchievement;
    
        public String getFkIdPoint() {
            return fkIdPoint;
        }
    
        public void setFkIdPoint(String fkIdPoint) {
            this.fkIdPoint = fkIdPoint;
        }
    
        public String getFkIdAchievement() {
            return fkIdAchievement;
        }
    
        public void setFkIdAchievement(String fkIdAchievement) {
            this.fkIdAchievement = fkIdAchievement;
        }
    }
}