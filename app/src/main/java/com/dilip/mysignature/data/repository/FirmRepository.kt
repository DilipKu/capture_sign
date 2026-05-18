package com.dilip.mysignature.data.repository

import com.dilip.mysignature.data.db.FirmUserDao
import com.dilip.mysignature.data.model.FirmUser
import kotlinx.coroutines.flow.Flow

class FirmRepository(private val firmUserDao: FirmUserDao) {
    val allFirmUsers: Flow<List<FirmUser>> = firmUserDao.getAllFirmUsers()

    suspend fun insert(firmUser: FirmUser) {
        firmUserDao.insertFirmUser(firmUser)
    }

    suspend fun delete(firmUser: FirmUser) {
        firmUserDao.deleteFirmUser(firmUser)
    }
}
