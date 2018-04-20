package com.gt.gzclaimapp.gson

import com.google.gson.annotations.SerializedName

data class OTDailyRecord(
        @SerializedName("checkInId") val checkInId: Int = 0,
        @SerializedName("otDate") val otDate: Long = 0,
        @SerializedName("checkInDate") val checkInDate: Long = 0,
        @SerializedName("checkInType") val checkInType: Int = 0,
        @SerializedName("projectName") val projectName: String = "",
        @SerializedName("price") val price: Double = 0.0,
        @SerializedName("editEnable") val editEnable: Int = 0
)