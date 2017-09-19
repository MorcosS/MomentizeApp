package pharmacy.morcos.andrew.drpharmacy;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.io.ByteArrayOutputStream;

public class NEWS extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 1888;
    private static int RESULT_LOAD_IMAGE = 1;
    int Max_ID;
    Button set_image, send, cancel;
    ImageView imageView;
    String pic_1, pic_2;
    EditText editText;
    RelativeLayout relativeLayout;
    ProgressBar progressBar;
    Firebase myFirebaseRef;

    Long NewsNo;
    String imgString="no image";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        myFirebaseRef = new Firebase("https://romance-pharmacy.firebaseio.com/");

        Query queryRef =myFirebaseRef.child("NewsNo");
        queryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                NewsNo = Long.parseLong(snapshot.getValue().toString());
            }

            @Override
            public void onCancelled(FirebaseError error) {
            }
        });

        Intent intent = getIntent();
        Max_ID = intent.getIntExtra("MaxID", 0);

        set_image = (Button) findViewById(R.id.button_set_image);
        send = (Button) findViewById(R.id.button_send_news);
        cancel = (Button) findViewById(R.id.button_cancel);
        imageView = (ImageView) findViewById(R.id.imageView_NEWS);
        editText = (EditText) findViewById(R.id.editText_NEWS);
        relativeLayout = (RelativeLayout) findViewById(R.id.layout);
        progressBar = (ProgressBar) findViewById(R.id.progressBar_send_news);
        progressBar.setVisibility(View.INVISIBLE);



        set_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] items = {
                        "Camera", "Gallery"
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(NEWS.this);
                builder.setTitle("Choose Photo From ..");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {

                        if (items[item].equals("Camera")) {
                            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(cameraIntent, CAMERA_REQUEST);
                        } else if (items[item].equals("Gallery")) {
                            Intent i = new Intent(
                                    Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                            startActivityForResult(i, RESULT_LOAD_IMAGE);
                        }
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relativeLayout.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        postData();
                    }
                });
                t.start();
            }
        });


    }

    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        return stream.toByteArray();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();


            Bitmap bitmap = BitmapFactory.decodeFile(picturePath);

            String imgString = Base64.encodeToString(getBytesFromBitmap(bitmap),
                    Base64.NO_WRAP);

            imageView.setImageBitmap(bitmap);

            pic_1 = imgString.substring(0, imgString.length() / 2);
            pic_2 = imgString.substring((imgString.length() / 2) + 1);

        } else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK && data != null) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");

           imgString = Base64.encodeToString(getBytesFromBitmap(photo),
                    Base64.NO_WRAP);

            imageView.setImageBitmap(photo);

        }


    }

    public void postData() {

        myFirebaseRef.child("NewsNo").setValue(NewsNo+1);
        myFirebaseRef.child("News").child(NewsNo+1+"").child("Picture").setValue(imgString);
        myFirebaseRef.child("News").child(NewsNo+1+"").child("Text").setValue(editText.getText().toString());

        Intent intent = new Intent(NEWS.this, Set_News.class);
        startActivity(intent);
    }

}
