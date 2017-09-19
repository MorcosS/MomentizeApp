package systembatarka.com.batarka.app.batarkasystem;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import systembatarka.com.batarka.app.batarkasystem.Adapters.TafaselM5dumAdapter;
import systembatarka.com.batarka.app.batarkasystem.Data.M5dumDataShow;

public class M5dumDetails extends AppCompatActivity {
    String m5dumName, m5dumPhoto, m5dumAddress, m5dumFloorNo,m5dumFlatNo,m5dumMobile,m5dumPhone,m5dumFatherMob
            ,m5dumMotherMob,m5dumPoints,dob;
    ArrayList<M5dumDataShow> arrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.m5dum_details_layout_design);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        m5dumName = intent.getStringExtra("Name");
        m5dumPhoto = intent.getStringExtra("Photo");
        m5dumAddress = intent.getStringExtra("Address");
        m5dumFloorNo= intent.getStringExtra("FloorNo");
        m5dumFlatNo = intent.getStringExtra("FlatNo");
        m5dumMobile = intent.getStringExtra("Mobile");
        m5dumPhone = intent.getStringExtra("Phone");
        m5dumFatherMob = intent.getStringExtra("FatherMob");
        m5dumMotherMob = intent.getStringExtra("MotherMob");
        m5dumPoints = intent.getStringExtra("Points");
        dob = intent.getStringExtra("DOB");
        arrayList = new ArrayList<M5dumDataShow>();
        addInList();
        TafaselM5dumAdapter tafaselM5dumAdapter = new TafaselM5dumAdapter(arrayList,this);
        ListView ls = (ListView) findViewById(R.id.listView4);
        ls.setAdapter(tafaselM5dumAdapter);
        TextView Name = (TextView) findViewById(R.id.textView6);
        Name.setText(m5dumName);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab1);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        ImageView myPhoto = (ImageView) findViewById(R.id.imageView5);
        byte[] decodedString = Base64.decode(m5dumPhoto, Base64.DEFAULT);
        Bitmap base64Bitmap = BitmapFactory.decodeByteArray(decodedString, 0,
                decodedString.length);
        myPhoto.setImageBitmap(base64Bitmap);
    }

    public void addInList(){
        if(!m5dumAddress.equals(""))
        arrayList.add(new M5dumDataShow(m5dumAddress+"\n"+"الدور: "+m5dumFloorNo+"\n"+"الشقة: "+m5dumFlatNo,"Address","ic_navigation_black_24dp","ic_edit_location_black_24dp"));
        if(!m5dumPhone.equals(""))
        arrayList.add(new M5dumDataShow(m5dumPhone,"Phone","ic_call_black_24dp","ic_call_black_24dp"));
        if(!m5dumMobile.equals(""))
        arrayList.add(new M5dumDataShow(m5dumMobile,"Mobile","ic_call_black_24dp","ic_message_black_24dp"));
        if(!m5dumFatherMob.equals(""))
        arrayList.add(new M5dumDataShow(m5dumFatherMob,"Father Mobile","ic_call_black_24dp","ic_message_black_24dp"));
        if(!m5dumMotherMob.equals(""))
        arrayList.add(new M5dumDataShow(m5dumMotherMob,"Mother Mobile","ic_call_black_24dp","ic_message_black_24dp"));
        if(!dob.equals(""))
        arrayList.add(new M5dumDataShow(dob,"Date Of Birth","ic_insert_invitation_black_24dp","ic_insert_invitation_black_24dp"));
        arrayList.add(new M5dumDataShow(m5dumPoints,"Points","ic_remove_circle_outline_black_24dp","ic_add_circle_outline_black_24dp"));
    }

}
