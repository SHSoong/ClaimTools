package com.gt.gzclaimapp.gson

import com.google.gson.annotations.SerializedName

data class ClaimMonthRecord(
        @SerializedName("date") val date: Long = 0,
        @SerializedName("totalPrice") val totalPrice: Double = 0.0,
        @SerializedName("applyCount") val applyCount: Int = 0
)
