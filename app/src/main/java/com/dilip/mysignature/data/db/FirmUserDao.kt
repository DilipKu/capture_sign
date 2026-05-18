package com.dilip.mysignature.data.db

import androidx.room.*
import com.dilip.mysignature.data.model.FirmUser
import kotlinx.coroutines.flow.Flow

@Dao
interface FirmUserDao {
    @Query("SELECT * FROM firm_users ORDER BY id DESC")
    fun getAllFirmUsers(): Flow<List<FirmUser>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFirmUser(firmUser: FirmUser)

    @Delete
    suspend fun deleteFirmUser(firmUser: FirmUser)
}
