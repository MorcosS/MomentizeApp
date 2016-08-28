package com.leadtools.app.leadtools;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import leadtools.ILeadStream;
import leadtools.LTLibrary;
import leadtools.LeadStreamFactory;
import leadtools.Platform;
import leadtools.RasterImage;
import leadtools.RasterSupport;
import leadtools.codecs.RasterCodecs;
import leadtools.controls.ImageViewer;
import leadtools.controls.ImageViewerPanZoomInteractiveMode;


public class MainActivity extends AppCompatActivity {

    private final String TAG = "LeadTools";
    private static final int IMAGE_GALLERY = 0x0001;
    private ImageViewer mViewer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Get shared libraries path for APK
        String sharedLibsPath = "";
        if (Build.VERSION.SDK_INT < 9)
            sharedLibsPath = String.format("%s/lib/", this.getApplicationInfo().dataDir);
        else
            sharedLibsPath = this.getApplicationInfo().nativeLibraryDir;

        // Load LEADTOOLS native libraries
        try {
            Platform.setLibPath(sharedLibsPath);
            Platform.loadLibrary(LTLibrary.LEADTOOLS);
        }
        catch (Exception ex) {
            Log.d(TAG, "Failed to load LEADTOOLS native libraries");
        }

        // Initialize and set license
        try{
            RasterSupport.initialize(this);
            RasterSupport.setLicense(getResources().openRawResource(R.raw.leadtools), "your_dev_key_goes_here");
        }
        catch(Exception ex){
            Log.d(TAG, "Failed to set LEADTOOLS license");
            finish();
        }

        // Ensure that the LEADTOOLS kernel is not expired
        if(RasterSupport.getKernelExpired()){
            Log.d(TAG, "LEADTOOLS kernel is expired");
            finish();
        }
        RasterSupport.initialize(this);

        // Init viewer
        mViewer = (ImageViewer) findViewById(R.id.imageviewer);
        mViewer.setTouchInteractiveMode(new ImageViewerPanZoomInteractiveMode());

    }
    public void onSelectImage(View v)
    {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(gallery, IMAGE_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == RESULT_OK)
        {
            if(requestCode == IMAGE_GALLERY)
            {
                Uri imageUri = data.getData();
                try
                {
                    // Create Lead Stream
                    ILeadStream stream = LeadStreamFactory.create(getContentResolver().openInputStream(imageUri), true);

                    // Create RasterCodecs
                    RasterCodecs codecs = new RasterCodecs();

                    // Load image
                    RasterImage image = codecs.load(stream);
                    
                    // Set the loaded image in the viewer

                }
                catch(Exception ex)
                {
                    Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

}
