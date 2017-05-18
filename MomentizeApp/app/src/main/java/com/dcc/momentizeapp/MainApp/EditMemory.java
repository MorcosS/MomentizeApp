package com.dcc.momentizeapp.MainApp;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;

import com.dcc.momentizeapp.Adapter.ImagesAdapter;
import com.dcc.momentizeapp.Data.Comments;
import com.dcc.momentizeapp.Data.Memory;
import com.dcc.momentizeapp.LoginActivity;
import com.dcc.momentizeapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sdsmdg.tastytoast.TastyToast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditMemory extends AppCompatActivity {
    @BindView(R.id.gridView) GridView gridView;
    @BindView(R.id.your_description)EditText yourDescription;
    @BindView(R.id.memory_title)EditText memoryTitle;
    @BindView(R.id.datePicker)DatePicker datePicker;
    @BindView(R.id.mate_description)EditText mateDescription;
    @BindView(R.id.login_progress) View mProgressView;

    int CAMERA_PIC_REQUEST = 2;
    ArrayList<Bitmap> imagesList;
    ArrayList<Uri> imagesUri;
    ImagesAdapter imagesAdapter;
    public static Location location;
    boolean GPSEnabled, NetworkEnabled;
    public static LocationManager Coordinates;
    private  double lat,lon;
    StorageReference storageRef;
    FirebaseStorage storage;
    private FirebaseAuth mAuth;
    private String myUID;
    private FirebaseDatabase database;
    private DatabaseReference mDatabase;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    String blobID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_memory);
        ButterKnife.bind(this);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        mAuth = FirebaseAuth.getInstance();
        myUID = mAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        sharedPref = getApplicationContext().getSharedPreferences("SharedPreference",Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        blobID = sharedPref.getString(getString(R.string.blobId),"");
        imagesUri = new ArrayList<Uri>();
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false);
        imagesList = new ArrayList<Bitmap>();
        imagesAdapter = new ImagesAdapter(imagesList,this);
        gridView.setAdapter(imagesAdapter);
        imagesList.add(Bitmap.createBitmap(100,100, Bitmap.Config.ALPHA_8));
        imagesAdapter.notifyDataSetChanged();

    }

    public void onGpsPressed (View view){
        Coordinates = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET},
                    PackageManager.PERMISSION_GRANTED);
        }

        GPSEnabled = true;
        NetworkEnabled = true;

        try {
            GPSEnabled = Coordinates.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            NetworkEnabled = Coordinates.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!GPSEnabled) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("GPS is Required but Disabled : Enable GPS , Restart App.");

            alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    finish();
                }
            });

            alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();


        }

        if (!NetworkEnabled) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Network is Required but Disabled : Enable Network , Restart App.");

            alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    finish();
                }
            });

            alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

        if (GPSEnabled && NetworkEnabled) {
            Coordinates.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });
            try {
                location = Coordinates.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                lat = location.getLatitude();
                lon = location.getLongitude();
            } catch (Exception e) {

            }
        }
    }

    public boolean validateData(){
        if(memoryTitle.getText().toString().isEmpty()||
                mateDescription.getText().toString().isEmpty()|| yourDescription.getText().toString().isEmpty())
                return false;
        return true;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);


            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);

        }
    }

    public void onSavePressed (View view){
        if(validateData()) {
            showProgress(true);
            final Memory memory = new Memory();
            memory.setComments(new Comments(yourDescription.getText().toString(), mateDescription.getText().toString()));
            memory.setMemoryTitle(memoryTitle.getText().toString());
            int day = datePicker.getDayOfMonth();
            int month = datePicker.getMonth();
            int year = datePicker.getYear();

            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);
            memory.setMemoryDate(calendar.getTime().toString());

            memory.setLocation(lat, lon);
            final String key = mDatabase.child("Blobs").child(blobID).child("Memories").push().getKey();
            if (!blobID.isEmpty()) {
                mDatabase.child("Blobs").child(blobID).child("Memories").child(key).setValue(memory);
                TastyToast.makeText(this,getString(R.string.success_add_memory),TastyToast.LENGTH_LONG,TastyToast.SUCCESS).show();
                showProgress(false);
                finish();
            }else{
                showProgress(false);
                TastyToast.makeText(this,getString(R.string.error_no_blob),TastyToast.LENGTH_LONG,TastyToast.CONFUSING).show();
            }
            if (imagesList.size() > 1) {
                int i =0;
                for (Bitmap bitmap : imagesList) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();
                    UploadTask uploadTask = storageRef.child(blobID).child(key+""+i+".jpg").putBytes(data);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                            @SuppressWarnings("VisibleForTests") Uri uri = taskSnapshot.getDownloadUrl();

                            String key1 = mDatabase.child("Blobs").child(blobID).child("Memories").child(key).child("imagesURi").push().getKey();
                            mDatabase.child("Blobs").child(blobID).child("Memories").child(key).child("imagesURi").child(key1).setValue(uri.toString());
                        }
                    });
                    i++;
                }
                memory.setImagesUri(imagesUri);
            }

        }else{
            TastyToast.makeText(this,getString(R.string.error_validate_data),TastyToast.LENGTH_LONG,TastyToast.WARNING).show();
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(LoginActivity.TAG,"result");
        if (requestCode == CAMERA_PIC_REQUEST && resultCode == RESULT_OK && data != null) {
            Bitmap image = (Bitmap) data.getExtras().get("data");
            imagesList.add(image);
            imagesAdapter.notifyDataSetChanged();
        }else if(requestCode == 1 && resultCode == RESULT_OK && data != null){
            Uri selectedImage = data.getData();
            Log.d(LoginActivity.TAG,"data result");
            InputStream imageStream = null;


            try {
                imageStream = getContentResolver().openInputStream(selectedImage);
                Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                imagesList.add(bitmap);
                imagesAdapter.notifyDataSetChanged();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


        }else{
            finish();
        }
    }
}

