/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.gms.samples.vision.ocrreader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.samples.vision.ocrreader.DataBase.DBHelper;
import com.google.android.gms.samples.vision.ocrreader.DataBase.OCRDatabase;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;

import java.util.Calendar;

/**
 * Main activity demonstrating how to pass extra parameters to an activity that
 * recognizes text.
 */
public class MainActivity extends Activity implements View.OnClickListener {

    // Use a compound button so either checkbox or switch widgets work.
    private CompoundButton autoFocus;
    private CompoundButton useFlash;
    private TextView statusMessage;
    private TextView textValue;
private  EditText editText;
    Calendar c;
    private Button send,edit,ok;
    private static final int RC_OCR_CAPTURE = 9003;
    private static final String TAG = "MainActivity";
    private static String SOAP_ACTION1 = "http://tempuri.org/GetData";
    private static String NAMESPACE = "http://tempuri.org/";
    private static String METHOD_NAME1 = "GetData";
    private static String METHOD_NAME2 = "GetDataResponse";
    private static String URL = "http://comws.vitalpro.net/webservice2.asmx";
  OCRDatabase ocrDatabase;
DBHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
c= Calendar.getInstance();
        ocrDatabase = new OCRDatabase(this);
        dbHelper= new DBHelper(this);
        statusMessage = (TextView)findViewById(R.id.status_message);
        textValue = (TextView)findViewById(R.id.text_value);
        editText = (EditText) findViewById(R.id.editText);
        send = (Button) findViewById(R.id.button2) ;
        edit = (Button) findViewById(R.id.button);
        ok = (Button) findViewById(R.id.button3);
        autoFocus = (CompoundButton) findViewById(R.id.auto_focus);
        useFlash = (CompoundButton) findViewById(R.id.use_flash);
        findViewById(R.id.read_text).setOnClickListener(this);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.read_text) {
            // launch Ocr capture activity.
            Intent intent = new Intent(this, OcrCaptureActivity.class);
            intent.putExtra(OcrCaptureActivity.AutoFocus, autoFocus.isChecked());
            intent.putExtra(OcrCaptureActivity.UseFlash, useFlash.isChecked());

            startActivityForResult(intent, RC_OCR_CAPTURE);
        }
    }

    /**
     * Called when an activity you launched exits, giving you the requestCode
     * you started it with, the resultCode it returned, and any additional
     * data from it.  The <var>resultCode</var> will be
     * {@link #RESULT_CANCELED} if the activity explicitly returned that,
     * didn't return any result, or crashed during its operation.
     * <p/>
     * <p>You will receive this call immediately before onResume() when your
     * activity is re-starting.
     * <p/>
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode  The integer result code returned by the child activity
     *                    through its setResult().
     * @param data        An Intent, which can return result data to the caller
     *                    (various data can be attached to Intent "extras").
     * @see #startActivityForResult
     * @see #createPendingResult
     * @see #setResult(int)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == RC_OCR_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    final String[] text = {data.getStringExtra(OcrCaptureActivity.TextBlockObject)};
                    statusMessage.setText(R.string.ocr_success);
                    textValue.setText(text[0]);
                    edit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            editText.setVisibility(View.VISIBLE);
                            editText.setText(text[0]);
                            ok.setVisibility(View.VISIBLE);
                        }
                    });
                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String edittext = editText.getText().toString().replace(" ","");
                            textValue.setText(edittext);
                            text[0] = text[0].replace(" ","");
                            for(int i = 0; i<edittext.length();i++) {
                                if (!Character.isDigit(text[0].charAt(i))) {
                                    try {
                                        if (ocrDatabase.getOCRID(text[0].charAt(i) + "", edittext.charAt(i) + "") != -1) {
                                            ocrDatabase.UpdateOCR(ocrDatabase.getOCRID(text[0].charAt(i) + "", edittext.charAt(i) + "")
                                                    , ocrDatabase.getOCR_Repeatance(ocrDatabase.getOCRID(text[0].charAt(i) + "", edittext.charAt(i) + "")) + 1);

                                            Log.v("hihi", "updated");
                                        } else {
                                            Log.v("hihi", "hainsert");
                                            ocrDatabase.insertOCR(text[0].charAt(i) + "", edittext.charAt(i) + "", 1);
                                        }
                                    } catch (Exception e) {
                                        ocrDatabase.insertOCR(text[0].charAt(i) + "", edittext.charAt(i) + "", 1);
                                        Log.v("hihi", "catch lih inserted " + text[0].charAt(i) + " hwa dh s7? " + edittext.charAt(i) + " ha s7 ? ");
                                    }
                                }
                            }
                            editText.setVisibility(View.GONE);
                            ok.setVisibility(View.GONE);
                        }
                    });
                    send.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME1);

                            try{
                                String ocrReadNoSpace = text[0];
                                String ocrIDNoSpace = "1";
                                request.addProperty("SubscriberID", ocrIDNoSpace);
                                request.addProperty("MeterReading", ocrReadNoSpace);
                                request.addProperty("ReadingTime", c.getTime().getDay() + "/" +
                                        (c.getTime().getMonth() + 1) + "/" +(c.getTime().getYear()+1900) + " " + c.getTime().getHours() + ":" + c.getTime().getMinutes());
                                Log.v("hihello", ocrIDNoSpace);

                                //Declare the version of the SOAP request
                                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

                                envelope.setOutputSoapObject(request);
                                envelope.dotNet = true;
                                if (android.os.Build.VERSION.SDK_INT > 9) {
                                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                                    StrictMode.setThreadPolicy(policy);
                                }
                                try {
                                    AndroidHttpTransport androidHttpTransport = new AndroidHttpTransport(URL);

                                    //this is the actual part that will call the webservice
                                    androidHttpTransport.call(SOAP_ACTION1, envelope);

                                    // Get the SoapResult from the envelope body.
                                    SoapPrimitive result = (SoapPrimitive) envelope.getResponse();

                                    if (result.toString().equals("1")) {
                                        Toast.makeText(getApplicationContext(), "تم ارسال القراءة بنجاح", Toast.LENGTH_LONG).show();

                                    } else if(result.toString().equals("0")) {
                                        Toast.makeText(getApplicationContext(), "هذا المستخدم غير موجود بقاعدة البيانات بالرجاء المحاولة مرة أخري ", Toast.LENGTH_LONG).show();

                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Log.v("hihello", e.toString());
                                    Toast.makeText(getApplicationContext()," لا يوجد اتصال بالشبكة، سيتم ارسال الطلب فور توافر اتصال بالانترنت", Toast.LENGTH_LONG).show();
                                   dbHelper.addOrder(ocrReadNoSpace, ocrIDNoSpace, c.getTime().getDay() + "/" +
                                            (c.getTime().getMonth() + 1) + "/" + (c.getTime().getYear() +1900) + " " + c.getTime().getHours() + ":" + c.getTime().getMinutes());

                                }
                            }catch (java.lang.ArrayIndexOutOfBoundsException exception){
                                Toast.makeText(getApplicationContext(), "بالرجاء إعادة التصوير مرة أخري ", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    Log.d(TAG, "Text read: " + text[0]);
                } else {
                    statusMessage.setText(R.string.ocr_failure);
                    Log.d(TAG, "No Text captured, intent data is null");
                }
            } else {
                statusMessage.setText(String.format(getString(R.string.ocr_error),
                        CommonStatusCodes.getStatusCodeString(resultCode)));
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
