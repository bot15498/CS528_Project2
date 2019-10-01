package com.bignerdranch.android.criminalintent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;

public class PreviewActivity extends AppCompatActivity {

    ImageView preview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        preview = (ImageView) findViewById(R.id.imageView);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String imgFilePath = extras.getString("photoFilePath");
            if(imgFilePath != null) {
                Log.d(getClass().getName(), extras.getString("photoFilePath"));
                File imgFile = new File(imgFilePath);
                Bitmap imgBitMap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                preview.setImageBitmap(imgBitMap);
            }
        }
    }

}
