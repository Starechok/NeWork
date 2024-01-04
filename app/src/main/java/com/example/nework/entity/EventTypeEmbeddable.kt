package com.example.nework.entity

import com.example.nework.enums.EventType


data class EventTypeEmbeddable(
    val eventType: String,
) {
    fun toDto() = EventType.valueOf(eventType)

    companion object {
        fun fromDto(dto: EventType) = EventTypeEmbeddable(dto.name)
    }
}


