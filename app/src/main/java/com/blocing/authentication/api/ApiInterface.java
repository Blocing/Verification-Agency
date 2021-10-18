package com.blocing.authentication.api;

import com.blocing.authentication.dto.Student;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiInterface {
    @GET("/idcard/auth/{did}")
    Call<Student> getStudent(@Path("did") String did);
}
