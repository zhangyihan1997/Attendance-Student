package com.yukino.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yukino.http.UserHttpController;


public class password extends AppCompatActivity {

    public TextView txtCurrentPassword;
    public TextView txtNewPassword;
    public TextView txtConfirmPassword;
    public static String CurrentPassword = MainActivity.password;
    public String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        txtCurrentPassword = (EditText) findViewById(R.id.txtCurrentPassword);
        txtNewPassword = (EditText) findViewById(R.id.txtNewPassword);
        txtConfirmPassword = (EditText) findViewById(R.id.txtConfirmPassword);

        Button mNameSignInButton = (Button) findViewById(R.id.sign_in_buttom1);

        mNameSignInButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                if(txtNewPassword.getText().toString().equalsIgnoreCase("") | txtConfirmPassword.getText().toString().equalsIgnoreCase("")| txtCurrentPassword.getText().toString().equalsIgnoreCase(""))
                {
                    Toast.makeText(getBaseContext(),"Please Complete the Information", Toast.LENGTH_SHORT).show();
                }
                else
                if(!txtNewPassword.getText().toString().equalsIgnoreCase(txtConfirmPassword.getText().toString()))
                {
                    Toast.makeText(getBaseContext(),
                            "These Passwords Don't Match !", Toast.LENGTH_SHORT).show();
                }
                else
                    if(!txtCurrentPassword.getText().toString().equalsIgnoreCase(CurrentPassword))
                    {
                        Toast.makeText(getBaseContext(),"Please input correct current password", Toast.LENGTH_SHORT).show();
                    }
                else{
                    password= txtConfirmPassword .getText().toString();
                    UserHttpController.StudentPassword(MainActivity.account, password, new UserHttpController.UserHttpControllerListener() {
                        @Override
                        public void success() {
                            Toast.makeText(password.this, "success",Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void fail() {
                            Toast.makeText(password.this, "wrong, please input different password",Toast.LENGTH_SHORT).show();
                        }
                    });
                    Intent intent = new Intent();
                    intent.setClass(password.this,MainActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}