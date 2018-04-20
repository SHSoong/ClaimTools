package com.gt.gzclaimapp.gson

import com.google.gson.annotations.SerializedName


data class ToDoList(
        @SerializedName("checkInDate") val checkInDate: Long = 0,
        @SerializedName("checkInId") val checkInId: Int = 0,
        @SerializedName("checkInType") val checkInType: Int = 0,
        @SerializedName("restDate") val restDate: Long = 0,
        @SerializedName("userName") val userName: String = ""
)
