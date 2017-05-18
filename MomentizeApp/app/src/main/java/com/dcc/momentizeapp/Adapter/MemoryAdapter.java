package com.dcc.momentizeapp.Adapter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dcc.momentizeapp.Data.Memory;
import com.dcc.momentizeapp.R;

import java.util.ArrayList;

/**
 * Created by MorcosS on 5/18/17.
 */

public class MemoryAdapter extends BaseAdapter {
    ArrayList<Memory> list;
    LayoutInflater inflater;
    Activity activity;


    public MemoryAdapter(ArrayList<Memory> list, Activity activity) {
        inflater = activity.getLayoutInflater();
        this.list = list;
        this.activity = activity;

    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.memory_card, null);
        ImageView img = (ImageView) view.findViewById(R.id.memory_image);
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView yourDescription = (TextView) view.findViewById(R.id.your_description);
        TextView mateDescription = (TextView) view.findViewById(R.id.mate_description);
        TextView date = (TextView) view.findViewById(R.id.date);
        title.setText(list.get(i).getMemoryTitle());
        date.setText(list.get(i).getMemoryDate());
        yourDescription.setText(list.get(i).getComments().getComments_1());
        mateDescription.setText(list.get(i).getComments().getComments_2());

        return view;
    }


}
