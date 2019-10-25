package com.yukino.utils;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;

public interface RetrofitAPI {

    @FormUrlEncoded
    @POST("user/student")
    Call<String> checkStudent(@HeaderMap Map<String, String> headers, @FieldMap Map<String, String> params);

    @FormUrlEncoded
    @PUT("location/student")
    Call<String> addStudentLocation(@HeaderMap Map<String, String> headers, @FieldMap Map<String, String> params);

    @GET("location/student")
    Call<String> getStudentLocation(@HeaderMap Map<String, String> headers);

    @GET("location/teacher")
    Call<String> getTeacherLocation(@HeaderMap Map<String, String> headers);

    @Multipart
    @POST("upload/Image")
    Call<ResponseBody> uploadImage(@Part MultipartBody.Part file, @Part("name") RequestBody requestBody);

    @GET("course1/check")
    Call<String> GetCourse1(@HeaderMap Map<String, String> headers);

    @FormUrlEncoded
    @PUT("password/student")
        // get student attendance result method
    Call<String> changestudentpassword(@HeaderMap Map<String, String> headers, @FieldMap Map<String, String> params);
}
