package com.gt.gzclaimapp.gson

import com.google.gson.annotations.SerializedName

class BaseGsonArray<out T>(@SerializedName("status") val status :Int = 0,
                           @SerializedName("message") val message:String = "",
                           @SerializedName("detail") val detail: List<T> = listOf())