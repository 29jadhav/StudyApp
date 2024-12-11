package com.vivek.studyapp.data.local

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.toColor
import androidx.room.TypeConverter

class ColorListConvertor {

    @TypeConverter
    fun colorListToString(colors: List<Color>): String {
        return colors.map { it.toArgb() }.joinToString(separator = ",")
    }

    @TypeConverter
    fun colorsStringToList(colors: String): List<Color> {
        return colors.split(",").map { Color(it.toInt()) }
    }
}