package com.dilip.mysignature.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dilip.mysignature.data.repository.SignatureRepository

class ViewModelFactory(private val repository: SignatureRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignatureViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SignatureViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
