package it.snicolai.pdastrotour.data;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

// Needed by Gson to understand the remote webserver response
public class RetrofitResponse {
    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("message")
    @Expose
    private String message;
    
    public RetrofitResponse(String st, String me) {
        status = st;
        message = me;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }
}
