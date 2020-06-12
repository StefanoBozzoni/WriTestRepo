package com.vjapp.writest.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.vjapp.writest.data.local.database.DatabaseConstants

@Entity(tableName = DatabaseConstants.TABLE_QUEUE)
data class CachedQueue (
    val operation: String,
    val token: String,
    val email: String,
    val diagnosi : String,
    @PrimaryKey(autoGenerate = true)
    val idItem      : Int?
)
