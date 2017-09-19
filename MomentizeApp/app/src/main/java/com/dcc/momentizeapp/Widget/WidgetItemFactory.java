package com.dcc.momentizeapp.Widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Binder;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;


import com.dcc.momentizeapp.Data.Memory;
import com.dcc.momentizeapp.MainApp.MainApp;
import com.dcc.momentizeapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.ArrayList;

import static com.dcc.momentizeapp.Widget.MomentizeAppWidget.ACTION_DATA_UPDATED;

/**
 * Created by MorcosS on 5/18/17.
 */

public class WidgetItemFactory implements RemoteViewsService.RemoteViewsFactory {
    private ArrayList <Memory>memoryArrayList = new ArrayList();
    private Context context = null;
    private int appWidgetId;
    public FirebaseAuth mAuth;
    public static String myUID;
    public static DatabaseReference mDatabase;
    SharedPreferences sharedPref;
    String blobId;

    public WidgetItemFactory(Context context, Intent intent) {
        this.context = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);


    }

    private void populateListItem() {
        mDatabase.child("Blobs").child(blobId).child("Memories").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        memoryArrayList.clear();
                        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                            Memory memory = postSnapshot.getValue(Memory.class);
                            memoryArrayList.add(memory);
                        }
                        Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED);
                        context.sendBroadcast(dataUpdatedIntent);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void onCreate() {
        sharedPref = context.getApplicationContext().getSharedPreferences("SharedPreference",Context.MODE_PRIVATE);
        blobId = sharedPref.getString(context.getString(R.string.blobId),"");
        mAuth = FirebaseAuth.getInstance();
        myUID = mAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        if(!blobId.isEmpty()){
            populateListItem();
        }
    }

    @Override
    public void onDataSetChanged() {
        initCursor();
    }

    @Override
    public void onDestroy() {

    }


    private void initCursor(){

    }


    @Override
    public int getCount() {
        return memoryArrayList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }


    @Override
    public RemoteViews getViewAt(int i) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_item);
        remoteViews.setTextViewText(R.id.title,memoryArrayList.get(i).getMemoryTitle());

         remoteViews.setTextViewText(R.id.description,memoryArrayList.get(i).getComments().getComments_1()+"/n" +
                 memoryArrayList.get(i).getComments().getComments_2());

        return remoteViews;
    }


    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

}