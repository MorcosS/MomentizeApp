package com.morcos_s.scoutfinance.Data

import java.io.Serializable

/**
 * Created by marcos on 6/18/17.
 */

class ScoutAccount : Serializable {
    var Name: String = ""
        get() = field
        set(value) { field = value }

    var Credit: Int = 0
        get() = field
        set(value) { field = value }

    var Key: String = ""
        get() = field
        set(value) { field = value }


    constructor( name: String, credit: Int) {
        this.Name = name
        this.Credit = credit
    }

    constructor() {}


}
