package com.bignerdranch.android.criminalintent;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CrimeImageAdapter extends BaseAdapter {



    Context context;
    LayoutInflater inflater;
    List<Crime> mCrimes;

    /**
     * Create new CrimeImageAdapter object
     */

    public CrimeImageAdapter(Context context, List<Crime> ccrimes){
      this.context = context;
      this.mCrimes = ccrimes;
      inflater = (LayoutInflater.from(context));
     }


    @Override
    public int getCount() {
        return mCrimes.size();
        //return crimeImages.length;
    }
    @Override
    public Object getItem(int i) {
        return null;
    }
    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if(view == null) {
            view = inflater.inflate(R.layout.activity_crime_images, null); // inflate the layout
            holder = new ViewHolder();
            holder.image = (ImageView) view.findViewById(R.id.crimeimage); // get the reference of ImageView
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }

       //holder.image.setImageResource(mCrimes.get(i).getmPhoto()); //set image

        return view;


    }

    static class ViewHolder{
        ImageView image;
    }
}
