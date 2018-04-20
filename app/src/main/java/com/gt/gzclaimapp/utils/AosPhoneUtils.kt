package com.gt.gzclaimapp.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.telephony.TelephonyManager
import android.view.inputmethod.InputMethodManager


class AosPhoneUtils {
    companion object {

        // 隐藏软键盘
        fun hideSoftInput(context: Context) {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow((context as Activity).window.decorView.windowToken, 0)
        }

        //deviceId
        @Suppress("DEPRECATION")
        @SuppressLint("MissingPermission", "HardwareIds")
        fun getIMEI(mContext: Context): String {
            val telephonyMgr = mContext.applicationContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            return telephonyMgr.deviceId
        }
    }
}