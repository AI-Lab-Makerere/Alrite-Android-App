package com.ug.air.alrite.APIs;

import com.ug.air.alrite.Utils.BackendPostRequest;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface BackendRequests {

    @GET("/alrite/apis/workflows/{workflow_id}/")
    Call<String> getJson(@Path("workflow_id") String workflow_id);

    @POST("/apis/data/{workflow_id}/{workflow_version}/")
    Observable<String> postToBackend(@Path("workflow_id") String workflow_id,
                                    @Path("workflow_version") String workflow_version,
                                    @Body BackendPostRequest body);
}
