package com.bignerdranch.android.criminalintent;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;

import java.util.List;

public class CrimeImagesActivity extends AppCompatActivity {



        List<Crime> mCrimes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_images);


        GridView gridView = (GridView) findViewById(R.id.gridview);


        CrimeImageAdapter crimeImageAdapter = new CrimeImageAdapter(this,  mCrimes);
        gridView.setAdapter(crimeImageAdapter);



    }
}
