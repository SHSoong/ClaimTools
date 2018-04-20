package com.gt.gzclaimapp.ui

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.View
import cn.jpush.android.api.JPushInterface
import com.gt.gzclaimapp.R
import com.gt.gzclaimapp.databinding.ActivityMainBinding
import com.gt.gzclaimapp.databinding.UserInfoData
import com.gt.gzclaimapp.gson.BaseGsonObject
import com.gt.gzclaimapp.gson.OtEditInfo
import com.gt.gzclaimapp.manager.ActivityManagers
import com.gt.gzclaimapp.manager.IoMainScheduler
import com.gt.gzclaimapp.manager.RetrofitManager
import com.gt.gzclaimapp.manager.SingleLoginManager
import com.gt.gzclaimapp.ui.base.BaseActivity
import com.gt.gzclaimapp.ui.claim.ClaimsMonthlyActivity
import com.gt.gzclaimapp.ui.clockout.ClockOutActivity
import com.gt.gzclaimapp.ui.hr.TodoListActivity
import com.gt.gzclaimapp.ui.ot.ClaimOverviewActivity
import com.gt.gzclaimapp.ui.ot.OtClaimDetailActivity
import com.gt.gzclaimapp.utils.AosPhoneUtils
import com.gt.gzclaimapp.utils.ConstantsUtils
import com.gt.gzclaimapp.utils.ConstantsUtils.Companion.INTENT_CHECK_IN_ID
import com.gt.gzclaimapp.utils.ConstantsUtils.Companion.INTENT_CLAIM_TYPE
import com.gt.gzclaimapp.utils.PreferenceUtils
import kotlinx.android.synthetic.main.activity_main.*

/**
 * 主页
 */
class MainActivity : BaseActivity(), View.OnClickListener {

    private var sessionId by PreferenceUtils(this, ConstantsUtils.SAVE_SESSION_ID, "")
    private var realName by PreferenceUtils(this, ConstantsUtils.SAVE_REALNAME, "")
    private var position by PreferenceUtils(this, ConstantsUtils.SAVE_POSITION, "")
    private var role by PreferenceUtils(this, ConstantsUtils.SAVE_ROLE, 0)

    private lateinit var binding: ActivityMainBinding
    private var userInfoData = UserInfoData()
    private var checkInId = -1

    override fun onClick(v: View?) {
        when (v) {
            rlClockOut ->
                navigateToClockOut()
            tvBacklog ->
                navigateToApply()
            rightList ->
                navigateToTodoList()
            rlOtClaim ->
                navigateToClaimOverview()
            rlEqpClaim ->
                navigateToClaimsMonthly(ClaimsMonthlyActivity.TYPE_EQP)
            rlAdmClaim ->
                navigateToClaimsMonthly(ClaimsMonthlyActivity.TYPE_ADM)
            btnLoginOut ->
                SingleLoginManager.navigateToLogin(this@MainActivity)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityManagers.addActivity(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        init()
    }

    override fun onStart() {
        super.onStart()
        checkGlobalPermission()
    }

    override fun onResume() {
        super.onResume()
        callAPI()
    }

    private fun init() {
        userInfoData.role = role
        userInfoData.position = position
        userInfoData.realName = realName
        binding.data = userInfoData

        tvBacklog.setOnClickListener(this)
        rlOtClaim.setOnClickListener(this)
        rlClockOut.setOnClickListener(this)
        rlEqpClaim.setOnClickListener(this)
        rlAdmClaim.setOnClickListener(this)
        btnLoginOut.visibility = View.VISIBLE   //注销登录，调试用
        btnLoginOut.setOnClickListener(this)

        rightList.visibility = if (userInfoData.role == 1) View.VISIBLE else View.GONE
        rightList.setOnClickListener(this)
    }

    private fun callAPI() {
        val infoDisposable = RetrofitManager.service.checkInEditOtInfo(sessionId)
                .compose(IoMainScheduler())
                .objectResult()
                .map { mapData(it) }
                .subscribe({

                }, { it!!.printStackTrace() })

        //由于现在要切换网络才能获取，暂时不放去Receiver，否则逻辑走不通
        val updateDisposable = RetrofitManager.service.pushUpdateToken(sessionId, AosPhoneUtils.getIMEI(this@MainActivity),
                JPushInterface.getRegistrationID(application))
                .compose(IoMainScheduler())
                .subscribe({ _ ->
                }, { error: Throwable? ->
                    error!!.printStackTrace()
                })

        addSubscription(infoDisposable)
        addSubscription(updateDisposable)
    }

    private fun mapData(obj: BaseGsonObject<OtEditInfo>) {
        hideTips()
        if (obj.detail.checkInId != null) {
            checkInId = obj.detail.checkInId
            showTips()
        }
    }

    private fun navigateToClockOut() {
        startActivity(Intent(this, ClockOutActivity::class.java))
    }

    private fun navigateToApply() {
        startActivity(Intent(this, OtClaimDetailActivity::class.java).putExtra(INTENT_CHECK_IN_ID, this.checkInId))
    }

    private fun navigateToClaimOverview() {
        startActivity(Intent(this, ClaimOverviewActivity::class.java))
    }

    private fun navigateToClaimsMonthly(type: Int) {
        startActivity(Intent(this, ClaimsMonthlyActivity::class.java).putExtra(INTENT_CLAIM_TYPE, type))
    }

    private fun navigateToTodoList() {
        startActivity(Intent(this, TodoListActivity::class.java))
    }

    private fun showTips() {
        tvBacklog.visibility = View.VISIBLE
    }

    private fun hideTips() {
        tvBacklog.visibility = View.GONE
    }

    //未清理后台任务时直接打开
    override fun onBackPressed() {
        //不调用父类的方法
        val it = Intent()
                .setAction(Intent.ACTION_MAIN)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addCategory(Intent.CATEGORY_HOME)
        startActivity(it)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        requestPermissionResult(requestCode, grantResults)
    }

}
