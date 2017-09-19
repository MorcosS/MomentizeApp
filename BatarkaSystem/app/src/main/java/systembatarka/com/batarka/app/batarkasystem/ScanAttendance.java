package systembatarka.com.batarka.app.batarkasystem;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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

public class ScanAttendance extends AppCompatActivity implements ZXingScannerView.ResultHandler {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelperScanedIDs = new DBHelperScanedIDs(this);
        c= Calendar.getInstance();
        backPressed=0;
        activity =this;
        final int day = c.get(Calendar.DAY_OF_WEEK);
        setContentView(R.layout.activity_scan_attendance);
        Button button = (Button) findViewById(R.id.qrScan);
        weeksRef = FirebaseDatabase.getInstance().getReference("Classes").child(MainActivity.myClass).child("Attendance");
        weeksRef.keepSynced(true);
         sharedPref =  getApplicationContext().getSharedPreferences("SharedPreference", Activity.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();
        if(day==Calendar.FRIDAY) {
            editor.putString("LastWeekDate", c.getTime().getDate() + "-" + (c.getTime().getMonth()+1) + "-" + (c.getTime().getYear() + 1900));

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
                Toast.makeText(getBaseContext(),"Disconnected",Toast.LENGTH_LONG).show();
                }
            }
        });
       DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    Cursor cursor;
                    cursor = dbHelperScanedIDs.getOrder(2);
                    try {
                        if (cursor.moveToFirst()) {
                            int i=0;
                            if(!sharedPref.getString("LastWeekDate","").equals("")) {

                                do {
                                    MainActivity.myFirebaseRef.child("Classes").child(MainActivity.myClass).child("Attendance").child(sharedPref.getString("LastWeekDate", "")).child(cursor.getInt(0) + "").setValue(true);
                                    dbHelperScanedIDs.deleteOrder(cursor.getInt(0),2);
                                    i++;
                                } while (cursor.moveToNext());
                            }
                        }
                    } catch (Exception e) {

                    }
                    if(day==Calendar.FRIDAY){


                   }else {
                        Toast.makeText(activity,"اليوم ليس الجمعة",Toast.LENGTH_LONG).show();
                    }
                     } else {

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });
        for(int i=0;i<MainActivity.PlaceholderFragment.kashfFaslList.size();i++){
            myList.add(MainActivity.PlaceholderFragment.kashfFaslList.get(i).getM5dumName());
        }
        final CharSequence[] items = myList.toArray(new CharSequence[myList.size()]);
        seletedItems=new ArrayList();
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                ScanAttendance.view = view;
                QrScanner(view);
            }
        });
        c= Calendar.getInstance();
        Button button1 = (Button) findViewById(R.id.button);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view1) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view1.getContext());
                builder.setTitle("اختر المخدومين:");
                builder.setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                        if (isChecked) {
                            // If the user checked the item, add it to the selected items
                            seletedItems.add(indexSelected);
                        }else if (seletedItems.contains(indexSelected)) {
                            // Else, if the item is already in the array, remove it
                            // Else, if the item is already in the array, remove it
                            seletedItems.remove(Integer.valueOf(indexSelected));

                        }
                    }
                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //  Your code when user clicked on OK
                        //  You can write the code  to save the selected item here
                       if(!sharedPref.getString("LastWeekDate","").equals("")){

                            for(int i=0;i<seletedItems.size();i++){
                            MainActivity.myFirebaseRef.child("Classes").child(MainActivity.myClass).child("Attendance").child(sharedPref.getString("LastWeekDate","")).child(MainActivity.PlaceholderFragment.kashfFaslList.get(Integer.parseInt(seletedItems.get(i).toString())).getM5dumID()).setValue(true);
                                dbHelperScanedIDs.addOrder(Integer.parseInt(MainActivity.PlaceholderFragment.kashfFaslList.get(Integer.parseInt(seletedItems.get(i).toString())).getM5dumID()),2);
                        }
                        seletedItems.clear();
                       } if(day==Calendar.FRIDAY) {

                           // Toast.makeText(view1.getContext(), "تم إضافة المخدومين بنجاح", Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(view1.getContext(),"اليوم ليس الجمعة", Toast.LENGTH_LONG).show();

                        }
                    }
                }).create();
                builder.show();
            }
        });
    }

    @Override
    public void handleResult(final Result rawResult) {
        // Do something with the result here

        Log.e("handler", rawResult.getText()); // Prints scan results
        Log.e("handler", rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode)

        // show the scanner result into dialog box.
       /* AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Scan Result");
        builder.setMessage(rawResult.getText());
        AlertDialog alert1 = builder.create();
        alert1.show();*/
            final String myOwnClass = checkThisID(rawResult.getText());

        if(!myOwnClass.equals("Not Found")) {

            if(!sharedPref.getString("LastWeekDate","").equals("")) {
                Log.v("Nota",myOwnClass +" "+ rawResult.getText());
                MainActivity.myFirebaseRef.child("Classes").child(myOwnClass).child("Attendance").child(sharedPref.getString("LastWeekDate","")).child(rawResult.getText().toString()).setValue(true);
               dbHelperScanedIDs.addOrder(Integer.parseInt(rawResult.getText().toString()),2);


                Toast.makeText(view.getContext(), "تم إضافة المخدوم بنجاح", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(getBaseContext(),"اليوم ليس الجمعة!",Toast.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(this, "لم يتم التعرف علي هذا الكود", Toast.LENGTH_LONG).show();

        }
    }

        // If you would like to resume scanning, call this method below:
        // mScannerView.resumeCameraPreview(this);

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
