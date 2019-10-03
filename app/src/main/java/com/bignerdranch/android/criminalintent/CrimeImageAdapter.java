package com.bignerdranch.android.criminalintent;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.bignerdranch.android.criminalintent.face.patch.SafeFaceDetector;
import com.bignerdranch.android.criminalintent.face.photo.FaceView;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CrimeImageAdapter extends ArrayAdapter<File> {


	Context context;
	LayoutInflater inflater;
	List<File> mImageFiles;
	FaceDetector detector;
	boolean fd;

	/**
	 * Create new CrimeImageAdapter object
	 */

	public CrimeImageAdapter(Context context, ArrayList<File> files, FaceDetector detector, boolean fd) {
		super(context, 0, files);
		this.context = context;
		mImageFiles = files;
		inflater = (LayoutInflater.from(context));
		this.detector = detector;
		this.fd = fd;
	}


	@Override
	public int getCount() {
		return mImageFiles.size();
	}

	@Override
	public File getItem(int i) {
		return mImageFiles.get(i);
	}

	@Override
	public long getItemId(int i) {
		return 0;
	}

	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		ViewHolder holder = null;
		if (view == null) {
			view = inflater.inflate(R.layout.crime_image, viewGroup, false); // inflate the layout
			holder = new ViewHolder();
			holder.image = (FaceView) view.findViewById(R.id.faceView); // get the reference of ImageView
		} else {
			holder = (ViewHolder) view.getTag();
		}
		File file = mImageFiles.get(i);
		holder.file = file;
		view.setTag(holder);
		Bitmap bitmap = PictureUtils.getScaledBitmap(file.getPath(), Integer.MAX_VALUE, Integer.MAX_VALUE);

		if(fd) {
			updateImage(bitmap,detector,holder.image, true);
		} else {
			updateImage(bitmap,detector,holder.image, false);
		}
		return view;
	}

	public static class ViewHolder {
		File file;
		FaceView image;
	}

	private void updateImage(Bitmap bitmap, FaceDetector detector, FaceView faceView, boolean yesFd) {
		Detector<Face> safeDetector = new SafeFaceDetector(detector);
		// Create a frame from the bitmap and run face detection on the frame.
		Frame frame = new Frame.Builder().setBitmap(bitmap).build();
		SparseArray<Face> faces = safeDetector.detect(frame);
		Bitmap tmpImgBitmap = bitmap;

		int rotations = 0;
		int numFaces;
		int max = 0;
		int maxRotations = 0;
		while (rotations < 3) {
			rotations++;
			tmpImgBitmap = rotateBitmap(tmpImgBitmap);
			frame = new Frame.Builder().setBitmap(tmpImgBitmap).build();
			faces = safeDetector.detect(frame);
			numFaces = faces.size();
			if (max <= numFaces) {
				max = numFaces;
				maxRotations = rotations;
			}
		}

		for (int i=0; i<maxRotations; i++)
		{
			bitmap = rotateBitmap(bitmap);
			frame = new Frame.Builder().setBitmap(bitmap).build();
			faces = safeDetector.detect(frame);
		}

		if(yesFd) {
			faceView.setContent(bitmap,faces,false);
		} else {
			faceView.setContent(bitmap,new SparseArray<Face>(),false);
		}
	}

	Bitmap rotateBitmap(Bitmap bitmapToRotate) {
		Matrix matrix = new Matrix();
		matrix.postRotate(90);
		return Bitmap.createBitmap(bitmapToRotate, 0, 0,
				bitmapToRotate.getWidth(), bitmapToRotate.getHeight(), matrix,
				true);
	}
}
