package com.dilip.mysignature.data.model

import android.net.Uri

data class Signature(
    val uri: Uri,
    val name: String? = null
)
