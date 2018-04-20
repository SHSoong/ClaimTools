package com.gt.gzclaimapp.gson

import com.google.gson.annotations.SerializedName


data class CommonResult(
        @SerializedName("status") val status: Int = 0,
        @SerializedName("message") val message: String = ""
)
