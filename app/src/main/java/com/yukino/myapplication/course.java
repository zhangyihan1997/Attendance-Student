package com.yukino.myapplication;

import android.os.Bundle;
import android.text.SpannableString;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.app.AppCompatActivity;
import com.yukino.http.UserHttpController;

public class course extends AppCompatActivity {

    public TextView course;
    String studnt_id = MainActivity.account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        course = (TextView) findViewById((R.id.course));
        //use scroll bar
        //use get result method to get all student attendance result in database

        UserHttpController.GetCourse1(course, new UserHttpController.UserHttpControllerListener() {
            SpannableString testText = new SpannableString("JOJO");

            @Override
            public void success() {
                Toast.makeText(course.this, "succeed",Toast.LENGTH_SHORT).show();
                //set to text to get all info in the database
                course.setText(testText, TextView.BufferType.SPANNABLE);
            }

            @Override
            public void fail() {
                Toast.makeText(course.this, "wrong",Toast.LENGTH_SHORT).show();
            }
        });

    }

}
