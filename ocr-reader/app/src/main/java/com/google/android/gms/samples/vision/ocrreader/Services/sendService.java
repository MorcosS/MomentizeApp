package com.google.android.gms.samples.vision.ocrreader.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.samples.vision.ocrreader.DataBase.DBHelper;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;



/**
 * Created by MorcosS on 7/31/16.
 */
public class sendService extends BroadcastReceiver {
    private static String SOAP_ACTION1 = "http://tempuri.org/GetData";
    private static String NAMESPACE = "http://tempuri.org/";
    private static String METHOD_NAME1 = "GetData";
    private static String METHOD_NAME2 = "GetDataResponse";
    private static String URL = "http://comws.vitalpro.net/webservice2.asmx";
    boolean isConnected ;

    @Override
    public void onReceive(Context context, Intent intent) {
       DBHelper dbHelper = new DBHelper(context);
        Cursor cursor = dbHelper.getOrder();
        if(isNetworkAvailable(context)){
            Log.v("hihello", "hi");
            if(cursor!=null){
                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME1);

                //Use this to add parameters
                if (cursor.moveToFirst()) {
                    do {
                        try{
                            request.addProperty("SubscriberID", cursor.getString(2));
                        request.addProperty("MeterReading", cursor.getString(1));
                        request.addProperty("ReadingTime", cursor.getString(3));

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
                                dbHelper.deleteOrder(cursor.getInt(0));
                                //Get the first property and change the label text
                                Toast.makeText(context, "تم ارسال القراءة بنجاح", Toast.LENGTH_LONG).show();

                            } else if(result.toString().equals("0")){
                                Log.v("hihello", result.toString());
                                Toast.makeText(context, "هذا المستخدم غير موجود بقاعدة البيانات بالرجاء المحاولة مرة أخري ", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.v("hihello",cursor.getString(2)+"");
                            Log.v("hihello", e.toString());

                        }
                        }catch (java.lang.ArrayIndexOutOfBoundsException exception){
                            Toast.makeText(context, "بالرجاء إعادة التصوير مرة أخري ", Toast.LENGTH_LONG).show();
                        }
                    } while (cursor.moveToNext());
                }
            }
        }
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        if(!isConnected){
                            isConnected = true;
                            Toast.makeText(context, "DONE", Toast.LENGTH_SHORT).show();
                            //do your processing here ---
                            //if you need to post any data to the server or get status
                            //update from the server
                        }
                        return true;
                    }
                }
            }
        }
        isConnected = false;
        return false;
    }

    }

