package com.dilip.mysignature.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "firm_users")
data class FirmUser(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val mail: String,
    val mobile: String,
    val firmName: String
)
