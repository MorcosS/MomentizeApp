package com.dcc.gawykora

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.dcc.gawykora.Adapter.TimelineAdapter
import com.dcc.gawykora.Data.ElMan
import com.dcc.gawykora.Data.Timeline
import kotlinx.android.synthetic.main.activity_history.*

class HistoryActivity : AppCompatActivity() {
    var elMan : ElMan? = null
    var myTimline = ArrayList<Timeline>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        elMan = intent.getSerializableExtra("elMan") as ElMan
        myTimline = elMan!!.TimeLine
        var timelineAdapter = TimelineAdapter(myTimline,this)
        this.timelineListView.setAdapter(timelineAdapter)
    }
}
