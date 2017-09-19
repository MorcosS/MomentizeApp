package com.dcc.gawykora.Data

import java.io.Serializable
import java.util.Date

/**
 * Created by marcos on 6/3/17.
 */

class Timeline  : Serializable {
    var action: String= ""
        get() = field
        set(value) { field = value }

    var focus: String= ""
        get() = field
        set(value) { field = value }

    var date: String= Date().toString()
        get() = field
        set(value) { field = value }

    constructor() {}

    constructor(action: String, focus: String,date: String) {
        this.action = action
        this.focus = focus
        this.date = date
    }

    fun getFocusResource(): String {
        return "g" + focus
    }
}
