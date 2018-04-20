package com.gt.gzclaimapp

import android.app.Application
import android.app.Notification
import cn.jpush.android.api.BasicPushNotificationBuilder
import cn.jpush.android.api.JPushInterface
import com.hss01248.notifyutil.NotifyUtil


class GZClaimApplication : Application() {

    @Suppress("DEPRECATION")
    override fun onCreate() {
        super.onCreate()

        NotifyUtil.init(this)

        JPushInterface.setDebugMode(true)
        JPushInterface.init(this)

        val builder = BasicPushNotificationBuilder(this@GZClaimApplication)
        builder.statusBarDrawable = R.mipmap.ic_launcher
        //设置为自动消失和呼吸灯闪烁
        builder.notificationFlags = Notification.FLAG_AUTO_CANCEL or Notification.FLAG_SHOW_LIGHTS
        // 设置为铃声、震动、呼吸灯闪烁都要
        builder.notificationDefaults = (Notification.DEFAULT_SOUND or Notification.DEFAULT_VIBRATE or Notification.DEFAULT_LIGHTS)
        JPushInterface.setPushNotificationBuilder(1, builder)
    }

}