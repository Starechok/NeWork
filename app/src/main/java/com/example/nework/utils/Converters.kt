package com.example.nework.utils

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun fromSet(set: Set<Long>): String = set.joinToString(",")

    @TypeConverter
    fun toSet(data: String): Set<Long> =
        if (data.isBlank()) emptySet()
        else data.split(",").map { it.toLong() }.toSet()
}