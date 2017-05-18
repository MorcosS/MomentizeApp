package com.dcc.momentizeapp.QR;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import com.dcc.momentizeapp.Data.Blob;
import com.dcc.momentizeapp.LoginActivity;
import com.dcc.momentizeapp.MainApp.MainApp;
import com.dcc.momentizeapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.ydcool.lib.qrmodule.activity.QrScannerActivity;
import me.ydcool.lib.qrmodule.encoding.QrGenerator;


public class QrActivity extends AppCompatActivity {
    @BindView(R.id.imageView)
    ImageView mImageView;
    private FirebaseAuth mAuth;
    private String myUID;
    private FirebaseDatabase database;
    private DatabaseReference mDatabase;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        myUID = mAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        Bitmap qrCode = null;

        try {
            qrCode = new QrGenerator.Builder()
                    .content(myUID)
                    .qrSize(300)
                    .margin(2)
                    .color(Color.BLACK)
                    .bgColor(Color.WHITE)
                    .ecc(ErrorCorrectionLevel.H)
                    .overlay(this, R.mipmap.ic_launcher)
                    .overlaySize(100)
                    .overlayAlpha(255)
                    .overlayXfermode(PorterDuff.Mode.SRC_ATOP)
                    .encode();
        } catch (WriterException e) {
            e.printStackTrace();
        }
        if(qrCode != null){
            mImageView.setImageBitmap(qrCode);
        }
    }

    public void onQrPressed (View view){
        Intent intent = new Intent(this, QrScannerActivity.class);
        startActivityForResult(intent, QrScannerActivity.QR_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == QrScannerActivity.QR_REQUEST_CODE) {
            if (resultCode == RESULT_OK){
                Blob blob = new Blob();
                blob.setLinkedUsers(myUID,data.getExtras().getString(QrScannerActivity.QR_RESULT_STR));
                String key =  mDatabase.child("Blobs").push().getKey();
                Map<String, Object> blobValues = blob.toMap();
                mDatabase.child("Blobs").child(key).setValue(blobValues);
                editor.putString(getString(R.string.blobId),key);
                mDatabase.child("Users").child(blob.getLinkedUsers().getBaseUID()).child(MainApp.BlobId).setValue(key);
                mDatabase.child("Users").child(blob.getLinkedUsers().getLinkedUID()).child(MainApp.BlobId).setValue(key);
            }
            Log.d(LoginActivity.TAG, resultCode == RESULT_OK
                    ? data.getExtras().getString(QrScannerActivity.QR_RESULT_STR)
                    : "Scanned Nothing!");
        }
    }

}
