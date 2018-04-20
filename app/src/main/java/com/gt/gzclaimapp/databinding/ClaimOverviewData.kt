package com.gt.gzclaimapp.databinding

import com.blankj.utilcode.util.TimeUtils
import com.gt.gzclaimapp.gson.ClaimMonthRecord
import com.gt.gzclaimapp.gson.OTMonthlyRecord
import com.gt.gzclaimapp.utils.StringUtils
import java.text.SimpleDateFormat
import java.util.*

class ClaimOverviewData() {

    lateinit var otDate: String
    lateinit var apply: String
    lateinit var amount: String
    var date: Long = 0

    constructor(otRecord: OTMonthlyRecord) : this() {
        otDate = TimeUtils.millis2String(otRecord.otDate, SimpleDateFormat("MM/yyyy", Locale.CHINESE))
        apply = otRecord.applyCount.toString()
        amount = StringUtils.moneyDecimalFormat(otRecord.totalPrice)
        date = otRecord.otDate
    }

    constructor(claimRecord: ClaimMonthRecord) : this() {
        otDate = TimeUtils.millis2String(claimRecord.date, SimpleDateFormat("MM/yyyy", Locale.CHINESE))
        apply = claimRecord.applyCount.toString()
        amount = StringUtils.moneyDecimalFormat(claimRecord.totalPrice)
        date = claimRecord.date
    }

}