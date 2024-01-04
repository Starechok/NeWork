package com.example.nework.dto

import com.example.nework.enums.AttachmentType

data class Attachment(
    val url: String,
    val type: AttachmentType,
)
