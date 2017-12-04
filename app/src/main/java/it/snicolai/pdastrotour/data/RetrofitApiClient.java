package it.snicolai.pdastrotour.data;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitApiClient {

    public static final String BASE_URL = "http://www.digitalsn.it/";
    private static Retrofit retrofit = null;

    // Added a boolean to enable or disable the HTTP debug
    public static Retrofit getClient(boolean debug) {

        if(debug) {
            // Using OkHttp to help with debugging
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

            if (retrofit == null) {
                retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .client(client) // remove this line to prevent debugging
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }
        } else {
            if (retrofit == null) {
                retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }
        }
        return retrofit;
    }
}
