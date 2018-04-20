package com.gt.gzclaimapp.utils

import android.annotation.SuppressLint
import android.content.Context
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class PreferenceUtils<T>(context: Context) : ReadWriteProperty<Any?, T> {

    private var key: String? = null
    private var value: T? = null

    constructor(context: Context, name: String, default: T) : this(context) {
        key = name
        value = default
    }

    private val prefs by lazy { context.getSharedPreferences("default", Context.MODE_PRIVATE) }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return findPreference(key!!, value!!)
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        putPreference(key!!, value)
    }

    @SuppressLint("ApplySharedPref")
    fun delete(vararg key: String) {
        if (key.isEmpty()) {
            prefs.edit().clear().commit()
            return
        }
        for (i in 0 until key.size) {
            prefs.edit().remove(key[i]).commit()
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <U> findPreference(name: String, default: U): U = with(prefs) {
        val res: Any = when (default) {
            is Long -> getLong(name, default)
            is String -> getString(name, default)
            is Int -> getInt(name, default)
            is Boolean -> getBoolean(name, default)
            is Float -> getFloat(name, default)
            else -> throw IllegalArgumentException("The data can not be saved")
        }
        res as U
    }

    @SuppressLint("CommitPrefEdits")
    private fun <U> putPreference(name: String, value: U) = with(prefs.edit()) {
        when (value) {
            is Long -> putLong(name, value)
            is String -> putString(name, value)
            is Int -> putInt(name, value)
            is Boolean -> putBoolean(name, value)
            is Float -> putFloat(name, value)
            else -> throw IllegalArgumentException("The data can not be saved")
        }.apply()
    }
}