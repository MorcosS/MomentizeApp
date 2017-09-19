package systembatarka.com.batarka.app.batarkasystem.Adapters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import systembatarka.com.batarka.app.batarkasystem.Data.m5dumData;
import systembatarka.com.batarka.app.batarkasystem.R;

/**
 * Created by MorcosS on 7/25/16.
 */
public class KashfFaslAdapter extends BaseAdapter {
   public static ArrayList<m5dumData> list;
    LayoutInflater inflater;
    Activity activity;

    public KashfFaslAdapter(ArrayList<m5dumData> list, Activity activity) {
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

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.m5dum_item,null);
        ImageView m5dumPhoto = (ImageView) view.findViewById(R.id.imageView4);
        TextView m5dumName = (TextView) view.findViewById(R.id.textView5);
        if(!list.get(i).getM5dumPhoto().equals("")) {
            byte[] decodedString = Base64.decode(list.get(i).getM5dumPhoto(), Base64.DEFAULT);
            Bitmap base64Bitmap = BitmapFactory.decodeByteArray(decodedString, 0,
                    decodedString.length);
            m5dumPhoto.setImageBitmap(base64Bitmap);
        }
        m5dumName.setText(list.get(i).getM5dumName());
        return view;
    }
}
