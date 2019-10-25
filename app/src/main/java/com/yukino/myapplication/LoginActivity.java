package com.yukino.myapplication;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class LoginActivity extends Local {

    //public String account;
    String studnt_id = MainActivity.account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Intent intent = getIntent();
        account = intent.getStringExtra("account");
    }

    public void Local (View Local){
        Intent intent = new Intent();
        intent.setClass(LoginActivity.this,Local.class);
        account = intent.getStringExtra("account");
        startActivity(intent);
    }

    public void GPS (View gps){
        Intent intent = new Intent();
        intent.setClass(LoginActivity.this,BaiDuMapActivity.class);
        account = intent.getStringExtra("account");
        startActivity(intent);
    }

    public void Attendance (View Attendance){
        Intent intent = new Intent();
        intent.setClass(LoginActivity.this,activity_attendance.class);
        account = intent.getStringExtra("account");
        startActivity(intent);
    }

    public void course (View course){
        Intent intent = new Intent();
        intent.setClass(LoginActivity.this,course.class);
        startActivity(intent);
    }

    public void password (View pass){
        Intent intent = new Intent();
        intent.setClass(LoginActivity.this,password.class);
        account = intent.getStringExtra("account");
        startActivity(intent);
    }


}
