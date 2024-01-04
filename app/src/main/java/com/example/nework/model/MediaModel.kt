package com.example.nework.model

import android.net.Uri
import com.example.nework.enums.AttachmentType

data class MediaModel(
    val uri: Uri? = null,
    val byteArray: ByteArray? = null,
    val attachmentType: AttachmentType? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MediaModel

        if (uri != other.uri) return false
        if (byteArray != null) {
            if (other.byteArray == null) return false
            if (!byteArray.contentEquals(other.byteArray)) return false
        } else if (other.byteArray != null) return false
        if (attachmentType != other.attachmentType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = uri?.hashCode() ?: 0
        result = 31 * result + (byteArray?.contentHashCode() ?: 0)
        result = 31 * result + (attachmentType?.hashCode() ?: 0)
        return result
    }

}