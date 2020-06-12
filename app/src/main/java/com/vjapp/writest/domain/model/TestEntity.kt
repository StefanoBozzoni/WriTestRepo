package com.vjapp.writest.domain.model

import android.view.textclassifier.TextClassificationSessionId
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.vjapp.writest.data.local.database.DatabaseConstants
import java.util.*

data class TestEntity(
    val sendDate   : Date,
    val token      : String,
    val videoUri   : String,
    val imgUri     : String,
    val school     : String,
    val classType  : String,
    val iDSchool   : String,
    val iDClassType: String,
    var idTest     : Int?
)
{
    constructor() : this(Date(), "",
        "", "","",
        "", "", "",
        0
    )
}