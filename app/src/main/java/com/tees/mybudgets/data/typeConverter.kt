package com.tees.mybudgets.data

import androidx.room.TypeConverter
import java.time.LocalDate


/*This class defines methods to convert a LocalDate object to a String and vice versa for use with Room database
* It is necessitated by fact that Room only supports storing primitive data types*/

class Converters {
    @TypeConverter
    fun fromLocalDate(date: LocalDate): String = date.toString()

    @TypeConverter
    fun toLocalDate(dateString: String): LocalDate = LocalDate.parse(dateString)
}


