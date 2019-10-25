package com.yukino.myapplication;


import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
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

public class BaiDuMapActivity extends AppCompatActivity {
    /**
     * SDK
     */
    public LocationClient locationClient;
    /**
     * location monitor
     */
    public MyLocationListenner myListener = new MyLocationListenner();
    /**
     * BaiDu Map control
     */
    private MapView mapView;
    /**
     *  BaiDu Map object
     */

    private LocationMode mCurrentMode;
    BitmapDescriptor mCurrentMarker,othersCurrentMarker;
    private BaiduMap baiduMap;
    String studnt_id = MainActivity.account;
    double result = activity_attendance.result;
    private SensorManager mSensorManager;
    OnCheckedChangeListener radioButtonListener;
    Button requestLocButton;
    ToggleButton togglebtn = null;
    boolean isFirstLoc = true; // is it first get location

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_bai_du_map);
        //get BaiDu Map API
        judgePermission();
        mapView = (MapView) findViewById(R.id.bmapView);
        //get BaiDu map object
        baiduMap = mapView.getMap();
        // open BaiDu Map
        baiduMap.setMyLocationEnabled(true);
        /**
         * start
         */
        locationClient = new LocationClient(this);
        locationClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // open gps
        option.setCoorType("bd09ll"); // set type of location map, this is BaiDu map's number
        option.setScanSpan(1000);//time require
        option.setIsNeedAddress(true);
        locationClient.setLocOption(option);
        //open
        locationClient.start();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://47.102.105.203:8082")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        RetrofitAPI service = retrofit.create(RetrofitAPI.class);
        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/json");
        Call<String> teacherCall = service.getTeacherLocation(header);

        teacherCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if (response.isSuccessful()) {
                    String returnJson = response.body();
                    try {
                        JSONObject jsonObject = new JSONObject(returnJson);

                        if(jsonObject.getBoolean("status")) {
                            JSONArray locations = jsonObject.getJSONArray("locations");
                            StringBuilder sb = new StringBuilder();
                            double longtitude, latitude;
                            for (int i = 0; i < locations.length(); i++) {
                                longtitude = locations.getJSONObject(i).getDouble("longitude");
                                latitude = locations.getJSONObject(i).getDouble("latitude");
                                sb.append("longitude: " + longtitude + "\n");
                                sb.append("latitude: " + latitude + "\n\n");
                                LatLng dbPoint = new LatLng(latitude, longtitude);
                                CoordinateConverter converter = new CoordinateConverter()
                                        .from(CoordinateConverter.CoordType.GPS)
                                        .coord(dbPoint);
                                LatLng desLatLng = converter.convert();
                                addOthersLocation(desLatLng.longitude, desLatLng.latitude);
                                //addSelfLocation(self_longitude, self_latitude);
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
    }



    public void addOthersLocation(double longitude, double latitude) {
        //set point
        LatLng point = new LatLng(latitude, longitude);
        //set picture
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_marka2);
        OverlayOptions option = new MarkerOptions()
                .position(point)
                .icon(bitmap);
        //show maker on MAP
        baiduMap.addOverlay(option);
    }
    /**
     * SDK function
     */


    public class MyLocationListenner implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view destroy
            BaiduMap mBaiduMap = mapView.getMap();
            if (location == null || mapView == null) {
                return;
            }

            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            //baiduMap.setMyLocationData(locData);

            if(result == 1){
                LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
                BitmapDescriptor bitmap = BitmapDescriptorFactory
                        .fromResource(R.drawable.icon_marka);
                OverlayOptions option = new MarkerOptions()
                        .position(point)
                        .icon(bitmap);
                //show maker on MAP
                baiduMap.addOverlay(option);
            }else{
                LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
                BitmapDescriptor bitmap = BitmapDescriptorFactory
                        .fromResource(R.drawable.icon_marka1);
                OverlayOptions option = new MarkerOptions()
                        .position(point)
                        .icon(bitmap);
                //show maker on MAP
                baiduMap.addOverlay(option);
            }
//            LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
//                BitmapDescriptor bitmap = BitmapDescriptorFactory
//                        .fromResource(R.drawable.icon_marka3);
//                OverlayOptions option = new MarkerOptions()
//                        .position(point)
//                        .icon(bitmap);
//                //show maker on MAP
//                baiduMap.addOverlay(option);




            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());

                float f = mBaiduMap.getMaxZoomLevel();// 19.0
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll,
                        f - 2);
                mBaiduMap.animateMapStatus(u);
                //show map
                Toast.makeText(BaiDuMapActivity.this, location.getAddrStr(),
                        Toast.LENGTH_SHORT).show();

                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        }
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        // destroy location
        locationClient.stop();
        // close GPS
        baiduMap.setMyLocationEnabled(false);
        mapView.onDestroy();
        mapView = null;
        super.onDestroy();
    }

    protected void judgePermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] SdCardPermission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (ContextCompat.checkSelfPermission(this, SdCardPermission[0]) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, SdCardPermission, 100);
            }

            String[] readPhoneStatePermission = {Manifest.permission.READ_PHONE_STATE};
            if (ContextCompat.checkSelfPermission(this, readPhoneStatePermission[0]) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, readPhoneStatePermission, 200);
            }

            String[] locationPermission = {Manifest.permission.ACCESS_FINE_LOCATION};
            if (ContextCompat.checkSelfPermission(this, locationPermission[0]) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, locationPermission, 300);
            }

            String[] ACCESS_COARSE_LOCATION = {Manifest.permission.ACCESS_COARSE_LOCATION};
            if (ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION[0]) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, ACCESS_COARSE_LOCATION, 400);
            }


            String[] READ_EXTERNAL_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE};
            if (ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE[0]) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, READ_EXTERNAL_STORAGE, 500);
            }

            String[] WRITE_EXTERNAL_STORAGE = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE[0]) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, WRITE_EXTERNAL_STORAGE, 600);
            }

        }else{
        }
    }

}
