package com.bignerdranch.android.criminalintent;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.widget.ImageView;
import android.widget.Toast;

import com.bignerdranch.android.criminalintent.face.patch.SafeFaceDetector;
import com.bignerdranch.android.criminalintent.face.photo.FaceView;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.File;

public class PreviewActivity extends AppCompatActivity {

    private ImageView imgDisplay;
    private FaceDetector detector;
    private Bitmap editedBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

//        imgDisplay = (ImageView) findViewById(R.id.imgDisplay);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String photoFilePath = extras.getString("photoFilePath");
            if (photoFilePath != null) {
                Log.d(getClass().getName(), photoFilePath);
                File imgFile = new File(photoFilePath);
                Bitmap imgBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//                imgDisplay.setImageBitmap(imgBitmap);

                detector = new FaceDetector.Builder(getApplicationContext())
                        .setTrackingEnabled(false)
                        .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                        .build();

                Detector<Face> safeDetector = new SafeFaceDetector(detector);

                // Create a frame from the bitmap and run face detection on the frame.
                Frame frame = new Frame.Builder().setBitmap(imgBitmap).build();
                SparseArray<Face> faces = safeDetector.detect(frame);

                if (faces.size() == 0) {
                    Toast.makeText(getApplicationContext(), "No faces detected", Toast.LENGTH_LONG).show();
                }

                if (!safeDetector.isOperational()) {
                    Log.w(getClass().getName(), "Face detector dependencies are not yet available.");

                    // Check for low storage.  If there is low storage, the native library will not be
                    // downloaded, so detection will not become operational.
                    IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
                    boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;

                    if (hasLowStorage) {
                        Toast.makeText(this, "Low storage", Toast.LENGTH_LONG).show();
                    }
                }

                FaceView overlay = (FaceView) findViewById(R.id.faceView);
                overlay.setContent(imgBitmap, faces);

                safeDetector.release();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        detector.release();
    }

}
