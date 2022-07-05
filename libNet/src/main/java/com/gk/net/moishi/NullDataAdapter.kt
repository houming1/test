package com.gk.net.moishi

import com.squareup.moshi.*

/**
 *    Created by Administrator on 2021/12/6.
 *    Desc :
 */
object NullDataAdapter {

    @FromJson
    fun fromJson(reader: JsonReader): Any? {
        if (reader.peek() == JsonReader.Token.NULL) {
            return reader.readJsonValue()
        }
        reader.nextNull<Unit>()
        return null
    }
}

object BooleanAdapter {

    @FromJson
    fun fromJson(reader: JsonReader): Boolean {
        if (reader.peek() == JsonReader.Token.STRING) {
            try {
                return reader.nextString().toBoolean()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else if (reader.peek() == JsonReader.Token.BOOLEAN) {
            return reader.nextBoolean()
        }
        return false
    }
}


object NullStringAdapter {
    @FromJson
    fun fromJson(reader: JsonReader): String {
        if (reader.peek() != JsonReader.Token.NULL) {
            return reader.nextString()
        }
        reader.nextNull<Unit>()
        return ""
    }
}