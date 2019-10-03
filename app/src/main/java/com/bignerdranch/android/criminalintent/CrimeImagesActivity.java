package com.bignerdranch.android.criminalintent;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import com.google.android.gms.vision.face.FaceDetector;
import java.util.UUID;

public class CrimeImagesActivity extends AppCompatActivity {

	private boolean yesDetect;
	private FaceDetector detector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_crime_images);
		Crime mCrime = CrimeLab.get(this).getCrime((UUID) getIntent().getSerializableExtra("CRIME_UUID"));
		yesDetect = getIntent().getBooleanExtra("yesFD", false);

		GridView gridView = (GridView) findViewById(R.id.gridview);

		detector = new FaceDetector.Builder(this)
				.setTrackingEnabled(false)
				.setLandmarkType(FaceDetector.ALL_LANDMARKS)
				.build();

		CrimeImageAdapter crimeImageAdapter = new CrimeImageAdapter(this, CrimeLab.get(this).getPhotoFiles(mCrime), detector, yesDetect);
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

	@Override
	protected void onDestroy() {
		super.onDestroy();
		detector.release();
	}
}
