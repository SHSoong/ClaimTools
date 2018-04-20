package com.gt.gzclaimapp.databinding

import android.content.Context
import com.blankj.utilcode.util.TimeUtils
import com.gt.gzclaimapp.R
import com.gt.gzclaimapp.gson.ClaimDailyRecord
import com.gt.gzclaimapp.ui.claim.ClaimsMonthlyActivity.Companion.TYPE_EQP
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class ClaimsDailyData() {

    lateinit var claimDate: String
    lateinit var applyDate: String
    lateinit var amount: String
    lateinit var type: String
    lateinit var claimDailyRecord: ClaimDailyRecord

    private val eqpTypeString = arrayOf(R.string.claim_type_eqp_0, R.string.claim_type_eqp_1, R.string.claim_type_eqp_2)
    private val admTypeString = arrayOf(R.string.claim_type_adm_0, R.string.claim_type_adm_1, R.string.claim_type_adm_2, R.string.claim_type_adm_3)

    constructor(record: ClaimDailyRecord, context: Context, type: Int) : this() {
        claimDate = TimeUtils.millis2String(record.date, SimpleDateFormat("MM/dd", Locale.CHINESE))
        applyDate = context.getString(R.string.prefix_time, TimeUtils.millis2String(record.date, SimpleDateFormat("MM/dd HH:mm", Locale.CHINESE)))
        val decimalFormat = DecimalFormat("###################.###########")
        amount = context.getString(R.string.prefix_amount, decimalFormat.format(record.totalPrice))
        this.type = if (type == TYPE_EQP) {
            "报销类型：${context.getString(eqpTypeString[record.claimType])}"
        } else {
            "报销类型：${context.getString(admTypeString[record.claimType])}"
        }
        this.claimDailyRecord = record
    }

}