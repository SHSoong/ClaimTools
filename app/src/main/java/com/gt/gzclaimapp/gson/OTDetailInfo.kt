package com.gt.gzclaimapp.gson

import com.google.gson.annotations.SerializedName
import com.gt.gzclaimapp.bean.UploadFileBean

class OTDetailInfo(
        @SerializedName("checkInId") val checkInId: Int = 0,
        @SerializedName("projectName") val projectName: String = "",
        @SerializedName("checkInDate") val checkInDate: Long = 0,
        @SerializedName("foodSubsidies") val foodSubsidies: Int = 0,
        @SerializedName("trafficSubsidies") val trafficSubsidies: Double = 0.0,
        @SerializedName("checkInType") val checkInType: Int = 0,
        @SerializedName("images") val images: List<UploadFileBean> = listOf()
)