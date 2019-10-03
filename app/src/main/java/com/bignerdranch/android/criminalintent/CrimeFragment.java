package com.bignerdranch.android.criminalintent;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;

import com.bignerdranch.android.criminalintent.face.patch.SafeFaceDetector;
import com.bignerdranch.android.criminalintent.face.photo.FaceView;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.File;
import java.util.Date;
import java.util.UUID;

public class CrimeFragment extends Fragment {

	private static final String ARG_CRIME_ID = "crime_id";
	private static final String DIALOG_DATE = "DialogDate";

	private static final int REQUEST_DATE = 0;
	private static final int REQUEST_CONTACT = 1;
	private static final int REQUEST_PHOTO = 2;

    private Crime mCrime;
    private Uri tempPhotoLocation;
    private File mPhotoFile;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckbox;
    private Button mReportButton;
    private Button mSuspectButton;
    private ImageButton mPhotoButton;
    private FaceView mPhotoView;
    private Button mGalaryButton;
    private CheckBox mFaceDetectCheckbox;
    private boolean yesDetect;
	private FaceDetector detector;
    private String photoFilePath;

	public static CrimeFragment newInstance(UUID crimeId) {
		Bundle args = new Bundle();
		args.putSerializable(ARG_CRIME_ID, crimeId);

		CrimeFragment fragment = new CrimeFragment();
		fragment.setArguments(args);
		return fragment;
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        tempPhotoLocation = null;
        mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);

