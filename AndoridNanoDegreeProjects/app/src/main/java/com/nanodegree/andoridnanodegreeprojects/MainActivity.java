package com.nanodegree.andoridnanodegreeprojects;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



    }

    public void setToast(View view){
        Button b =(Button) view;
        Toast.makeText(getApplicationContext(),"This Button Will launch "+b.getText()+" Project.",Toast.LENGTH_LONG).show();

    }

}
