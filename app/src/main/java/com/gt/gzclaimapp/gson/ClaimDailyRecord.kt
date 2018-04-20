package com.gt.gzclaimapp.gson

import com.google.gson.annotations.SerializedName

data class ClaimDailyRecord(
        @SerializedName("recordId") val recordId: Int = 0,
        @SerializedName("date") val date: Long = 0,
        @SerializedName("claimType") val claimType: Int = 0,
        @SerializedName("totalPrice") val totalPrice: Double = 0.0
)