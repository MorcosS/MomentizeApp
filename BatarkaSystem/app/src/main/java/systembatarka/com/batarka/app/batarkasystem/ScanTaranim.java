package systembatarka.com.batarka.app.batarkasystem;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;

import java.util.ArrayList;
import java.util.Calendar;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import systembatarka.com.batarka.app.batarkasystem.Data.DBHelperScanedIDs;

public class ScanTaranim extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    long m5dumIteration;
    Calendar c;
    static View view;
    boolean found;
    DatabaseReference presenceRef,weeksRef;
    ArrayList seletedItems;
    DataSnapshot myChild;
    SharedPreferences sharedPref;
    DBHelperScanedIDs dbHelperScanedIDs;
    Activity activity;
    int backPressed;
    int myPoints;
    boolean isTagawob;
    int day;
    String myOwnClass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_taranim);
        Button button = (Button) findViewById(R.id.qrScan4);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                ScanAttendance.view = view;
                QrScanner(view);
            }
        });
        c = Calendar.getInstance();
        dbHelperScanedIDs = new DBHelperScanedIDs(this);
        day = c.get(Calendar.DAY_OF_WEEK);
        backPressed = 0;
        activity = this;
        weeksRef = FirebaseDatabase.getInstance().getReference("Classes").child(MainActivity.myClass).child("Attendance");
        weeksRef.keepSynced(true);
        sharedPref = getApplicationContext().getSharedPreferences("SharedPreference", Activity.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();
        if (day == Calendar.FRIDAY) {
            editor.putString("LastWeekDate", c.getTime().getDate() + "-" + (c.getTime().getMonth() + 1) + "-" + (c.getTime().getYear() + 1900));

            editor.commit();
        }
        ArrayList<String> myList = new ArrayList<String>();

        DatabaseReference presenceRef = FirebaseDatabase.getInstance().getReference("disconnectmessage");
// Write a string when this client loses connection

        presenceRef.onDisconnect().setValue("I disconnected!");
        presenceRef.onDisconnect().removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError error, DatabaseReference firebase) {
                if (error != null) {
                    Toast.makeText(getBaseContext(), "Disconnected", Toast.LENGTH_LONG).show();
                }
            }
        });
        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    final Cursor cursor;
                    cursor = dbHelperScanedIDs.getOrder(1);
                    try {
                        if (cursor.moveToFirst()) {
                            int i = 0;
                            if (!sharedPref.getString("LastWeekDate", "").equals("")) {

                                do {
                                    myOwnClass = checkThisID(cursor.getInt(0)+"");
                                    //MainActivity.myFirebaseRef.child("Classes").child(MainActivity.myClass).child("Attendance").child(sharedPref.getString("LastWeekDate", "")).child(cursor.getInt(0) + "").setValue(true);
                                    // dbHelperScanedIDs.deleteOrder(cursor.getInt(0));
                                    Query query = MainActivity.myFirebaseRef.child("Classes").child(myOwnClass).child("m5dumin").child(cursor.getInt(0)+"").child("Points");
                                    query.addValueEventListener(new com.firebase.client.ValueEventListener() {
                                        @Override
                                        public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                                            myPoints = Integer.parseInt(dataSnapshot.child("PointsTotal").getValue().toString());
                                            try {
                                                isTagawob = Boolean.parseBoolean(dataSnapshot.child(sharedPref.getString("LastWeekDate", "")).child("Tanawol").getValue().toString());
                                                if (isTagawob) {
                                                    dbHelperScanedIDs.deleteOrder(cursor.getInt(0), 1);
                                                }
                                            }catch (Exception e){
                                                MainActivity.myFirebaseRef.child("Classes").child(myOwnClass).child("m5dumin").child(cursor.getInt(0)+"").child("Points").child(sharedPref.getString("LastWeekDate", "")).child("Tanawol").setValue(true);
                                                MainActivity.myFirebaseRef.child("Classes").child(myOwnClass).child("m5dumin").child(cursor.getInt(0)+"").child("Points").child("PointsTotal").setValue(myPoints + 10);
                                                dbHelperScanedIDs.deleteOrder(cursor.getInt(0),
                                                        1);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(FirebaseError firebaseError) {

                                        }
                                    });
                                    i++;
                                } while (cursor.moveToNext());
                            }
                        }
                    } catch (Exception e) {

                    }

                } else {

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void handleResult(final Result rawResult) {
        // Do something with the result here

        Log.e("handler", rawResult.getText()); // Prints scan results
        Log.e("handler", rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode)
        myOwnClass = checkThisID(rawResult.getText());

        if(!myOwnClass.equals("Not Found")) {
            if (day == Calendar.FRIDAY) {

                Query query = MainActivity.myFirebaseRef.child("Classes").child(myOwnClass).child("m5dumin").child(rawResult.getText()).child("Points");
                query.addValueEventListener(new com.firebase.client.ValueEventListener() {
                    @Override
                    public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                        myPoints = Integer.parseInt(dataSnapshot.child("PointsTotal").getValue().toString());
                        try {
                            isTagawob = Boolean.parseBoolean(dataSnapshot.child(sharedPref.getString("LastWeekDate", "")).child("Tanawol").getValue().toString());
                            if (!isTagawob) {
                            }
                        }catch (Exception e){
                            MainActivity.myFirebaseRef.child("Classes").child(myOwnClass).child("m5dumin").child(rawResult.getText()).child("Points").child(sharedPref.getString("LastWeekDate", "")).child("Tanawol").setValue(true);
                            MainActivity.myFirebaseRef.child("Classes").child(myOwnClass).child("m5dumin").child(rawResult.getText()).child("Points").child("PointsTotal").setValue(myPoints + 10);
                            dbHelperScanedIDs.addOrder(Integer.parseInt(rawResult.getText().toString()),1);


                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });


            } else {
                Toast.makeText(activity, "اليوم ليس الجمعة", Toast.LENGTH_LONG).show();
            }


        }

    }

    public void QrScanner(View view){

        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();         // Start camera
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public String checkThisID(String result){
        if(result.length()==3) {
            switch (result.charAt(0)) {
                case '1':
                    return "Pope Kirolos";
                case '2':
                    return "Pope Asansius";
                case '3':
                    return "Pope Alexandros";
                case '4':
                    return "Pope Dimitrius";
                case '5':
                    return "Pope Abraam";
                case '6':
                    return "Pope Shinuda";
            }
        }
        return "Not Found";
    }
    @Override
    public void onBackPressed() {
        if(backPressed==0) {
            QrScanner(view);
            backPressed++;
        }else{
            super.onBackPressed();
        }
    }
}
