package com.fdf.base.utils

import android.os.Parcelable
import com.fdf.base.BaseConfig
import com.tencent.mmkv.MMKV
import java.util.*

/**
 *
 *  author : JiangKun
 *  e-mail : jiangkuikui001@gmail.com
 *  time   : 2021/12/01
 *  desc   :
 *
 */

val mmkv: MMKV by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
    MMKV.mmkvWithID(BaseConfig.config?.getCacheId() ?: "mmkvId")
}

object CacheUtil {

    fun save(key: String, value: Any) {
        when (value) {
            is String -> mmkv.encode(key, value)
            is Float -> mmkv.encode(key, value)
            is Boolean -> mmkv.encode(key, value)
            is Int -> mmkv.encode(key, value)
            is Long -> mmkv.encode(key, value)
            is Double -> mmkv.encode(key, value)
            is ByteArray -> mmkv.encode(key, value)
            is Nothing -> return

        }

    }

    fun saveSet(key: String, sets: Set<String>) {
        mmkv.encode(key, sets)

    }

    fun saveParcelable(key: String, obj: Parcelable) {
        mmkv.encode(key, obj)
    }

    fun getInt(key: String): Int {
        return mmkv.decodeInt(key, 0)
    }

    fun getDouble(key: String): Double {
        return mmkv.decodeDouble(key, 0.00)
    }

    fun getLong(key: String): Long {
        return mmkv.decodeLong(key, 0L)
    }

    fun getBoolean(key: String): Boolean {
        return mmkv.decodeBool(key, false)
    }

    fun getBooleanTrue(key: String): Boolean {
        return mmkv.decodeBool(key, true)
    }

    fun getFloat(key: String): Float? {
        return mmkv.decodeFloat(key, 0F)
    }

    fun getByteArray(key: String): ByteArray? {
        return mmkv.decodeBytes(key)
    }

    fun getString(key: String): String? {
        return mmkv.decodeString(key, "")
    }

    fun getStringDef(key: String, def: String): String? {
        return mmkv.decodeString(key, def)
    }

    fun getStringSet(key: String): Set<String> {
        return mmkv.decodeStringSet(key, Collections.emptySet()) as Set<String>
    }

    fun <T : Parcelable> getParcelable(key: String, tClass: Class<T>): T? {
        return mmkv.decodeParcelable(key, tClass, null)
    }

    fun removeKey(key: String) {
        mmkv.removeValueForKey(key)
    }

    fun clearAll() {
        mmkv.clearAll()
    }
}