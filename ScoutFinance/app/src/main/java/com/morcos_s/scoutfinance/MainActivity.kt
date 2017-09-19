package com.morcos_s.scoutfinance

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.morcos_s.scoutfinance.Data.ScoutAccount

class MainActivity : AppCompatActivity() {
    val TAG = "Scout Finance"
    var sharedPref: SharedPreferences? = null
    var AccountsArray =  ArrayList<ScoutAccount>()
    var activity : Activity = Activity();
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        activity=this
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        sharedPref=applicationContext.getSharedPreferences("SharedPreference", Context.MODE_PRIVATE)
        var TeamName = sharedPref?.getString(getString(R.string.myTeam),"")
        val Database =  FirebaseDatabase.getInstance().getReference(TeamName)
        val Listener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                AccountsArray.clear()
                // Get Post object and use the values to update the UI
                for ( elmanSnapshot in dataSnapshot.getChildren()) {
                    val elMan = elmanSnapshot.getValue<ScoutAccount>(ScoutAccount::class.java)
                    elMan.Key =  elmanSnapshot.key
                    AccountsArray.add(elMan)

                }
                // ...
                //elmensAdapter.notifyDataSetChanged()
               // activity.listView.deferNotifyDataSetChanged()

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
                // ...
            }
        }
        Database.addValueEventListener(Listener)
                val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
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
            finish()
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}
