package com.gt.gzclaimapp.databinding

import com.gt.gzclaimapp.gson.UserInfo
import java.io.Serializable

class UserInfoData() : Serializable {

    var realName: String = ""
    var position: String = ""
    var role: Int = 0
    var sessionId: String = ""

    constructor (detail: UserInfo) : this() {
        this.realName = detail.realName
        this.position = detail.position
        this.role = detail.role
        this.sessionId = detail.sessionId
    }

}