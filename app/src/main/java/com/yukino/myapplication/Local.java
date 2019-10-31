package com.yukino.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.yukino.http.UserHttpController;

public class Local extends AppCompatActivity{
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    TextView mText;
    private LocationManager lm;
    private static final String TAG = "Local";
    public String account;
    public static double lng;
    public static double lat;


    String bestProvider;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e(TAG,"onRequestPermissionsResult");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (lm != null) {
            lm.removeGpsStatusListener(listener);
        }
    }

    //位置监听
    public LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            updateView(location, account);
            Log.e(TAG, "time：" + location.getTime());
            Log.e(TAG, "longitude：" + location.getLongitude());
            Log.e(TAG, "latitude：" + location.getLatitude());
        }

        /**
         * GPS change
         */
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG,"onStatusChanged status=" + status);
            switch (status) {
                //GPS status is visible
                case LocationProvider.AVAILABLE:
                    Log.i(TAG, "GPS status is visible");
                    break;
                //GPS status is outside the service area
                case LocationProvider.OUT_OF_SERVICE:
                    Log.i(TAG, "GPS status is outside the service area");
                    break;
                //GPS status is suspended service
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.i(TAG, "GPS status is suspended service");
                    break;
            }
        }
        /**
         * GPS open
         */
        public void onProviderEnabled(String provider) {
            Log.e(TAG,"onProviderEnabled");
            if (Build.VERSION.SDK_INT >= 23) {
                int checkPermission = ContextCompat.checkSelfPermission(Local.this, Manifest.permission.ACCESS_COARSE_LOCATION);
                if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Local.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    //ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    Log.d("T", "warning");
                }
            }
            Location location = lm.getLastKnownLocation(provider);
            updateView(location, account);
        }
        /**
         * GPSstop
         */
        public void onProviderDisabled(String provider) {
            updateView(null, account);
        }

    };
    //状态监听
    GpsStatus.Listener listener = new GpsStatus.Listener() {
        public void onGpsStatusChanged(int event) {
            Log.e(TAG,"onGpsStatusChanged event =" + event);
            switch (event) {
                //first local
                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    Log.i(TAG, "first location");
                    break;
                //Satellite status change
            }
        }
    };
    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= 23) {
            int checkPermission = ContextCompat.checkSelfPermission(Local.this, Manifest.permission.ACCESS_COARSE_LOCATION);
            if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(Local.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                ActivityCompat.requestPermissions(Local.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                Log.d("T", "warning");
            }
        }
        //set query conditions to obtain geographic location information.
        bestProvider = lm.getBestProvider(getCriteria(), true);
        Location location;
        location = lm.getLastKnownLocation(bestProvider);
        updateView(location, account);
        //Listening state
        lm.addGpsStatusListener(listener);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local);

        Button photo = (Button) findViewById(R.id.photo);
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Local.this, Add_photo.class);
                startActivity(i);
            }
        });
        initView();
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

    }
    private void initView() {
        mText = (TextView) this.findViewById(R.id.showresult);
    }

    /**
     *
     *
     * @param location
     */
    private void updateView(Location location, String account) {
        if (location != null) {
            mText.setText("Location：\n\nLongitude：");
            mText.append(String.valueOf(location.getLongitude()));
            lng = location.getLongitude();
            mText.append("\nLatitude：");
            mText.append(String.valueOf(location.getLatitude()));
            lat = location.getLatitude();
            UserHttpController.SendLocation(account, String.valueOf(location.getLongitude()),
                    String.valueOf(location.getLatitude()), String.valueOf(location.getTime()), new UserHttpController.UserHttpControllerListener() {
                        @Override
                        public void success() {
                            Toast.makeText(Local.this, "upload location succeed",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void fail() {
                        }
                    });
        } else {
            //clear EditText object
            mText.getEditableText().clear();
        }
    }

    /**
     * return result
     *
     * @return
     */
    private Criteria getCriteria() {
        Criteria criteria = new Criteria();
        //Set positioning accuracy
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        //set speed
        criteria.setSpeedRequired(false);
        // set cost
        criteria.setCostAllowed(false);
        //set position
        criteria.setBearingRequired(false);
        //set altitude
        criteria.setAltitudeRequired(false);
        //set power require
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        return criteria;
    }
}
