package it.snicolai.pdastrotour.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class RetrofitResponseCategory extends RetrofitResponse {
    
    @SerializedName("datas")
    @Expose
    private ArrayList<Data> data;
    
    public RetrofitResponseCategory(String st, String me, ArrayList<Data> gr) {
        super(st, me);
        data = gr;
    }
    
    public ArrayList<Data> getDatas() {
        return data;
    }
    
    public class Data {
    
        @SerializedName("id_category")
        @Expose
        private String idCategory;
        @SerializedName("name")
        @Expose
        private String name;
    
        public String getIdCategory() {
            return idCategory;
        }
    
        public void setIdCategory(String idCategory) {
            this.idCategory = idCategory;
        }
    
        public String getName() {
            return name;
        }
    
        public void setName(String name) {
            this.name = name;
        }
    }
}