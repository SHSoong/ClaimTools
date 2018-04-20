package com.gt.gzclaimapp.ui.claim

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.gt.gzclaimapp.R
import com.gt.gzclaimapp.databinding.ClaimOverviewData
import com.gt.gzclaimapp.gson.ClaimMonthRecord
import com.gt.gzclaimapp.manager.IoMainScheduler
import com.gt.gzclaimapp.manager.RetrofitManager
import com.gt.gzclaimapp.ui.adapter.ClaimOverviewAdapter
import com.gt.gzclaimapp.ui.base.BaseActivity
import com.gt.gzclaimapp.utils.ConstantsUtils
import com.gt.gzclaimapp.utils.ConstantsUtils.Companion.INTENT_CLAIM_TYPE
import com.gt.gzclaimapp.utils.ConstantsUtils.Companion.INTENT_OT_DATE
import com.gt.gzclaimapp.utils.PreferenceUtils
import kotlinx.android.synthetic.main.activity_single_recyclerview.*
import kotlinx.android.synthetic.main.common_title_bar.*

class ClaimsMonthlyActivity : BaseActivity() {

    private lateinit var claimsMonthlyAdapter: ClaimOverviewAdapter
    private var sessionId: String by PreferenceUtils(this, ConstantsUtils.SAVE_SESSION_ID, "")
    private var type: Int = 0

    companion object {
        val TYPE_EQP = 0
        val TYPE_ADM = 1
    }

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
        type = intent.getIntExtra(INTENT_CLAIM_TYPE, 0)

        leftBack.visibility = View.VISIBLE
        leftBack.setOnClickListener { finish() }

        tv_title.text = getTitleByType()
        rightOther.visibility = View.VISIBLE
        rightOther.setOnClickListener {
            startActivity(Intent(this, ClaimsDetailActivity::class.java)
                    .putExtra(INTENT_CLAIM_TYPE, type))
        }

        claimsMonthlyAdapter = ClaimOverviewAdapter()
        claimsMonthlyAdapter.setOnItemClickListener({ _, _, position ->
            startActivity(Intent(this, ClaimsDailyActivity::class.java)
                    .putExtra(INTENT_CLAIM_TYPE, type)
                    .putExtra(INTENT_OT_DATE, claimsMonthlyAdapter.data[position].date))
        })

        singleRecyclerView.layoutManager = LinearLayoutManager(this)
        singleRecyclerView.adapter = claimsMonthlyAdapter
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
        val disposable = RetrofitManager.service.claimMonthRecord(sessionId, type)
                .compose(IoMainScheduler())
                .arrayResult()
                .map({ list ->
                    mapData(list)
                })
                .subscribe({ claimOvcerviewData ->
                    hideLoading()

                    claimsMonthlyAdapter.setNewData(claimOvcerviewData)
                }, { _: Throwable? ->
                    hideLoading()
                })

        addSubscription(disposable)
    }

    private fun mapData(list: List<ClaimMonthRecord>): List<ClaimOverviewData>? {
        return list.map { ClaimOverviewData(it) }
    }

}