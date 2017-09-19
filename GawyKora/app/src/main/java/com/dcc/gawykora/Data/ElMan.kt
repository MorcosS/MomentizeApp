package com.dcc.gawykora.Data

import java.io.Serializable
import java.sql.Time

/**
 * Created by marcos on 5/26/17.
 */

class ElMan : Serializable {
    var Name: String = ""
        get() = field
        set(value) { field = value }

    var Total: Int = 0
        get() = field
        set(value) { field = value }

    var Team: String = ""
        get() = field
        set(value) { field = value }

    var Key: String = ""
        get() = field
        set(value) { field = value }

    var TimeLine : ArrayList<Timeline> =  ArrayList<Timeline>()
        get() = field
        set(value) {field =value}

    constructor(team: String, name: String, score: Int) {
        this.Team = team
        this.Name = name
        this.Total = score
    }

    constructor() {}

    fun getTeamResource(): String {
        return "el_" + Team.toLowerCase()
    }

}
