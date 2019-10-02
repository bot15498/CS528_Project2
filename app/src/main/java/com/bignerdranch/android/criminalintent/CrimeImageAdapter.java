package com.bignerdranch.android.criminalintent;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

public class CrimeImageAdapter extends ArrayAdapter<File> {


	Context context;
	LayoutInflater inflater;
	List<File> mImageFiles;

	/**
	 * Create new CrimeImageAdapter object
	 */

	public CrimeImageAdapter(Context context, ArrayList<File> files) {
		super(context, 0, files);
		this.context = context;
		mImageFiles = files;
		inflater = (LayoutInflater.from(context));
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
			holder.image = (ImageView) view.findViewById(R.id.crimeimage); // get the reference of ImageView
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		File file = mImageFiles.get(i);
		Bitmap bitmap = PictureUtils.getScaledBitmap(file.getPath(), holder.image.getMaxWidth(), holder.image.getMaxHeight());
        holder.image.setImageBitmap(bitmap);

		return view;


	}

	static class ViewHolder {
		ImageView image;
	}
}
