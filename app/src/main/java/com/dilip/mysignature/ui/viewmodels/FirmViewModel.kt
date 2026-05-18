package com.dilip.mysignature.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dilip.mysignature.data.model.FirmUser
import com.dilip.mysignature.data.repository.FirmRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FirmViewModel(private val repository: FirmRepository) : ViewModel() {

    val allFirmUsers: StateFlow<List<FirmUser>> = repository.allFirmUsers
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addFirmUser(mail: String, mobile: String, firmName: String) {
        viewModelScope.launch {
            repository.insert(FirmUser(mail = mail, mobile = mobile, firmName = firmName))
        }
    }

    fun deleteFirmUser(firmUser: FirmUser) {
        viewModelScope.launch {
            repository.delete(firmUser)
        }
    }
}
