package com.ug.air.alrite.APIs;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;

public interface DecisionTreeJSON {

    @GET("/alrite/apis/workflows/IMCI/")
    Call<String> getJson();
}
