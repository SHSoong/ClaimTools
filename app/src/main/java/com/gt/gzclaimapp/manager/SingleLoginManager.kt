package com.gt.gzclaimapp.manager

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import com.gt.gzclaimapp.R
import com.gt.gzclaimapp.ui.MainActivity
import com.gt.gzclaimapp.ui.other.LoginActivity
import com.gt.gzclaimapp.utils.ConstantsUtils
import com.gt.gzclaimapp.utils.PreferenceUtils

class SingleLoginManager {

    companion object {
        fun navigateToLogin(context: Context) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle(context.getString(R.string.single_login_dialog_title))
                    .setMessage(context.getString(R.string.single_login_dialog_msg))
                    .setPositiveButton(context.getString(R.string.single_login_dialog_close), { _, _ ->
                        val pref = PreferenceUtils<Any>(context)
                        pref.delete(ConstantsUtils.SAVE_SESSION_ID)
                        val it = Intent(context, LoginActivity::class.java)
                        context.startActivity(it)
                        (context as Activity).finish()
                        ActivityManagers.finishActivity(MainActivity::class.java)
                    })
                    .setCancelable(false)
            builder.create()
                    .show()
        }
    }

}