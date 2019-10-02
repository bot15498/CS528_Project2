package com.bignerdranch.android.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Environment;

import com.bignerdranch.android.criminalintent.database.CrimeBaseHelper;
import com.bignerdranch.android.criminalintent.database.CrimeCursorWrapper;

import com.bignerdranch.android.criminalintent.database.CrimeDbSchema;
import com.bignerdranch.android.criminalintent.database.CrimeDbSchema.CrimeTable;
import com.bignerdranch.android.criminalintent.database.ImageCursorWrapper;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CrimeLab {
	private static CrimeLab sCrimeLab;

	private Context mContext;
	private SQLiteDatabase mDatabase;

	public static CrimeLab get(Context context) {
		if (sCrimeLab == null) {
			sCrimeLab = new CrimeLab(context);
		}
		return sCrimeLab;
	}

	private CrimeLab(Context context) {
		mContext = context.getApplicationContext();
		mDatabase = new CrimeBaseHelper(mContext)
				.getWritableDatabase();
	}


	public void addCrime(Crime c) {
		ContentValues values = getContentValues(c);

		mDatabase.insert(CrimeTable.NAME, null, values);
	}

	public List<Crime> getCrimes() {
		List<Crime> crimes = new ArrayList<>();

		CrimeCursorWrapper cursor = queryCrimes(null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			crimes.add(cursor.getCrime());
			cursor.moveToNext();
		}
		cursor.close();

		return crimes;
	}

	public Crime getCrime(UUID id) {
		CrimeCursorWrapper cursor = queryCrimes(
				CrimeTable.Cols.UUID + " = ?",
				new String[]{id.toString()}
		);

		try {
			if (cursor.getCount() == 0) {
				return null;
			}

			cursor.moveToFirst();
			return cursor.getCrime();
		} finally {
			cursor.close();
		}
	}

	public ArrayList<File> getPhotoFiles(Crime crime) {
		ArrayList<File> photoFiles = new ArrayList<>();
		ImageCursorWrapper cursor = queryCrimeImages(
				CrimeDbSchema.CrimeImageTable.Cols.CRIMEID + " = ?",
				new String[]{crime.getId().toString()});

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			photoFiles.add(new File(cursor.getImageFilepath()));
			cursor.moveToNext();
		}
		cursor.close();

		return photoFiles;
	}

	public void addImage(Crime crime, Uri photoUri) {
		ContentValues values = new ContentValues();
		values.put(CrimeDbSchema.CrimeImageTable.Cols.CRIMEID, crime.getId().toString());
		values.put(CrimeDbSchema.CrimeImageTable.Cols.FILEPATH, photoUri.getPath());
		mDatabase.insert(CrimeDbSchema.CrimeImageTable.NAME,null,values);
	}

	public void updateCrime(Crime crime) {
		String uuidString = crime.getId().toString();
		ContentValues values = getContentValues(crime);

		mDatabase.update(CrimeTable.NAME, values,
				CrimeTable.Cols.UUID + " = ?",
				new String[]{uuidString});
	}

	private static ContentValues getContentValues(Crime crime) {
		ContentValues values = new ContentValues();
		values.put(CrimeTable.Cols.UUID, crime.getId().toString());
		values.put(CrimeTable.Cols.TITLE, crime.getTitle());
		values.put(CrimeTable.Cols.DATE, crime.getDate().getTime());
		values.put(CrimeTable.Cols.SOLVED, crime.isSolved() ? 1 : 0);
		values.put(CrimeTable.Cols.SUSPECT, crime.getSuspect());

		return values;
	}

	private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs) {
		Cursor cursor = mDatabase.query(
				CrimeTable.NAME,
				null, // Columns - null selects all columns
				whereClause,
				whereArgs,
				null, // groupBy
				null, // having
				null  // orderBy
		);

		return new CrimeCursorWrapper(cursor);
	}

	private ImageCursorWrapper queryCrimeImages(String whereClause, String[] whereArgs) {
		Cursor cursor = mDatabase.query(
				CrimeDbSchema.CrimeImageTable.NAME,
				null, // Columns - null selects all columns
				whereClause,
				whereArgs,
				null, // groupBy
				null, // having
				null  // orderBy
		);

		return new ImageCursorWrapper(cursor);
	}
}
