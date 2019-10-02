package com.bignerdranch.android.criminalintent;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;

import java.util.List;
import java.util.UUID;

public class CrimeImagesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_images);
        Crime mCrime = CrimeLab.get(this).getCrime((UUID) getIntent().getSerializableExtra("CRIME_UUID"));

        GridView gridView = (GridView) findViewById(R.id.gridview);

        CrimeImageAdapter crimeImageAdapter = new CrimeImageAdapter(this,  CrimeLab.get(this).getPhotoFiles(mCrime));
        gridView.setAdapter(crimeImageAdapter);



    }
}
