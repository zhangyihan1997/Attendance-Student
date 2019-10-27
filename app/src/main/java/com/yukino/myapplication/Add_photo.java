package com.yukino.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.yukino.http.UserHttpController;

import java.io.File;
import java.io.IOException;

public class Add_photo extends AppCompatActivity {


    /**
     * Called when the activity is first created.
     */
    // open camera
    Button but, upload_image;
    String student_id = MainActivity.account;
    ImageView img;

    private Uri imageURI;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo);

        but = findViewById(R.id.my_camare_button);
        upload_image = findViewById(R.id.upload_image);
        img = findViewById(R.id.my_img_view);
        //take photo

        but.setOnClickListener(v -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getPackageManager()) != null) {
                try {
                    imageURI = FileProvider.getUriForFile(Add_photo.this, "com.yukino.myapplication.provider",
                            File.createTempFile("temp_img", ".jpg", getExternalFilesDir(Environment.DIRECTORY_PICTURES)));
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI);
                    startActivityForResult(intent, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(Add_photo.this, "No camera found", Toast.LENGTH_LONG).show();
            }
        });


        //upload
        upload_image.setOnClickListener((View v)-> {

                // TODO Auto-generated method stub
                if(imageURI != null)
                {
                    Glide.with(Add_photo.this)
                            .asBitmap()
                            .load(imageURI)
                            .into(new CustomTarget<Bitmap>() {

                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    UserHttpController.uploadImage(resource, student_id, new UserHttpController.UserHttpControllerListener() {
                                        @Override
                                        public void success() {
                                            Toast.makeText(Add_photo.this, "succeed to upload",Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent();
                                            intent.setClass(Add_photo.this,LoginActivity.class);
                                            startActivity(intent);
                                        }
                                        @Override
                                        public void fail() {
                                            Toast.makeText(Add_photo.this, "upload success",Toast.LENGTH_SHORT).show();

                                            AlertDialog.Builder builder = new AlertDialog.Builder(Add_photo.this);
                                            builder.setTitle("Add Photo")
                                                    .setMessage("Upload Succeed.")
                                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog,
                                                                            int which) {
                                                            Intent intent = new Intent();
                                                            intent.setClass(Add_photo.this,LoginActivity.class);
                                                            startActivity(intent);
                                                        }
                                                    })
                                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog,
                                                                            int which) {
                                                        }
                                                    })
                                                    .show();
                                        }
                                    });
                                }
                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {

                            }
                        });

                    Toast.makeText(Add_photo.this, "uploading....",
                            Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(Add_photo.this, "please take photo first....",
                            Toast.LENGTH_LONG).show();
                }

        });
    }

    //show photo after use camera
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            img.setImageURI(imageURI);
        } else {

        }
    }


}
