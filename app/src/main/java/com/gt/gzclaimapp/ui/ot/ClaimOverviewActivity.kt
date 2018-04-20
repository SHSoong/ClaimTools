package com.gt.gzclaimapp.ui.ot

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.gt.gzclaimapp.R
import com.gt.gzclaimapp.databinding.ClaimOverviewData
import com.gt.gzclaimapp.gson.OTMonthlyRecord
import com.gt.gzclaimapp.manager.IoMainScheduler
import com.gt.gzclaimapp.manager.RetrofitManager
import com.gt.gzclaimapp.ui.adapter.ClaimOverviewAdapter
import com.gt.gzclaimapp.ui.base.BaseActivity
import com.gt.gzclaimapp.utils.ConstantsUtils
import com.gt.gzclaimapp.utils.ConstantsUtils.Companion.INTENT_OT_DATE
import com.gt.gzclaimapp.utils.PreferenceUtils
import kotlinx.android.synthetic.main.activity_single_recyclerview.*
import kotlinx.android.synthetic.main.common_title_bar.*

class ClaimOverviewActivity : BaseActivity() {

    private lateinit var claimOverviewAdapter: ClaimOverviewAdapter
    private var sessionId: String? by PreferenceUtils(this, ConstantsUtils.SAVE_SESSION_ID, "")

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
        claimOverviewAdapter = ClaimOverviewAdapter()
        claimOverviewAdapter.setOnItemClickListener({ _, _, position ->
            startActivity(Intent(this, OtClaimDailyActivity::class.java)
                    .putExtra(INTENT_OT_DATE, claimOverviewAdapter.data[position].date))
        })

        singleRecyclerView.layoutManager = LinearLayoutManager(this)
        singleRecyclerView.adapter = claimOverviewAdapter

        leftBack.visibility = View.VISIBLE
        leftBack.setOnClickListener({ finish() })
        tv_title.setText(R.string.overview_title)
    }

    private fun callAPI() {
        showLoading()
        val disposable = RetrofitManager.service.checkInOTRecord(sessionId ?: "")
                .compose(IoMainScheduler())
                .arrayResult()
                .map({ list ->
                    mapData(list)
                })
                .subscribe({ claimOverviewList ->
                    hideLoading()
                    claimOverviewAdapter.setNewData(claimOverviewList)
                }, { _: Throwable? ->
                    hideLoading()
                })
        addSubscription(disposable)
    }

    private fun mapData(list: List<OTMonthlyRecord>): List<ClaimOverviewData>? {
        return list.map { ClaimOverviewData(it) }
    }
}