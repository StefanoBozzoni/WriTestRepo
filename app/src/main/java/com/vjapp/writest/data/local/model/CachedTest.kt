package com.vjapp.writest.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.vjapp.writest.data.local.database.DatabaseConstants
import java.util.*

@Entity(tableName = DatabaseConstants.TABLE_TESTS)
data class CachedTest(
    val sendDate    : Date,
    val token       : String,
    val videoUri    : String,
    val imgUri      : String,
    val school      : String,
    val classType   : String,
    val iDSchool    : String,
    val iDClassType : String,
    val diagnosis   : String?,
    @PrimaryKey(autoGenerate = true)
    val idTest      : Int?
){
    //@PrimaryKey(autoGenerate = true)
    //var idTest  : Int = 1
    constructor() : this(Date(), "",
        "", "","",
        "", "", "","No",
        0
    )
}