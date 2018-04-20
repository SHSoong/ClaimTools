package com.gt.gzclaimapp.databinding

import android.content.Context
import com.blankj.utilcode.util.TimeUtils
import com.gt.gzclaimapp.R
import com.gt.gzclaimapp.gson.OTDailyRecord
import com.gt.gzclaimapp.utils.StringUtils
import java.text.SimpleDateFormat
import java.util.*

class OtClaimDailyData() {

    lateinit var claimDate: String
    lateinit var applyDate: String
    lateinit var amount: String
    lateinit var type: String
    lateinit var project: String
    var editEnable: Int = 0
    lateinit var monthOTRecord: OTDailyRecord

    private val typeString = arrayOf(R.string.overview_type_none, R.string.overview_type_none, R.string.overview_type_half, R.string.overview_type_full)

    constructor(monthOTRecord: OTDailyRecord, context: Context) : this() {
        claimDate = TimeUtils.millis2String(monthOTRecord.otDate, SimpleDateFormat("MM/dd", Locale.CHINESE))
        applyDate = context.getString(R.string.prefix_time, TimeUtils.millis2String(monthOTRecord.checkInDate, SimpleDateFormat("MM/dd HH:mm", Locale.CHINESE)))

        amount = context.getString(R.string.prefix_amount, StringUtils.moneyDecimalFormat(monthOTRecord.price))
        type = context.getString(typeString[monthOTRecord.checkInType - 1])
        project = context.getString(R.string.overview_project, monthOTRecord.projectName)
        editEnable = monthOTRecord.editEnable
        this.monthOTRecord = monthOTRecord
    }

}