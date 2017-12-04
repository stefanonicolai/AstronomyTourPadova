package it.snicolai.pdastrotour.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class RetrofitResponseReachPoint extends RetrofitResponse {
    
    @SerializedName("datas")
    @Expose
    private ArrayList<Data> data;
    
    public RetrofitResponseReachPoint(String st, String me, ArrayList<Data> gr) {
        super(st, me);
        data = gr;
    }
    
    public ArrayList<Data> getDatas() {
        return data;
    }
    
    public class Data {
        
        @SerializedName("id_reachpoint")
        @Expose
        private String idReachpoint;
        @SerializedName("fk_parent_id")
        @Expose
        private String fkParentId;
        
        public String getIdReachpoint() {
            return idReachpoint;
        }
        
        public void setIdReachpoint(String idReachpoint) {
            this.idReachpoint = idReachpoint;
        }
        
        public String getFkParentId() {
            return fkParentId;
        }
        
        public void setFkParentId(String fkParentId) {
            this.fkParentId = fkParentId;
        }
    }
}