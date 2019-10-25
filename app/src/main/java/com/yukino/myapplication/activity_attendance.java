package com.yukino.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.yukino.http.UserHttpController;
import com.yukino.utils.RetrofitAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;


public class activity_attendance extends AppCompatActivity {


    public TextView location;
    String student_id = MainActivity.account;
    String account = student_id;
    public static double result;
    //Intent account = getIntent();
    //String account1 = account.getStringExtra("account");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        location = (TextView) findViewById(R.id.locationInfo);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://47.102.105.203:8082")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        RetrofitAPI service = retrofit.create(RetrofitAPI.class);
        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", "text/plain");
        Call<String> studentCall = service.getStudentLocation(header);

        studentCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if (response.isSuccessful()) {
                    String returnJson = response.body();
                    try {
                        JSONObject jsonObject = new JSONObject(returnJson);
                        if(jsonObject.getBoolean("status")) {
                            JSONArray locations = jsonObject.getJSONArray("locations");
                            StringBuilder sb = new StringBuilder();
                            String student_id;
                            double longtitude, latitude;
                            int attendance;
                            for (int i = 0; i < locations.length(); i++) {
                                student_id = locations.getJSONObject(i).getString("student_id");
//                                longtitude = locations.getJSONObject(i).getDouble("longitude");
//                                latitude = locations.getJSONObject(i).getDouble("latitude");
                                attendance = locations.getJSONObject(i).getInt("attendance");
                                if(student_id.equals(account)) {
                                    sb.append("student_id: " + student_id + "\n");
//                                    sb.append("longitude: " + longtitude + "\n");
//                                    sb.append("latitude: " + latitude + "\n");
                                    if(attendance == 1) {
                                        sb.append("result: Attendance\n\n");
                                        result = attendance;
                                    }
                                    else{
                                        sb.append("result: Absent\n\n");
                                        result = attendance;
                                    }
                                }
                            }
                        } else {
                            Log.i("call failed", "diu lei lou mou");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.i("call failed", "Stack");
                    }
                } else {
                    Log.i("call failed", response.toString());
                }
            }


            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                Log.i("call failed", throwable.getMessage());
                //userHttpControllerListener.fail();
            }
        });

        UserHttpController.GetLocation(account, location, new UserHttpController.UserHttpControllerListener() {
            SpannableString testText = new SpannableString("JOJO");

            @Override
            public void success() {
                Toast.makeText(activity_attendance.this, "succeed",Toast.LENGTH_SHORT).show();
                location.setText(testText, TextView.BufferType.SPANNABLE);
            }

            @Override
            public void fail() {
                Toast.makeText(activity_attendance.this, "wrong",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void GPS (View gps){
        Intent intent = new Intent();
        intent.setClass(activity_attendance.this,BaiDuMapActivity.class);
        account = intent.getStringExtra("account");
        startActivity(intent);
    }

}
