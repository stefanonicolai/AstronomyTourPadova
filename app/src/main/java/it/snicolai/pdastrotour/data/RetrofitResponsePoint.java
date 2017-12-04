package it.snicolai.pdastrotour.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class RetrofitResponsePoint extends RetrofitResponse {
    
    @SerializedName("datas")
    @Expose
    private ArrayList<Data> data;
    
    public RetrofitResponsePoint(String st, String me, ArrayList<Data> gr) {
        super(st, me);
        data = gr;
    }
    
    public ArrayList<Data> getDatas() {
        return data;
    }
    
    
    public class Data {
    
        @SerializedName("id_point")
        @Expose
        private String idPoint;
        @SerializedName("pos")
        @Expose
        private String pos;
        @SerializedName("image")
        @Expose
        private String image;
        @SerializedName("lat")
        @Expose
        private Double lat;
        @SerializedName("lon")
        @Expose
        private Double lon;
        @SerializedName("radius")
        @Expose
        private Integer radius;
    
        public String getIdPoint() {
            return idPoint;
        }
    
        public void setIdPoint(String idPoint) {
            this.idPoint = idPoint;
        }
    
        public String getPos() {
            return pos;
        }
    
        public void setPos(String pos) {
            this.pos = pos;
        }
    
        public String getImage() {
            return image;
        }
    
        public void setImage(String image) {
            this.image = image;
        }
    
        public Double getLat() {
            return lat;
        }
    
        public void setLat(Double lat) {
            this.lat = lat;
        }
    
        public Double getLon() {
            return lon;
        }
    
        public void setLon(Double lon) {
            this.lon = lon;
        }
    
        public Integer getRadius() {
            return radius;
        }
    
        public void setRadius(Integer radius) {
            this.radius = radius;
        }
    }
    
}