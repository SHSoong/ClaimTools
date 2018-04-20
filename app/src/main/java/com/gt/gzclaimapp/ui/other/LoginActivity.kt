package com.gt.gzclaimapp.ui.other

import android.content.Intent
import android.os.Bundle
import cn.jpush.android.api.JPushInterface
import com.gt.gzclaimapp.R
import com.gt.gzclaimapp.gson.BaseGsonObject
import com.gt.gzclaimapp.gson.UserInfo
import com.gt.gzclaimapp.manager.IoMainScheduler
import com.gt.gzclaimapp.manager.RetrofitManager
import com.gt.gzclaimapp.ui.MainActivity
import com.gt.gzclaimapp.ui.base.BaseActivity
import com.gt.gzclaimapp.utils.*
import kotlinx.android.synthetic.main.activity_login.*

/**
 * 登录
 */
class LoginActivity : BaseActivity() {

    private var email by PreferenceUtils(this, ConstantsUtils.SAVE_EMAIL, "")
    private var sessionId by PreferenceUtils(this, ConstantsUtils.SAVE_SESSION_ID, "")
    private var realName by PreferenceUtils(this, ConstantsUtils.SAVE_REALNAME, "")
    private var position by PreferenceUtils(this, ConstantsUtils.SAVE_POSITION, "")
    private var role by PreferenceUtils(this, ConstantsUtils.SAVE_ROLE, 0)

    private var deviceId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        init()
    }

    private fun init() {
        if (email.isNotBlank()) etEmail.setText(email)
    }

    override fun onStart() {
        super.onStart()
        checkGlobalPermission()
    }

    override fun initPermission() {
        deviceId = AosPhoneUtils.getIMEI(this)
        btnLogin.setOnClickListener { initLogin() }
    }

    private fun initLogin() {
        email = etEmail.text.toString()
        if (email.isBlank()) {
            ToastUtils.showToast(this, getString(R.string.login_email_empty))
            return
        }
        if (!StringUtils.isEmail(email)) {
            ToastUtils.showToast(this, getString(R.string.login_email_error))
            return
        }
        AosPhoneUtils.hideSoftInput(this)
        callAPI()
    }

    private fun navigateToMain() {
        val it = Intent(this, MainActivity::class.java)
        startActivity(it)
        finish()
    }

    private fun callAPI() {
        showLoading()
        val disposable = RetrofitManager.service.userLogin(email, deviceId, 1, JPushInterface.getRegistrationID(application))
                .compose(IoMainScheduler())
                .objectResult()
                .map { obj ->
                    mapData(obj)
                }
                .subscribe({ detail ->
                    hideLoading()

                    sessionId = detail.sessionId
                    realName = detail.realName
                    position = detail.position
                    role = detail.role
                    navigateToMain()

                }, { _: Throwable? ->
                    hideLoading()
                })
        addSubscription(disposable)
    }

    private fun mapData(obj: BaseGsonObject<UserInfo>): UserInfo {
        return obj.detail
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        requestPermissionResult(requestCode, grantResults)
    }
}
