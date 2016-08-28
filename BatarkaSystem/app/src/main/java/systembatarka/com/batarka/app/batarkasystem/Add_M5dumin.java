package systembatarka.com.batarka.app.batarkasystem;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * A login screen that offers login via email/password.
 */
public class Add_M5dumin extends AppCompatActivity {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;


    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */

    // UI references.
    private EditText m5dumName,m5dumAddress,m5dumPhone,m5dumMobile,m5dumFatherMob,m5dumMotherMob,m5dumFlatNo,m5dumFloorNo;
    private View mProgressView;
    private View mLoginFormView;
    private ImageButton m5dumPhoto;
    private String myClass;
    private long m5dumIDs,m5dumID;
    private DatePicker m5dumMilad;
    StorageReference storageRef;
    public static Firebase myFirebaseRef;
    ImageView ivOriginal;
    String imageInBase64;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_add__m5dumin);
         myFirebaseRef = new Firebase("https://batarkasystem.firebaseio.com/");
        // Set up the login form.
        imageInBase64 = "No Image";
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://batarkasystem.appspot.com");
        SharedPreferences sharedPref =  getApplicationContext().getSharedPreferences("SharedPreference", Activity.MODE_PRIVATE);
        myClass=sharedPref.getString("Class","");
        Query queryRef = myFirebaseRef.child("Classes").child(myClass).child("m5dumIDs");
        queryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                m5dumIDs = Long.parseLong(snapshot.getValue().toString());
                m5dumID = m5dumIDs +1;
            }
            @Override public void onCancelled(FirebaseError error) {

            }
        });
        m5dumName= (EditText) findViewById(R.id.m5dum_name);
        m5dumAddress = (EditText) findViewById(R.id.m5dum_address);
        m5dumFlatNo = (EditText) findViewById(R.id.m5dum_flatNo);
        m5dumFloorNo = (EditText) findViewById(R.id.m5dum_floorNo);
        m5dumMobile = (EditText) findViewById(R.id.m5dum_mobile);
        m5dumPhone = (EditText) findViewById(R.id.m5dum_phone);
        m5dumFatherMob = (EditText) findViewById(R.id.m5dum_mobileFather);
        m5dumMotherMob = (EditText) findViewById(R.id.m5dum_mobileMother);
        m5dumMilad = (DatePicker) findViewById(R.id.datePicker);
        m5dumPhoto = (ImageButton) findViewById(R.id.imageButton);
        m5dumPhoto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                final CharSequence[] items = {
                        "Camera", "Gallery"
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(Add_M5dumin.this);
                builder.setTitle("Choose Photo From ..");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {

                        if (items[item].equals("Camera")) {
                            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(cameraIntent,2);
                        } else if (items[item].equals("Gallery")) {
                            Intent i = new Intent(
                                    Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                            startActivityForResult(i,  1);
                        }
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();

            }
        });
        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                generateQRAndSend();
            }
                       });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    public boolean generateQRAndSend(){
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(m5dumID+"", BarcodeFormat.QR_CODE, 512, 512);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            m5dumPhoto.setImageBitmap(bmp);
            StorageReference imagesRef = storageRef.child("أسرة البطاركة ١٧٣٣"+"/"+myClass+"/"+m5dumName.getText().toString()+".jpg");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = imagesRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    Toast.makeText(Add_M5dumin.this, "لم يتم إضافة المخدوم.", Toast.LENGTH_LONG).show();
                    finish();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    myFirebaseRef.child("Classes").child(myClass).child("m5dumin").child(m5dumID+"").child("Name").setValue(m5dumName.getText().toString());
                    myFirebaseRef.child("Classes").child(myClass).child("m5dumin").child(m5dumID+"").child("Address").setValue(m5dumAddress.getText().toString());
                    myFirebaseRef.child("Classes").child(myClass).child("m5dumin").child(m5dumID+"").child("FloorNo").setValue(m5dumFloorNo.getText().toString());
                    myFirebaseRef.child("Classes").child(myClass).child("m5dumin").child(m5dumID+"").child("FlatNo").setValue(m5dumFlatNo.getText().toString());
                    myFirebaseRef.child("Classes").child(myClass).child("m5dumin").child(m5dumID+"").child("Mobile").setValue(m5dumMobile.getText().toString());
                    myFirebaseRef.child("Classes").child(myClass).child("m5dumin").child(m5dumID+"").child("Phone").setValue(m5dumPhone.getText().toString());
                    myFirebaseRef.child("Classes").child(myClass).child("m5dumin").child(m5dumID+"").child("FatherMob").setValue(m5dumFatherMob.getText().toString());
                    myFirebaseRef.child("Classes").child(myClass).child("m5dumin").child(m5dumID+"").child("MotherMob").setValue(m5dumMotherMob.getText().toString());
                    myFirebaseRef.child("Classes").child(myClass).child("m5dumin").child(m5dumID+"").child("Points").child("PointsTotal").setValue(0);
                    myFirebaseRef.child("Classes").child(myClass).child("m5dumin").child(m5dumID+"").child("DOB").setValue(m5dumMilad.getDayOfMonth()+"/"
                            +(m5dumMilad.getMonth()+1)+"/"+m5dumMilad.getYear());
                    myFirebaseRef.child("Classes").child(myClass).child("m5dumin").child(m5dumID+"").child("Photo").setValue(imageInBase64);
                    myFirebaseRef.child("Classes").child(myClass).child("m5dumIDs").setValue(m5dumID);
                    Toast.makeText(Add_M5dumin.this, "قد تم إضافة المخدوم بنجاح.", Toast.LENGTH_LONG).show();
                    finish();
                }
            });

        } catch (WriterException e) { //eek//
        }
        return true;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            Bitmap image = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, byteArray);
            byte[] byteArr = byteArray.toByteArray();
            imageInBase64 = Base64.encodeToString(byteArr, Base64.DEFAULT);
            m5dumPhoto.setImageBitmap(image);
        }else if(requestCode == 1 && resultCode == RESULT_OK && data != null){
            Uri selectedImage = data.getData();
            InputStream imageStream = null;
            try {
                imageStream = getContentResolver().openInputStream(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Bitmap image1 = BitmapFactory.decodeStream(imageStream);
            ByteArrayOutputStream byteArray1 = new ByteArrayOutputStream();
            image1.compress(Bitmap.CompressFormat.JPEG, 100, byteArray1);
            byte[] byteArr1 = byteArray1.toByteArray();
            imageInBase64 = Base64.encodeToString(byteArr1, Base64.DEFAULT);
            m5dumPhoto.setImageBitmap(image1);

        }else{
            finish();
        }
    }

}

