package it.snicolai.pdastrotour.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class RetrofitResponseQa extends RetrofitResponse {
    
    @SerializedName("datas")
    @Expose
    private ArrayList<Data> data;
    
    public RetrofitResponseQa(String st, String me, ArrayList<Data> gr) {
        super(st, me);
        data = gr;
    }
    
    public ArrayList<Data> getDatas() {
        return data;
    }
    
    public class Data {
        
        @SerializedName("id_qa")
        @Expose
        private String idQa;
        @SerializedName("question")
        @Expose
        private String question;
        @SerializedName("answer_1")
        @Expose
        private String answer1;
        @SerializedName("answer_2")
        @Expose
        private String answer2;
        @SerializedName("answer_3")
        @Expose
        private String answer3;
        @SerializedName("answer_4")
        @Expose
        private String answer4;
        @SerializedName("pos_correct")
        @Expose
        private String posCorrect;
        
        public String getIdQa() {
            return idQa;
        }
        
        public void setIdQa(String idQa) {
            this.idQa = idQa;
        }
        
        public String getQuestion() {
            return question;
        }
        
        public void setQuestion(String question) {
            this.question = question;
        }
        
        public String getAnswer1() {
            return answer1;
        }
        
        public void setAnswer1(String answer1) {
            this.answer1 = answer1;
        }
        
        public String getAnswer2() {
            return answer2;
        }
        
        public void setAnswer2(String answer2) {
            this.answer2 = answer2;
        }
        
        public String getAnswer3() {
            return answer3;
        }
        
        public void setAnswer3(String answer3) {
            this.answer3 = answer3;
        }
        
        public String getAnswer4() {
            return answer4;
        }
        
        public void setAnswer4(String answer4) {
            this.answer4 = answer4;
        }
        
        public String getPosCorrect() {
            return posCorrect;
        }
        
        public void setPosCorrect(String posCorrect) {
            this.posCorrect = posCorrect;
        }
    }
}