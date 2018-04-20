package com.gt.gzclaimapp.ui.claim

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.gt.gzclaimapp.R
import com.gt.gzclaimapp.databinding.ClaimsDailyData
import com.gt.gzclaimapp.gson.ClaimDailyRecord
import com.gt.gzclaimapp.manager.IoMainScheduler
import com.gt.gzclaimapp.manager.RetrofitManager
import com.gt.gzclaimapp.ui.adapter.ClaimsDailyAdapter
import com.gt.gzclaimapp.ui.base.BaseActivity
import com.gt.gzclaimapp.ui.claim.ClaimsMonthlyActivity.Companion.TYPE_ADM
import com.gt.gzclaimapp.ui.claim.ClaimsMonthlyActivity.Companion.TYPE_EQP
import com.gt.gzclaimapp.utils.ConstantsUtils
import com.gt.gzclaimapp.utils.ConstantsUtils.Companion.INTENT_CLAIM_RECORD_ID
import com.gt.gzclaimapp.utils.PreferenceUtils
import kotlinx.android.synthetic.main.activity_single_recyclerview.*
import kotlinx.android.synthetic.main.common_title_bar.*

class ClaimsDailyActivity : BaseActivity() {

    private var sessionId by PreferenceUtils(this, ConstantsUtils.SAVE_SESSION_ID, "")
    private lateinit var claimsDailyAdapter: ClaimsDailyAdapter
    private var type: Int = 0
    private var date: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_recyclerview)
        init()
    }

    override fun onResume() {
        super.onResume()
        callAPI()
    }

    private fun init() {
        type = intent.getIntExtra(ConstantsUtils.INTENT_CLAIM_TYPE, 0)
        date = intent.getLongExtra(ConstantsUtils.INTENT_OT_DATE, 0)

        leftBack.visibility = View.VISIBLE
        leftBack.setOnClickListener { finish() }
        tv_title.text = getTitleByType()
        rightOther.visibility = View.VISIBLE
        rightOther.setOnClickListener {
            startActivity(Intent(this, ClaimsDetailActivity::class.java)
                    .putExtra(ConstantsUtils.INTENT_CLAIM_TYPE, type))
        }

        claimsDailyAdapter = ClaimsDailyAdapter()
        claimsDailyAdapter.setOnItemClickListener({ _, _, position ->
            startActivity(Intent(this, ClaimsDetailActivity::class.java)
                    .putExtra(INTENT_CLAIM_RECORD_ID, claimsDailyAdapter.data[position].claimDailyRecord.recordId)
                    .putExtra(ConstantsUtils.INTENT_CLAIM_TYPE, type))
        })

        singleRecyclerView.layoutManager = LinearLayoutManager(this)
        singleRecyclerView.adapter = claimsDailyAdapter
    }

    private fun getTitleByType(): String {
        return when (type) {
            TYPE_EQP ->
                getString(R.string.claim_title_eqp)
            TYPE_ADM ->
                getString(R.string.claim_title_adm)
            else ->
                getString(R.string.app_name)
        }
    }

    private fun callAPI() {
        showLoading()
        val disposable = RetrofitManager.service.claimDailyRecord(sessionId, date, type)
                .compose(IoMainScheduler())
                .arrayResult()
                .map { list -> mapData(list) }
                .subscribe({ claimRecordList ->
                    hideLoading()
                    claimsDailyAdapter.setNewData(claimRecordList)
                }, { _: Throwable? ->
                    hideLoading()
                })
        addSubscription(disposable)
    }

    private fun mapData(list: List<ClaimDailyRecord>): List<ClaimsDailyData>? {
        return list.map { ClaimsDailyData(it, this, type) }
    }

}
