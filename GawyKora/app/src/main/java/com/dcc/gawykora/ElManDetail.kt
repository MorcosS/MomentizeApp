package com.dcc.gawykora

import android.accessibilityservice.GestureDescription
import android.app.PendingIntent.getActivity
import android.content.Intent
import android.os.Bundle
import android.provider.SyncStateContract
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.Toast
import com.dcc.gawykora.Data.ElMan
import com.dcc.gawykora.Data.Timeline

import com.dcc.gawykora.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_el_man_detail.*
import kotlinx.android.synthetic.main.content_el_man_detail.*

class ElManDetail : AppCompatActivity() {
    var elMan : ElMan? = null
    var elMens : DatabaseReference ? = null
    var timeline : Timeline ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_el_man_detail)
        elMan = intent.getSerializableExtra("elMan") as ElMan
        elMens =  FirebaseDatabase.getInstance().getReference("Elmens").child(elMan!!.Key)
        elMens!!.keepSynced(true)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        toolbar.setTitle(elMan!!.Name)
        var myTeamResource = elMan!!.getTeamResource();
        this.teamImage.setImageResource(this.resources.getIdentifier(myTeamResource,"drawable", this.packageName));
        setSupportActionBar(toolbar)
    }

        public fun onFab(view:View){
            val intent = Intent(this, HistoryActivity::class.java)
            intent.putExtra("elMan",elMan);
            startActivity(intent);
        }

        public fun onSave(view: View){
        var myTotal : Int = 0
            timeline = Timeline()
        if (this.attendance.isChecked)
            myTotal+=10
        if (this.bible.isChecked)
            myTotal+=5
        if (this.nota.isChecked)
            myTotal+=5
        if (this.agpeya.isChecked)
            myTotal+=5
        if (this.pen.isChecked)
            myTotal+=5
        if (this.pen.isChecked)
            myTotal+=5
        if (this.spiritual_nota.isChecked)
            myTotal+=20
        if (this.competition.isChecked)
            myTotal+=20

        if(this.decrease.text.toString().length!=0)
            myTotal -= this.decrease.text.toString().toInt()


        if(this.increase.text.toString().length!=0)
            myTotal += this.increase.text.toString().toInt()


        if(myTotal>0){
            timeline!!.action = " تم اضافة " +  myTotal + " علي نقاطك "
            timeline!!.focus = "10"
        }else{
            timeline!!.action = "تم خصم " +  this.decrease.text.toString().toInt() + " من نقاطك "
            timeline!!.focus = "00"
        }

            elMens!!.child("Total").setValue(elMan!!.Total+myTotal)
        var key = elMens!!.child("Timeline").push().key
            elMens!!.child("Timeline").child(key).setValue(timeline)

        Toast.makeText(baseContext,"تم إضافة عدد  "+ myTotal.toString()+"  نقاط"   ,Toast.LENGTH_LONG).show()
        finish()
    }

}
