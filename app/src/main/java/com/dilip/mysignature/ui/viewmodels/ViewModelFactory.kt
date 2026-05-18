package com.dilip.mysignature.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dilip.mysignature.data.repository.FirmRepository
import com.dilip.mysignature.data.repository.SignatureRepository

class ViewModelFactory(private val repository: Any) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(SignatureViewModel::class.java) -> {
                SignatureViewModel(repository as SignatureRepository) as T
            }
            modelClass.isAssignableFrom(FirmViewModel::class.java) -> {
                FirmViewModel(repository as FirmRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
