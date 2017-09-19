package systembatarka.com.batarka.app.batarkasystem;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import systembatarka.com.batarka.app.batarkasystem.Adapters.KashfFaslAdapter;
import systembatarka.com.batarka.app.batarkasystem.Data.m5dumData;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class KashfFasly extends AppCompatActivity {
    public static KashfFaslAdapter myAdapter;
    public static ArrayList<m5dumData> kashfFaslList;
    Activity activity;
    DatabaseReference m5dumRef;
    com.google.firebase.database.DataSnapshot myChild;













}