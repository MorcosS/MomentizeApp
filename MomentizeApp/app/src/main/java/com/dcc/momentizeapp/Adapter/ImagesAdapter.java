package com.dcc.momentizeapp.Adapter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.dcc.momentizeapp.Data.Memory;
import com.dcc.momentizeapp.LoginActivity;
import com.dcc.momentizeapp.MainApp.MainApp;
import com.dcc.momentizeapp.R;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import static android.R.id.list;
import static android.app.Activity.RESULT_OK;

/**
 * Created by MorcosS on 5/13/17.
 */

public class ImagesAdapter extends BaseAdapter  {
    ArrayList<Bitmap> imagesList;
    Activity activity;
    LayoutInflater inflater;
    int CAMERA_PIC_REQUEST = 2;






    @Override
    public int getCount() {
        return imagesList.size();
    }

    @Override
    public Object getItem(int i) {
        return imagesList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.images_view, null);
        ImageView image = (ImageView) view.findViewById(R.id.imageButton);
        if (position == imagesList.size() - 1) {
            image.setImageResource(R.drawable.ic_note_add_black_24px);
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(LoginActivity.TAG, "clicked");
                    final CharSequence[] items = {
                            "Camera", "Gallery"
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setTitle("Choose Photo From ..");
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {

                            if (items[item].equals("Camera")) {
                                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                                activity.startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
                            } else if (items[item].equals("Gallery")) {
                                Intent i = new Intent(
                                        Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                                activity.startActivityForResult(i, 1);
                            }
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            });
        }else{
            image.setImageBitmap(imagesList.get(position));
        }

        return view;
    }




    public ImagesAdapter(ArrayList<Bitmap> imagesList, Activity activity) {
        this.imagesList = imagesList;
        this.activity = activity;
        inflater = activity.getLayoutInflater();

    }

    public void setImagesList(ArrayList<Bitmap> imagesList){
        this.imagesList = imagesList;

    }








}
