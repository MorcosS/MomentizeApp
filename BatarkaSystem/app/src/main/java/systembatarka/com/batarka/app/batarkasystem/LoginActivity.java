package systembatarka.com.batarka.app.batarkasystem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * Id to identity READ_CONTACTS permission request.
     */


    // UI references.
    private EditText mAddressView, mMobileView, mFlatView, mFloorView, mNameView;
    private Spinner mClassView;
    private View mProgressView;
    private View mLoginFormView;
    private String myClassName;
    Resources res;
    long size, id;
    public static int counter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
Firebase.setAndroidContext(this);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        size = 0;
        res = getResources();
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("SharedPreference", Activity.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();
        boolean isRegistered = sharedPref.getBoolean("IsRegistered", false);
        if (isRegistered) {
            Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
            LoginActivity.this.startActivity(mainIntent);
            LoginActivity.this.finish();
        } else {
            counter = 0;
            final Firebase myFirebase = new Firebase("https://batarkasystem.firebaseio.com");
            DatabaseReference scoresRef = FirebaseDatabase.getInstance().getReference("5udamID");
            scoresRef.keepSynced(true);
            Query queryRef = myFirebase.child("5udamID");
            queryRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    size = Long.parseLong(snapshot.getValue().toString());

                }

                @Override
                public void onCancelled(FirebaseError error) {
                    Toast.makeText(LoginActivity.this, "بالرجاء التأكد من التوصيل بالانترنت", Toast.LENGTH_LONG).show();
                }
            });
            mNameView = (EditText) findViewById(R.id.name);
            mAddressView = (EditText) findViewById(R.id.address);
            mFlatView = (EditText) findViewById(R.id.flatNo);
            mFloorView = (EditText) findViewById(R.id.floorNo);
            mMobileView = (EditText) findViewById(R.id.mobile);
            mClassView = (Spinner) findViewById(R.id.spinner);
            Button mSignInButton = (Button) findViewById(R.id.email_sign_in_button);
            mClassView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView,
                                           View selectedItemView, int position, long id) {
                    try {
                        switch (position) {
                            case 0:
                                myClassName = "";
                                break;
                            case 1:
                                myClassName = "Pope Kirolos";
                                break;
                            case 2:
                                myClassName = "Pope Asansius";
                                break;
                            case 3:
                                myClassName = "Pope Alexandros";
                                break;
                            case 4:
                                myClassName = "Pope Dimitrius";
                                break;
                            case 5:
                                myClassName = "Pope Abraam";
                                break;
                            case 6:
                                myClassName = "Pope Shinuda";
                                break;
                        }
                    } catch (Exception e) {

                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {

                }

            });
            mSignInButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isConnected(LoginActivity.this)) {
                        if (checkFormSubmitCorrect()) {
                            id = size + 1;
                            editor.putString("Name", mNameView.getText().toString());
                            editor.putString("Address", mAddressView.getText().toString());
                            editor.putString("Mobile", mMobileView.getText().toString());
                            editor.putString("Floor", mFloorView.getText().toString());
                            editor.putString("Flat", mFlatView.getText().toString());
                            editor.putString("Class", myClassName);
                            editor.putBoolean("IsRegistered", true);
                            editor.putString("ID", id + "");
                            editor.commit();
                            myFirebase.child("Classes").child(myClassName).child("5udam").child(id + "").child("Name").setValue(mNameView.getText().toString());
                            myFirebase.child("Classes").child(myClassName).child("5udam").child(id + "").child("Address").setValue(mAddressView.getText().toString());
                            myFirebase.child("Classes").child(myClassName).child("5udam").child(id + "").child("FloorNo").setValue(mFloorView.getText().toString());
                            myFirebase.child("Classes").child(myClassName).child("5udam").child(id + "").child("FlatNo").setValue(mFlatView.getText().toString());
                            myFirebase.child("Classes").child(myClassName).child("5udam").child(id + "").child("Mobile").setValue(mMobileView.getText().toString());
                            myFirebase.child("5udamID").setValue(id+"");
                            Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                            LoginActivity.this.startActivity(mainIntent);
                            LoginActivity.this.finish();
                        }
                    }
                }
            });

        }
    }

    public boolean checkFormSubmitCorrect() {
        if (mNameView.getText().toString().isEmpty()) {
            Toast.makeText(LoginActivity.this, "بالرجاء كتابة الاسم", Toast.LENGTH_LONG).show();
            return false;
        } else if (mAddressView.getText().toString().isEmpty()) {
            Toast.makeText(LoginActivity.this, "بالرجاء كتابة العنوان", Toast.LENGTH_LONG).show();
            return false;
        } else if (mFloorView.getText().toString().isEmpty() || !TextUtils.isDigitsOnly(mFloorView.getText().toString())) {
            Toast.makeText(LoginActivity.this, "بالرجاء كتابة رقم الدور صحيحاٍ", Toast.LENGTH_LONG).show();
            return false;
        } else if (mFlatView.getText().toString().isEmpty() || !TextUtils.isDigitsOnly(mFlatView.getText().toString())) {
            Toast.makeText(LoginActivity.this, "بالرجاء كتابة رقم الشقة صحيحا", Toast.LENGTH_LONG).show();
            return false;
        } else if (mMobileView.getText().toString().isEmpty()) {
            Toast.makeText(LoginActivity.this, "بالرجاء كتابة رقم المحمول", Toast.LENGTH_LONG).show();
            return false;
        } else if (!TextUtils.isDigitsOnly(mMobileView.getText().toString()) || (mMobileView.getText().toString().length() < 11)) {
            Toast.makeText(LoginActivity.this, "بالرجاء كتابة رقم المحمول صحيحا", Toast.LENGTH_LONG).show();
            return false;
        } else if (myClassName.equals("")) {
            Toast.makeText(LoginActivity.this, "بالرجاء اختيار اسم الفصل", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }


    /**
     * Callback received when a permissions request has been completed.
     */
    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            try {
                URL url = new URL("http://www.google.com/");
                final HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                urlc.setRequestProperty("User-Agent", "test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1000); // mTimeout is in seconds
                //urlc.connect();
                // return (urlc.getResponseCode() == 200);
            } catch (IOException e) {
                Log.i("warning", "Error checking internet connection", e);
                return false;
            }
            return true;
        }
        return false;
    }




}