		if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
				!= PackageManager.PERMISSION_GRANTED) {
			// Permission is not granted
			ActivityCompat.requestPermissions(getActivity(),
					new String[]{Manifest.permission.CAMERA},
					100);
		}
    }

	@Override
	public void onPause() {
		super.onPause();

		CrimeLab.get(getActivity())
				.updateCrime(mCrime);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_crime, container, false);

		mTitleField = (EditText) v.findViewById(R.id.crime_title);
		mTitleField.setText(mCrime.getTitle());
		mTitleField.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				mCrime.setTitle(s.toString());
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		mDateButton = (Button) v.findViewById(R.id.crime_date);
		updateDate();
		mDateButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentManager manager = getFragmentManager();
				DatePickerFragment dialog = DatePickerFragment
						.newInstance(mCrime.getDate());
				dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
				dialog.show(manager, DIALOG_DATE);
			}
		});

		mSolvedCheckbox = (CheckBox) v.findViewById(R.id.crime_solved);
		mSolvedCheckbox.setChecked(mCrime.isSolved());
		mSolvedCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mCrime.setSolved(isChecked);
			}
		});

        mFaceDetectCheckbox = (CheckBox) v.findViewById(R.id.checkBox);
        mFaceDetectCheckbox.setChecked(false);
        mFaceDetectCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                yesDetect = isChecked;
                updatePhotoView();
            }
        });

        mReportButton = (Button)v.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                i.putExtra(Intent.EXTRA_SUBJECT,
                        getString(R.string.crime_report_subject));
                i = Intent.createChooser(i, getString(R.string.send_report));

				startActivity(i);
			}
		});

		mGalaryButton = (Button)v.findViewById(R.id.crime_galary_button);
		mGalaryButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), CrimeImagesActivity.class);
				intent.putExtra("CRIME_UUID", mCrime.getId());
				intent.putExtra("yesFD", yesDetect);
				startActivity(intent);
			}
		});

		final Intent pickContact = new Intent(Intent.ACTION_PICK,
				ContactsContract.Contacts.CONTENT_URI);
		mSuspectButton = (Button) v.findViewById(R.id.crime_suspect);
		mSuspectButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startActivityForResult(pickContact, REQUEST_CONTACT);
			}
		});

		if (mCrime.getSuspect() != null) {
			mSuspectButton.setText(mCrime.getSuspect());
		}

		PackageManager packageManager = getActivity().getPackageManager();
		if (packageManager.resolveActivity(pickContact,
				PackageManager.MATCH_DEFAULT_ONLY) == null) {
			mSuspectButton.setEnabled(false);
		}

		mPhotoButton = (ImageButton) v.findViewById(R.id.crime_camera);
		final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        boolean canTakePhoto = mPhotoFile != null &&
                captureImage.resolveActivity(packageManager) != null;
        mPhotoButton.setEnabled(canTakePhoto);

        if (canTakePhoto) {
            Uri uri = Uri.fromFile(mPhotoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }

        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
                tempPhotoLocation = Uri.fromFile(getGeneratedPhotoLocation());
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, tempPhotoLocation);
                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });

        mPhotoView = (FaceView) v.findViewById(R.id.crime_photo);
		mPhotoView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if(photoFilePath != null) {
					Log.d("blah", photoFilePath);
					if (photoFilePath != null) {
						Intent intent = new Intent(getActivity(), PreviewActivity.class);
						intent.putExtra("photoFilePath", photoFilePath);
						intent.putExtra("yesFD", yesDetect);
						startActivity(intent);
					}
				}
			}
		});

        updatePhotoView();

		return v;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK) {
			return;
		}

		if (requestCode == REQUEST_DATE) {
			Date date = (Date) data
					.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
			mCrime.setDate(date);
			updateDate();
		} else if (requestCode == REQUEST_CONTACT && data != null) {
			Uri contactUri = data.getData();
			// Specify which fields you want your query to return
			// values for.
			String[] queryFields = new String[]{
					ContactsContract.Contacts.DISPLAY_NAME,
			};
			// Perform your query - the contactUri is like a "where"
			// clause here
			ContentResolver resolver = getActivity().getContentResolver();
			Cursor c = resolver
					.query(contactUri, queryFields, null, null, null);

			try {
				// Double-check that you actually got results
				if (c.getCount() == 0) {
					return;
				}

				// Pull out the first column of the first row of data -
				// that is your suspect's name.
				c.moveToFirst();

				String suspect = c.getString(0);
				mCrime.setSuspect(suspect);
				mSuspectButton.setText(suspect);
			} finally {
				c.close();
			}
		} else if (requestCode == REQUEST_PHOTO) {
			// save image filepath in database.
			CrimeLab.get(getActivity()).addImage(mCrime,tempPhotoLocation);
			//then update the small image on the crime scene.
			updatePhotoView();
		}
	}

	private void updateDate() {
		mDateButton.setText(mCrime.getDate().toString());
	}

	private String getCrimeReport() {
		String solvedString = null;
		if (mCrime.isSolved()) {
			solvedString = getString(R.string.crime_report_solved);
		} else {
			solvedString = getString(R.string.crime_report_unsolved);
		}
		String dateFormat = "EEE, MMM dd";
		String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();
		String suspect = mCrime.getSuspect();
		if (suspect == null) {
			suspect = getString(R.string.crime_report_no_suspect);
		} else {
			suspect = getString(R.string.crime_report_suspect, suspect);
		}
		String report = getString(R.string.crime_report,
				mCrime.getTitle(), dateString, solvedString, suspect);
		return report;
	}

    private void updatePhotoView() {
        for(File file : CrimeLab.get(getActivity()).getPhotoFiles(mCrime)) {
            if(file.exists()) {
                Bitmap bitmap = PictureUtils.getScaledBitmap(file.getPath(), getActivity());

				detector = new FaceDetector.Builder(getActivity())
						.setTrackingEnabled(false)
						.setLandmarkType(FaceDetector.ALL_LANDMARKS)
						.build();

				if(yesDetect) {
					updateImage(bitmap,detector,mPhotoView, true);
				} else {
					updateImage(bitmap,detector,mPhotoView, false);
				}

                photoFilePath = file.getPath();
                return;
            }
        }
    }

	private void updateImage(Bitmap bitmap, FaceDetector detector, FaceView faceView, boolean yesFd) {
		Detector<Face> safeDetector = new SafeFaceDetector(detector);
		// Create a frame from the bitmap and run face detection on the frame.
		Frame frame = new Frame.Builder().setBitmap(bitmap).build();
		SparseArray<Face> faces = safeDetector.detect(frame);
		Bitmap tmpImgBitmap = bitmap;

		if(faces.size() == 0) {
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

			for (int i = 0; i < maxRotations; i++) {
				bitmap = rotateBitmap(bitmap);
				frame = new Frame.Builder().setBitmap(bitmap).build();
				faces = safeDetector.detect(frame);
			}
		}

		if(yesFd) {
			faceView.setContent(bitmap,faces,false);
		} else {
			faceView.setContent(bitmap,new SparseArray<Face>(),false);
		}
		safeDetector.release();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if(detector != null)
			detector.release();
	}

	Bitmap rotateBitmap(Bitmap bitmapToRotate) {
		Matrix matrix = new Matrix();
		matrix.postRotate(90);
		return Bitmap.createBitmap(bitmapToRotate, 0, 0,
				bitmapToRotate.getWidth(), bitmapToRotate.getHeight(), matrix,
				true);
	}

	public File getGeneratedPhotoLocation() {
		File externalFilesDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
		if (externalFilesDir == null) {
			return null;
		}
		return new File(externalFilesDir, "IMG_" + System.currentTimeMillis() + ".jpg");
	}
}
