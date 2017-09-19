package systembatarka.com.batarka.app.batarkasystem.Data;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by MorcosS on 8/18/16.
 */
public class FireBaseDB
{
    DatabaseReference myClassRef;
    String myClass;
    m5dumData m5duum;
    DataSnapshot myChild;
     public FireBaseDB(Context context){
         final SharedPreferences sharedPref =  context.getSharedPreferences("SharedPreference", Activity.MODE_PRIVATE);
         final SharedPreferences.Editor editor = sharedPref.edit();
         myClass=sharedPref.getString("Class","");
         myClassRef = FirebaseDatabase.getInstance().getReference("Classes").child(myClass);
         myClassRef.keepSynced(true);

     }

    public m5dumData getM5dum(int id){
        myClassRef.child(id+"").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                DataSnapshot dataSnapshot = snapshot.child("m5dumin");
                m5duum = new m5dumData(dataSnapshot.child("Name").getValue().toString(),
                        dataSnapshot.child("Photo").getValue().toString(), dataSnapshot.child("Address").getValue().toString(),
                        dataSnapshot.child("FloorNo").getValue().toString(), dataSnapshot.child("FlatNo").getValue().toString(),
                        dataSnapshot.child("Mobile").getValue().toString(), dataSnapshot.child("Phone").getValue().toString(),
                        dataSnapshot.child("FatherMob").getValue().toString(), dataSnapshot.child("MotherMob").getValue().toString(),
                       dataSnapshot.child("Points").child("PointsTotal").getValue().toString(), dataSnapshot.child("DOB").getValue().toString(), dataSnapshot.getKey().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return m5duum;
    }

    public ArrayList<Integer> getAbsence(String date){
        final ArrayList<Integer> absence = new ArrayList<Integer>();
        myClassRef.child("Attendance").child(date).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<com.google.firebase.database.DataSnapshot> myChildren = dataSnapshot.getChildren();
                int i = 0;
                while (myChildren.iterator().hasNext()) {

                    myChild = myChildren.iterator().next();
                    if(Boolean.parseBoolean(myChild.getValue().toString())==true){
                        i++;
                        absence.add(Integer.parseInt(myChild.getKey().toString()));
                    }
                }
                Log.v("BatarkaSys",i+" ahooo");
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return absence;
    }

    public ArrayList<String> getDates(){
        final ArrayList<String> dates = new ArrayList<String>();
        myClassRef.child("Attendance").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<com.google.firebase.database.DataSnapshot> myChildren = dataSnapshot.getChildren();
                while (myChildren.iterator().hasNext()) {
                    int i = 0;
                    myChild = myChildren.iterator().next();
                  dates.add(myChild.getKey().toString());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return dates;
    }
}
