package com.gt.gzclaimapp.ui.other

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import com.gt.gzclaimapp.R
import com.gt.gzclaimapp.ui.MainActivity
import com.gt.gzclaimapp.ui.base.BaseActivity
import com.gt.gzclaimapp.utils.ConstantsUtils
import com.gt.gzclaimapp.utils.PreferenceUtils


/**
 * app入口
 */
class SplashActivity : BaseActivity() {

    private var sessionId: String by PreferenceUtils(this, ConstantsUtils.SAVE_SESSION_ID, "")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //使用全屏
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_splash)
    }

    override fun onStart() {
        super.onStart()
        checkGlobalPermission()
    }

    override fun initPermission() {
        Handler().postDelayed({
            //退出全屏
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            if (sessionId.isBlank()) {
                navigateToLogin()
            } else {
                navigateToMain()
            }
        }, 1 * 1000)
    }

    private fun navigateToLogin() {
        val it = Intent(this, LoginActivity::class.java)
        startActivity(it)
        finish()
    }

    private fun navigateToMain() {
        val it = Intent(this, MainActivity::class.java)
        startActivity(it)
        finish()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        requestPermissionResult(requestCode, grantResults)
    }
}