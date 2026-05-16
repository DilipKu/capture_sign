package com.dilip.mysignature.ui.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.dilip.mysignature.data.model.Signature
import com.dilip.mysignature.data.repository.SignatureRepository

class SignatureViewModel(private val repository: SignatureRepository) : ViewModel() {
    private val _signatures = mutableStateListOf<Signature>()
    val signatures: List<Signature> = _signatures

    fun loadSignatures() {
        val list = repository.getSavedSignatures()
        _signatures.clear()
        _signatures.addAll(list)
    }
}
