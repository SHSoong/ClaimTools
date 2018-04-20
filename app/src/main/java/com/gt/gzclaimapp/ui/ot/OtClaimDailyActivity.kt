package com.gt.gzclaimapp.ui.ot

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.gt.gzclaimapp.R
import com.gt.gzclaimapp.databinding.OtClaimDailyData
import com.gt.gzclaimapp.gson.OTDailyRecord
import com.gt.gzclaimapp.manager.IoMainScheduler
import com.gt.gzclaimapp.manager.RetrofitManager
import com.gt.gzclaimapp.ui.adapter.OtClaimDailyAdapter
import com.gt.gzclaimapp.ui.base.BaseActivity
import com.gt.gzclaimapp.utils.ConstantsUtils
import com.gt.gzclaimapp.utils.ConstantsUtils.Companion.INTENT_CHECK_IN_ID
import com.gt.gzclaimapp.utils.PreferenceUtils
import kotlinx.android.synthetic.main.activity_single_recyclerview.*
import kotlinx.android.synthetic.main.common_title_bar.*

class OtClaimDailyActivity : BaseActivity() {

    private lateinit var otClaimDailyAdapter: OtClaimDailyAdapter

    private var sessionId: String by PreferenceUtils(this, ConstantsUtils.SAVE_SESSION_ID, "")

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
        leftBack.visibility = View.VISIBLE
        leftBack.setOnClickListener({ finish() })
        tv_title.setText(R.string.overview_title)

        otClaimDailyAdapter = OtClaimDailyAdapter()
        otClaimDailyAdapter.setOnItemClickListener({ _, _, position ->
            if (otClaimDailyAdapter.data[position].editEnable == 1) {
                startActivity(Intent(this, OtClaimDetailActivity::class.java)
                        .putExtra(INTENT_CHECK_IN_ID, otClaimDailyAdapter.data[position].monthOTRecord.checkInId))
            }
        })

        singleRecyclerView.layoutManager = LinearLayoutManager(this)
        singleRecyclerView.adapter = otClaimDailyAdapter

    }

    private fun callAPI() {
        showLoading()
        val disposable = RetrofitManager.service.checkInOTRecordList(sessionId, intent.getLongExtra(ConstantsUtils.INTENT_OT_DATE, 0))
                .compose(IoMainScheduler())
                .arrayResult()
                .map({ list ->
                    mapData(list)
                })
                .subscribe({ claimMonthlyData ->
                    hideLoading()
                    otClaimDailyAdapter.setNewData(claimMonthlyData)
                }, { error ->
                    hideLoading()
                    error.printStackTrace()
                })
        addSubscription(disposable)
    }

    private fun mapData(list: List<OTDailyRecord>): List<OtClaimDailyData>? {
        return list.map { OtClaimDailyData(it, this) }
    }
}