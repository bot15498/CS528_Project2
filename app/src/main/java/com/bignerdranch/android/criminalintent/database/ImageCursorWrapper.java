package com.bignerdranch.android.criminalintent.database;

import android.database.Cursor;
import android.database.CursorWrapper;

public class ImageCursorWrapper extends CursorWrapper {
	public ImageCursorWrapper(Cursor cursor) {
		super(cursor);
	}

	public String getImageFilepath() {
		return getString(getColumnIndex(CrimeDbSchema.CrimeImageTable.Cols.FILEPATH));
	}
}
