package com.dcc.gawykora.Adapter

import android.app.Activity
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.dcc.gawykora.Data.ElMan
import com.dcc.gawykora.Data.Timeline
import com.dcc.gawykora.R
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by marcos on 6/3/17.
 */

class TimelineAdapter : BaseAdapter {
    internal var timelineList: ArrayList<Timeline>? = null
    internal var activity: Activity
    internal var inflater: LayoutInflater? = null

    constructor(timelineList: ArrayList<Timeline>, activity: Activity) {
        this.timelineList = timelineList
        this.activity = activity
        this.inflater = activity.layoutInflater;
    }


    override fun getCount(): Int {
        return timelineList!!.size
    }

    override fun getItem(position: Int): Any {
        return timelineList!![position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var retView: View

        if (convertView == null) {
            retView = inflater!!.inflate(R.layout.timline_item_layout, null)

        } else {
            retView = convertView
        }
        var action = retView.findViewById(R.id.action) as TextView
        action.setText(timelineList!!.get(position).action)

        var date = retView.findViewById(R.id.date) as TextView
        var myDate : String
        val format = SimpleDateFormat("EEE, MMM yyyy  hh:mm a")

        try{
            myDate = format.format(Date.parse(timelineList!!.get(position).date))
            date.setText(myDate)
        }catch(e:Exception){
            date.setText(timelineList!!.get(position).date)
        }

        var color = retView.findViewById(R.id.color) as ImageView
        if(timelineList!![position].focus.toCharArray()[0]=='0'){
            color.setBackgroundColor(Color.RED)
        }else{
            color.setBackgroundColor(activity.getResources().getColor(R.color.green))
        }
        var image = retView.findViewById(R.id.emoji) as ImageView
        var myTeamResource = timelineList!!.get(position).getFocusResource();
        image.setImageResource(activity.resources.getIdentifier(myTeamResource,"drawable", activity.packageName));

        return retView
    }


}
