package com.dcc.gawykora

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import com.dcc.gawykora.Adapter.ElmanArrayAdapter
import com.dcc.gawykora.Data.ElMan
import com.dcc.gawykora.Data.Timeline
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.full.activity_main.*
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.full.content_main.*
import java.text.FieldPosition


class MainActivity : AppCompatActivity() {
    val TAG = "GawyKora"
    var ElMensArray =  ArrayList<ElMan>()
    var activity :Activity = Activity();
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        activity=this;
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        setSupportActionBar(this.toolbar)
        val elMens =  FirebaseDatabase.getInstance().getReference("Elmens")
        var elmensAdapter = ElmanArrayAdapter(ElMensArray,activity);
        activity.listView.setAdapter(elmensAdapter);
        elMens.keepSynced(true)
        activity.listView.setOnItemClickListener({ parent, view, position, id ->
            val intent = Intent(activity, ElManDetail::class.java)
            intent.putExtra("elMan",ElMensArray.get(position));
            startActivity(intent);
        })
        val elmensListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                ElMensArray.clear()
                // Get Post object and use the values to update the UI
                for ( elmanSnapshot in dataSnapshot.getChildren()) {
                    val elMan = elmanSnapshot.getValue<ElMan>(ElMan::class.java)
                    elMan.Key =  elmanSnapshot.key;
                    if(elmanSnapshot.child("Timeline")!==null) {
                        for (timeLine in elmanSnapshot.child("Timeline").getChildren()) {
                            val timeline = timeLine.getValue<Timeline>(Timeline::class.java)
                            elMan.TimeLine.add(timeline)
                        }
                    }
                    ElMensArray.add(elMan);

                }
                // ...
                elmensAdapter.notifyDataSetChanged()
                activity.listView.deferNotifyDataSetChanged()

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
                // ...
            }
        }
        elMens.addValueEventListener(elmensListener)
        }




    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}
