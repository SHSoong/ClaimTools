package com.gt.gzclaimapp.ui.clockout

import android.app.AlertDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import com.blankj.utilcode.util.TimeUtils
import com.gt.gzclaimapp.R
import com.gt.gzclaimapp.manager.IoMainScheduler
import com.gt.gzclaimapp.manager.RetrofitManager
import com.gt.gzclaimapp.ui.base.BaseActivity
import com.gt.gzclaimapp.utils.*
import kotlinx.android.synthetic.main.activity_clockout.*
import kotlinx.android.synthetic.main.common_title_bar.*
import java.text.SimpleDateFormat
import java.util.*


/**
 * 打卡
 */
class ClockOutActivity : BaseActivity() {

    private var sessionId by PreferenceUtils(this, ConstantsUtils.SAVE_SESSION_ID, "")
    private var projectName: String = ""
    private var projectTime: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clockout)
        init()
    }

    private fun init() {
        tv_title.text = getString(R.string.clock_out_title)
        leftBack.visibility = View.VISIBLE
        leftBack.setOnClickListener {
            finish()
        }

        //编辑框单行
        etProName.setSingleLine(true)

        btnSubmit.isEnabled = true
        btnSubmit.setOnClickListener {
            initSubmit()
        }

        etProTime.setOnClickListener {
            TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                val timeStr = TimeUtils.string2Millis("$hourOfDay:$minute", SimpleDateFormat("HH:mm", Locale.CHINESE))
                etProTime.text = TimeUtils.millis2String(timeStr, SimpleDateFormat("HH:mm", Locale.CHINESE))
            }, 0, 0, true).show()
        }

        StringUtils.lengthFilter(this, etProName, 20, "")
    }

    private fun initSubmit() {
        projectName = etProName.text.toString().trim()
        if (projectName.isBlank()) {
            ToastUtils.showToast(this, getString(R.string.clock_out_proName_empty))
            return
        }
        projectTime = etProTime.text.toString().trim()
        AosPhoneUtils.hideSoftInput(this)
        callAPI()
    }

    private fun callAPI() {
        showLoading()
        btnSubmit.isEnabled = false
        val disposable = RetrofitManager.service.checkInCheckIn(projectName, projectTime, sessionId)
                .compose(IoMainScheduler())
                .objectResult()
                .map { _ -> hideLoading() }
                .subscribe({ _ ->
                    hideLoading()
                    btnSubmit.isEnabled = true
                    tipsDialog().show()
                }, { _: Throwable? ->
                    hideLoading()
                    btnSubmit.isEnabled = true
                    ToastUtils.showToast(this, getString(R.string.network_error))
                })
        addSubscription(disposable)
    }

    private fun tipsDialog(): Dialog {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.clock_dialog_title))
                .setMessage(getString(R.string.clock_dialog_suc))
                .setPositiveButton(getString(R.string.clock_dialog_close), { _, _ ->
                    finish()
                })
                .setCancelable(false)
        return builder.create()
    }

}
