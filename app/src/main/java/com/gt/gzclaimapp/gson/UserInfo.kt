package com.gt.gzclaimapp.gson

import com.google.gson.annotations.SerializedName

class UserInfo(
        @SerializedName("email") val email: String = "",
        @SerializedName("sessionId") val sessionId: String = "",
        @SerializedName("realName") val realName: String = "",
        @SerializedName("lastTime") val lastTime: Long = 0,
        @SerializedName("userId") val userId: Int = 0,
        @SerializedName("portrait") val portrait: String = "",
        @SerializedName("position") val position: String = "",
        @SerializedName("role") val role: Int = 0
)