package com.ug.air.alrite.APIs;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface DecisionTreeJSON {

    @GET("/alrite/apis/workflows/{workflow_id}/")
    Call<String> getJson(@Path("workflow_id") String workflow_id);
}
