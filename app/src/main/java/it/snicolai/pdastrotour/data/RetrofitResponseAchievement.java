package it.snicolai.pdastrotour.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class RetrofitResponseAchievement extends RetrofitResponse {
    
    @SerializedName("datas")
    @Expose
    private ArrayList<Data> data;
    
    public RetrofitResponseAchievement(String st, String me, ArrayList<Data> gr) {
        super(st, me);
        data = gr;
    }
    
    public ArrayList<Data> getDatas() {
        return data;
    }
    
    public class Data {
    
        @SerializedName("id_achievement")
        @Expose
        private String idAchievement;
        @SerializedName("name")
        @Expose
        private String name;
    
        public String getIdAchievement() {
            return idAchievement;
        }
    
        public void setIdAchievement(String idAchievement) {
            this.idAchievement = idAchievement;
        }
    
        public String getName() {
            return name;
        }
    
        public void setName(String name) {
            this.name = name;
        }
    }
}