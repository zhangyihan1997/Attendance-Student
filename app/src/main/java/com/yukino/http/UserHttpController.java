package com.yukino.http;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.TextView;

import com.yukino.myapplication.MainActivity;
import com.yukino.utils.RetrofitAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class UserHttpController {
    public interface UserHttpControllerListener {
        void success();

        void fail();
    }

    public static int result;

    public static void UserCheck1(String student_id, String password, UserHttpControllerListener userHttpControllerListener) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://47.102.105.203:8082")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        RetrofitAPI service = retrofit.create(RetrofitAPI.class);
        Map<String, String> header = new HashMap<>();
        Map<String, String> body = new HashMap<>();
        header.put("Content-Type", "application/json");
        body.put("student_id", student_id);
        body.put("password", password);
        Call<String> studentCall = service.checkStudent(header, body);

        studentCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    String returnJson = response.body();
                    try {
                        JSONObject jsonObject = new JSONObject(returnJson);

                        if(jsonObject.getBoolean("status")) {
                            userHttpControllerListener.success();
                        } else {
                            Log.i("call failed", "diu lei lou mou");
                            userHttpControllerListener.fail();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.i("call failed", "Stack");
                        userHttpControllerListener.fail();
                    }
                } else {
                    Log.i("call failed", response.body());
                    userHttpControllerListener.fail();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                Log.i("call failed", throwable.getMessage());
                userHttpControllerListener.fail();
            }
        });
    }

    public static void SendLocation(String student_id, String Longitude, String Latitude, String time, UserHttpControllerListener userHttpControllerListener) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://47.102.105.203:8082")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        RetrofitAPI service = retrofit.create(RetrofitAPI.class);
        Map<String, String> header = new HashMap<>();
        Map<String, String> body = new HashMap<>();
        header.put("Content-Type", "application/json");
        body.put("student_id", student_id);
        body.put("longitude", Longitude);
        body.put("latitude", Latitude);
        body.put("time", time);
        Call<String> studentCall = service.addStudentLocation(header, body);

        studentCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    String returnJson = response.body();
                    try {
                        JSONObject jsonObject = new JSONObject(returnJson);

                        if(jsonObject.getBoolean("status")) {
                            userHttpControllerListener.success();
                        } else {
                            Log.i("call failed", "diu lei lou mou");
                            userHttpControllerListener.fail();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.i("call failed", "Stack");
                        userHttpControllerListener.fail();
                    }
                } else {
                    Log.i("call failed", response.toString());
                    userHttpControllerListener.fail();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                Log.i("call failed", throwable.getMessage());
                userHttpControllerListener.fail();
            }
        });
    }


    public static void GetLocation(String account, TextView textView, UserHttpControllerListener userHttpControllerListener) {
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
                                longtitude = locations.getJSONObject(i).getDouble("longitude");
                                latitude = locations.getJSONObject(i).getDouble("latitude");
                                attendance = locations.getJSONObject(i).getInt("attendance");
                                result = attendance;
                                if(student_id.equals(account)) {
                                    sb.append("student_id: " + student_id + "\n");
                                    sb.append("longitude: " + longtitude + "\n");
                                    sb.append("latitude: " + latitude + "\n");
                                    sb.append("result: " + attendance + "\n\n");
                                }
                            }
                            textView.setText(sb.toString());
                        } else {
                            Log.i("call failed", "diu lei lou mou");
                            userHttpControllerListener.fail();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.i("call failed", "Stack");
                        userHttpControllerListener.fail();
                    }
                } else {
                    Log.i("call failed", response.toString());
                    userHttpControllerListener.fail();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                Log.i("call failed", throwable.getMessage());
                //userHttpControllerListener.fail();
            }
        });
    }

    public static void GetCourse1(TextView textView, UserHttpControllerListener userHttpControllerListener) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://47.102.105.203:8082")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        RetrofitAPI service = retrofit.create(RetrofitAPI.class);
        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", "text/plain");
        //use get result method
        Call<String> courseCall = service.GetCourse1(header);

        courseCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if (response.isSuccessful()) {
                    String returnJson = response.body();
                    try {
                        JSONObject jsonObject = new JSONObject(returnJson);
                        if(jsonObject.getBoolean("status")) {
                            JSONArray course = jsonObject.getJSONArray("course");
                            StringBuilder sb = new StringBuilder();
                            //get the result from database and add them to form a text
                            int student_id;
                            String classroom, name, teacher, time;
                            for (int i = 0; i < course.length(); i++) {
                                student_id = course.getJSONObject(i).getInt("student_id");
                                String studentid = String.valueOf(student_id);
                                if(MainActivity.account.equals(studentid)) {
                                    classroom = course.getJSONObject(i).getString("classroom");
                                    time = course.getJSONObject(i).getString("time");
                                    name = course.getJSONObject(i).getString("name");
                                    teacher = course.getJSONObject(i).getString("teacher");
                                    sb.append("classroom: " + classroom + "\n");
                                    sb.append("name:" + name + "\n");
                                    sb.append("teacher:" + teacher + "\n");
                                    sb.append("time:" + time + "\n\n");
                                }
                                else{
                                    sb.append("no class\n");
                                }
                            }
                            textView.setText(sb.toString());
                        } else {
                            Log.i("call failed", "diu lei lou mou");
                            userHttpControllerListener.fail();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.i("call failed", "Stack");
                        userHttpControllerListener.fail();
                    }
                } else {
                    Log.i("call failed", response.toString());
                    userHttpControllerListener.fail();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                Log.i("call failed", throwable.getMessage());
                //userHttpControllerListener.fail();
            }
        });
    }

    public static void StudentPassword(String student_id,  String password, UserHttpControllerListener userHttpControllerListener) {
        //connect to server
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://47.102.105.203:8082")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        RetrofitAPI service = retrofit.create(RetrofitAPI.class);
        Map<String, String> header = new HashMap<>();
        Map<String, String> body = new HashMap<>();
        header.put("Content-Type", "application/json");
        body.put("student_id", student_id);
        body.put("password", password);
        //use addteacherlocation method
        Call<String> studentCall = service.changestudentpassword(header, body);

        studentCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    String returnJson = response.body();
                    try {
                        JSONObject jsonObject = new JSONObject(returnJson);

                        if(jsonObject.getBoolean("status")) {
                            userHttpControllerListener.success();
                        } else {
                            Log.i("call failed", "diu lei lou mou");
                            userHttpControllerListener.fail();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.i("call failed", "Stack");
                        userHttpControllerListener.fail();
                    }
                } else {
                    Log.i("call failed", response.toString());
                    userHttpControllerListener.fail();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                Log.i("call failed", throwable.getMessage());
                userHttpControllerListener.fail();
            }
        });
    }


    public static void uploadImage(Bitmap image, String student_id, UserHttpControllerListener userHttpControllerListener){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://47.102.105.203:8082")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), byteArray);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("upload", student_id + ".jpg", fileBody);

        RetrofitAPI service = retrofit.create(RetrofitAPI.class);

        Call<ResponseBody> ImageCall = service.uploadImage(fileToUpload, fileBody);
        ImageCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    String returnJson = response.body().toString();
                    try {
                        JSONObject jsonObject = new JSONObject(returnJson);

                        if(jsonObject.getBoolean("status")) {
                            userHttpControllerListener.success();
                        } else {
                            Log.i("call failed", "diu lei lou mou");
                            userHttpControllerListener.fail();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.i("call failed", "Stack");
                        userHttpControllerListener.fail();
                    }
                } else {
                    Log.i("call failed", response.toString());
                    userHttpControllerListener.fail();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i("call failed", t.getMessage());
                userHttpControllerListener.fail();
            }
        });
    }
}
