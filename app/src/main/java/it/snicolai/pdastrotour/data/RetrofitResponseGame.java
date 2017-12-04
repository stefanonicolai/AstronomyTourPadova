package it.snicolai.pdastrotour.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class RetrofitResponseGame extends RetrofitResponse {
    
    @SerializedName("datas")
    @Expose
    private ArrayList<Data> data;
    
    public RetrofitResponseGame(String st, String me, ArrayList<Data> gr) {
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
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("time_reached")
        @Expose
        private String timeReached;
        @SerializedName("time_completed")
        @Expose
        private String timeCompleted;
        
        public String getFkIdPoint() {
            return fkIdPoint;
        }
        
        public void setFkIdPoint(String fkIdPoint) {
            this.fkIdPoint = fkIdPoint;
        }
        
        public String getStatus() {
            return status;
        }
        
        public void setStatus(String status) {
            this.status = status;
        }
        
        public String getTimeReached() {
            return timeReached;
        }
        
        public void setTimeReached(String timeReached) {
            this.timeReached = timeReached;
        }
        
        public String getTimeCompleted() {
            return timeCompleted;
        }
        
        public void setTimeCompleted(String timeCompleted) {
            this.timeCompleted = timeCompleted;
        }
    }
    
}