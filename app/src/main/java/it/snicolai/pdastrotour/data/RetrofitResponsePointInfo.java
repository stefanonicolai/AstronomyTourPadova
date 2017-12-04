package it.snicolai.pdastrotour.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class RetrofitResponsePointInfo extends RetrofitResponse {
    
    @SerializedName("datas")
    @Expose
    private ArrayList<Data> data;
    
    public RetrofitResponsePointInfo(String st, String me, ArrayList<Data> gr) {
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
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("title")
        @Expose
        private String title;
        @SerializedName("address")
        @Expose
        private String address;
        @SerializedName("description")
        @Expose
        private String description;
        @SerializedName("event")
        @Expose
        private String event;
        @SerializedName("extras")
        @Expose
        private String extras;
        @SerializedName("fk_qa_id")
        @Expose
        private String fkQaId;
        @SerializedName("fk_reachpoint_id")
        @Expose
        private String fkReachpointId;
        @SerializedName("reach_title")
        @Expose
        private String reachTitle;
        @SerializedName("reach_description")
        @Expose
        private String reachDescription;
        @SerializedName("id_pointinfo")
        @Expose
        private String idPointinfo;
        
        public String getFkIdPoint() {
            return fkIdPoint;
        }
        
        public void setFkIdPoint(String fkIdPoint) {
            this.fkIdPoint = fkIdPoint;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getTitle() {
            return title;
        }
        
        public void setTitle(String title) {
            this.title = title;
        }
        
        public String getAddress() {
            return address;
        }
        
        public void setAddress(String address) {
            this.address = address;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
        
        public String getEvent() {
            return event;
        }
        
        public void setEvent(String event) {
            this.event = event;
        }
        
        public String getExtras() {
            return extras;
        }
        
        public void setExtras(String extras) {
            this.extras = extras;
        }
        
        public String getFkQaId() {
            return fkQaId;
        }
        
        public void setFkQaId(String fkQaId) {
            this.fkQaId = fkQaId;
        }
        
        public String getFkReachpointId() {
            return fkReachpointId;
        }
        
        public void setFkReachpointId(String fkReachpointId) {
            this.fkReachpointId = fkReachpointId;
        }
        
        public String getReachTitle() {
            return reachTitle;
        }
        
        public void setReachTitle(String reachTitle) {
            this.reachTitle = reachTitle;
        }
        
        public String getReachDescription() {
            return reachDescription;
        }
        
        public void setReachDescription(String reachDescription) {
            this.reachDescription = reachDescription;
        }
        
        public String getIdPointinfo() {
            return idPointinfo;
        }
        
        public void setIdPointinfo(String idPointinfo) {
            this.idPointinfo = idPointinfo;
        }
        
    }
}