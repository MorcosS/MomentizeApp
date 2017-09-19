package com.morcos_s.scoutfinance

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import kotlinx.android.synthetic.main.activity_team_selector.*

class TeamSelector : AppCompatActivity() {
     var sharedPref: SharedPreferences ? = null
    internal var editor: SharedPreferences.Editor ?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_team_selector)
        sharedPref=applicationContext.getSharedPreferences("SharedPreference",Context.MODE_PRIVATE)
        editor = sharedPref?.edit()
        if(!sharedPref?.getString(getString(R.string.myTeam),"").equals("")){
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    fun onSavePressed(view: View){
        editor?.putString(getString(R.string.myTeam), this.spinner.selectedItem.toString());
        editor?.commit();
        startActivity(Intent(this, MainActivity::class.java));
    }

}
