package com.gt.gzclaimapp.bean

import com.gt.gzclaimapp.gson.OTDetailInfo

class GetOtInfoBean() {

    var checkInId: Int = 0
    var projectName: String = ""
    var checkInDate: Long = 0
    var foodSubsidies: Int = 0
    var trafficSubsidies: Double = 0.0
    var checkInType: Int = 0
    var images: List<UploadFileBean>? = null

    constructor(detail: OTDetailInfo) : this() {
        this.checkInId = detail.checkInId
        this.projectName = detail.projectName
        this.checkInDate = detail.checkInDate
        this.foodSubsidies = detail.foodSubsidies
        this.trafficSubsidies = detail.trafficSubsidies
        this.checkInType = detail.checkInType
        this.images = detail.images
    }

}