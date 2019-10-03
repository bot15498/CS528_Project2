package com.bignerdranch.android.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.List;
import java.util.UUID;

public class CrimeImagesActivity extends AppCompatActivity {

    private boolean yesDetect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_images);
        Crime mCrime = CrimeLab.get(this).getCrime((UUID) getIntent().getSerializableExtra("CRIME_UUID"));
        yesDetect = getIntent().getBooleanExtra("yesFD", false);

        GridView gridView = (GridView) findViewById(R.id.gridview);

        CrimeImageAdapter crimeImageAdapter = new CrimeImageAdapter(this,  CrimeLab.get(this).getPhotoFiles(mCrime));
        gridView.setAdapter(crimeImageAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CrimeImageAdapter.ViewHolder holder = (CrimeImageAdapter.ViewHolder) view.getTag();
                if (holder.file != null) {
                    Intent intent = new Intent(getBaseContext(), PreviewActivity.class);
                    intent.putExtra("photoFilePath", holder.file.getPath());
                    intent.putExtra("yesFD", yesDetect);
                    startActivity(intent);
                }
            }
        });



    }
}
