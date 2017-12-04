package it.snicolai.pdastrotour.data;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RetrofitApi {

    // Points
    @GET("/showroom/astrotourpd/get_atpoints.php")
    Call<RetrofitResponsePoint> retreiveAtPoints();
    
    // PointInfos
    @GET("/showroom/astrotourpd/get_atpointinfos.php")
    Call<RetrofitResponsePointInfo> retreiveAtPointInfos();
    
    // Game
    @GET("/showroom/astrotourpd/get_atgames.php")
    Call<RetrofitResponseGame> retreiveAtGames();
    
    // QA
    @GET("/showroom/astrotourpd/get_atqa.php")
    Call<RetrofitResponseQa> retreiveAtQas();
    
    // ReachPoint
    @GET("/showroom/astrotourpd/get_atreachpoint.php")
    Call<RetrofitResponseReachPoint> retreiveAtReachPoints();
    
    // Categories
    @GET("/showroom/astrotourpd/get_atcategories.php")
    Call<RetrofitResponseCategory> retreiveAtCategories();
    
    // CatPoint
    @GET("/showroom/astrotourpd/get_atcatpoints.php")
    Call<RetrofitResponseCatPoint> retreiveAtCatPoints();
    
    // Achievements
    @GET("/showroom/astrotourpd/get_atachievements.php")
    Call<RetrofitResponseAchievement> retreiveAtAchievements();
    
    // AchiPoint
    @GET("/showroom/astrotourpd/get_atachipoints.php")
    Call<RetrofitResponseAchiPoint> retreiveAtAchiPoints();
    
}
