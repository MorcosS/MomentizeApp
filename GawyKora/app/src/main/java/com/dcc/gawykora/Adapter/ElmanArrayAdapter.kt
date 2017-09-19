package com.dcc.gawykora.Adapter

import android.app.Activity
import android.graphics.Bitmap
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

import com.dcc.gawykora.Data.ElMan
import com.dcc.gawykora.R

import java.util.ArrayList
import kotlinx.android.synthetic.main.elman_item.*
/**
 * Created by marcos on 5/26/17.
 */

class ElmanArrayAdapter : BaseAdapter {
    internal var elMenList: ArrayList<ElMan>? = null
    internal var activity: Activity
    internal var inflater: LayoutInflater? = null

    constructor(elManList: ArrayList<ElMan>, activity: Activity) {
        this.elMenList = elManList
        this.activity = activity
        this.inflater = activity.layoutInflater;
    }


    override fun getCount(): Int {
        return elMenList!!.size
    }

    override fun getItem(position: Int): Any {
        return elMenList!![position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var retView: View

        if (convertView == null) {
            retView = inflater!!.inflate(R.layout.elman_item, null)

        } else {
            retView = convertView
        }
        var name = retView.findViewById(R.id.name) as TextView
        name.setText(elMenList!!.get(position).Name)

        var team = retView.findViewById(R.id.team) as TextView
        team.setText(elMenList!!.get(position).Team)

        var score = retView.findViewById(R.id.score) as TextView
        score.setText(elMenList!!.get(position).Total.toString())



        var image = retView.findViewById(R.id.imageView) as ImageView
        var myTeamResource = elMenList!!.get(position).getTeamResource();
        image.setImageResource(activity.resources.getIdentifier(myTeamResource,"drawable", activity.packageName));

        return retView
    }


}
