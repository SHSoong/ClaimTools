package com.gt.gzclaimapp.gson

import com.google.gson.annotations.SerializedName

data class ClaimRecordDetail(
        @SerializedName("recordId") val recordId: Int = 0,
        @SerializedName("remark") val remark: String = "",
        @SerializedName("claimType") val claimType: Int = 0,
        @SerializedName("totalPrice") val totalPrice: Double = 0.0,
        @SerializedName("editable") val editable: Boolean = false
)