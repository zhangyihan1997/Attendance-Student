package com.yukino.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yukino.http.UserHttpController;



public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_READ_CONTACTS = 0;

    EditText mText;
    private LocationManager lm;
    private static final String TAG = "MainActivity";
    private EditText mNameView;
    private EditText mPasswordView;
    private CheckBox rememberPass;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    public static String account;
    public String account1;
    public static String password;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        mNameView = (EditText) findViewById(R.id.name);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
        rememberPass = (CheckBox) findViewById(R.id.ck);

        boolean isRemember=preferences.getBoolean("remember_password",false);
        if(isRemember){
            String account=preferences.getString("name","");
            String password=preferences.getString("password","");
            mNameView.setText(account);
            mPasswordView.setText(password);
            rememberPass.setChecked(true);
        }
        account = preferences.getString("name", "");
        password = preferences.getString("password", "");
        mNameView.setText(account);
        mPasswordView.setText(password);


        //登录按钮
        Button mNameSignInButton = (Button) findViewById(R.id.sign_in_buttom);
        mNameSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //Bundle data = new Bundle();
                account = mNameView.getText().toString();
                account1 = mNameView.getText().toString();
                password = mPasswordView.getText().toString();
                UserHttpController.UserCheck1(account, password, new UserHttpController.UserHttpControllerListener() {
                    @Override
                    public void success() {
                        editor = preferences.edit();
                        if(rememberPass.isChecked()){
                            editor.putBoolean("remember_password",true);
                            editor.putString("name",account);
                            editor.putString("password",password);
                        }
                        else{
                            editor.clear();
                        }
                        editor.apply();
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);

                        intent.putExtra("account", account);
                        intent.putExtra("password", password);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void fail() {
                        Toast.makeText(MainActivity.this, "wrong, please input right password",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void populateAutoComplete() {
        getLoaderManager().initLoader(0, null, null);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    private void attemptLogin() {
        mNameView.setError(null);
        mPasswordView.setError(null);
        // Store values at the time of the login attempt.。
        String student_id = mNameView.getText().toString();
        String password = mPasswordView.getText().toString();
        editor = preferences.edit();
        if (rememberPass.isChecked()) {
            editor.putBoolean("remember_password", true);
            editor.putString("Name", student_id);
            editor.putString("password", password);
        } else {
            editor.clear();
        }
        editor.apply();

    }

}