package com.bignerdranch.android.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.bignerdranch.android.criminalintent.face.patch.SafeFaceDetector;
import com.bignerdranch.android.criminalintent.face.photo.FaceView;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.util.List;
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

//	private SparseArray<Face> findFaces(Bitmap bitmap) {
//		Detector<Face> safeDetector = new SafeFaceDetector(detector);
//// Create a frame from the bitmap and run face detection on the frame.
//		Frame frame = new Frame.Builder().setBitmap(bitmap).build();
//		SparseArray<Face> faces = safeDetector.detect(frame);
//
//		int rotations = 0;
//		int numFaces;
//		int max = 0;
//		int maxRotations = 0;
//		while (rotations < 3) {
//			Bitmap tmpImgBitmap = rotateBitmap(bitmap);
//			frame = new Frame.Builder().setBitmap(tmpImgBitmap).build();
//			faces = safeDetector.detect(frame);
//			numFaces = faces.size();
//			if (max <= numFaces) {
//				max = numFaces;
//				maxRotations = rotations;
//			}
//			rotations++;
//		}
//
//		for (int i=0; i<=maxRotations; i++)
//		{
//			bitmap = rotateBitmap(bitmap);
//			frame = new Frame.Builder().setBitmap(bitmap).build();
//			faces = safeDetector.detect(frame);
//		}
//
//		if (!safeDetector.isOperational()) {
//			Log.w(getClass().getName(), "Face detector dependencies are not yet available.");
//
//			// Check for low storage.  If there is low storage, the native library will not be
//			// downloaded, so detection will not become operational.
//			IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
//			boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;
//
//			if (hasLowStorage) {
//				Toast.makeText(this, "Low storage", Toast.LENGTH_LONG).show();
//			}
//		}
//		return faces;
//	}
//
//	Bitmap rotateBitmap(Bitmap bitmapToRotate) {
//		Matrix matrix = new Matrix();
//		matrix.postRotate(90);
//		return Bitmap.createBitmap(bitmapToRotate, 0, 0,
//				bitmapToRotate.getWidth(), bitmapToRotate.getHeight(), matrix,
//				true);
//	}
}
