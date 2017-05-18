package com.damian.aldoc;

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

public class FullScreenImageActivity extends AppCompatActivity {

    ImageView image_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        image_view = (ImageView)findViewById(R.id.fullScreenImage_imageView);

        Intent intent = getIntent();
        Uri image_uri = (Uri)intent.getData();

        if(image_view != null && image_uri != null)
        {
            InputStream image_stream;
            Bitmap image_bitmap;

            try{
                image_stream = getContentResolver().openInputStream(image_uri);
                image_bitmap = BitmapFactory.decodeStream(image_stream);
                image_view.setImageBitmap(image_bitmap);
            }catch (FileNotFoundException e)
            {
                Toast.makeText(this, "Nie udało się załadować obrazu. Spróbuj ponownie.", Toast.LENGTH_LONG).show();
                finish();
            }
        }
        else
            finish();
    }
}
