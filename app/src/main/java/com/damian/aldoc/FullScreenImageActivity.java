package com.damian.aldoc;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class FullScreenImageActivity extends Activity {

    ImageView image_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        image_view = (ImageView)findViewById(R.id.fullScreenImage_imageView);

        Intent intent = getIntent();
        String image_database_uri = intent.getStringExtra("uri");

        Database.StorageDownloadAndDisplayInContextImage(this, Uri.parse(image_database_uri),image_view);
    }
}
