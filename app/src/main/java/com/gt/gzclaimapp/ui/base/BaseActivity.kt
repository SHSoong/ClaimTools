package com.gt.gzclaimapp.ui.base

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import com.dyhdyh.widget.loading.dialog.LoadingDialog
import com.gt.gzclaimapp.R
import com.gt.gzclaimapp.exception.BaseException
import com.gt.gzclaimapp.gson.BaseGsonArray
import com.gt.gzclaimapp.gson.BaseGsonObject
import com.gt.gzclaimapp.manager.SingleLoginManager
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import permissions.dispatcher.*

@RuntimePermissions
@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {

    private var compositeDisposable = CompositeDisposable()

    override fun onDestroy() {
        super.onDestroy()
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.clear()
        }
    }

    fun addSubscription(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    @NeedsPermission(Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun globalPermission() {
        initPermission()
    }

    @OnShowRationale(Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun showRationaleForGlobalPermission(request: PermissionRequest) {
        AlertDialog.Builder(this)
                .setMessage(R.string.permission_global_tips)
                .setPositiveButton(R.string.dialog_confirm, { _, _ ->
                    request.proceed()
                })
                .setNegativeButton(R.string.dialog_cancel, { _, _ ->
                    request.cancel()
                })
                .setCancelable(false)
                .show()
    }

    @OnPermissionDenied(Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun showDeniedForGlobalPermission() {
        android.app.AlertDialog.Builder(this)
                .setMessage(R.string.permission_denied_global)
                .setPositiveButton(R.string.dialog_confirm, { _, _ -> finish() })
                .setCancelable(false)
                .show()
    }

    @OnNeverAskAgain(Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun showNeverAskForGlobalPermission() {
        AlertDialog.Builder(this)
                .setMessage(R.string.permission_never_ask_global)
                .setPositiveButton(R.string.dialog_confirm, { _, _ ->
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                })
                .setNegativeButton(R.string.dialog_cancel, { _, _ ->
                    finish()
                })
                .setCancelable(false)
                .show()
    }

    /**
     * 先这样写，库传方法有问题
     */
    @NeedsPermission(Manifest.permission.CAMERA)
    fun takeCamera() {
        takePic()
    }

    @OnShowRationale(Manifest.permission.CAMERA)
    fun showRationaleForCamera(request: PermissionRequest) {
        request.proceed()
    }

    @OnPermissionDenied(Manifest.permission.CAMERA)
    fun showDeniedForCameraPhone() {
    }

    @OnNeverAskAgain(Manifest.permission.CAMERA)
    fun showNeverAskForCamera() {
        AlertDialog.Builder(this)
                .setMessage(R.string.permission_never_ask_camera)
                .setPositiveButton(R.string.dialog_confirm, { _, _ ->
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                })
                .setNegativeButton(R.string.dialog_cancel, { _, _ -> })
                .setCancelable(false)
                .show()
    }

    fun checkTakeCamera() {
        takeCameraWithPermissionCheck()
    }

    fun checkGlobalPermission() {
        globalPermissionWithPermissionCheck()
    }

    fun requestPermissionResult(requestCode: Int, grantResults: IntArray) {
        onRequestPermissionsResult(requestCode, grantResults)
    }

    open fun takePic() {

    }

    open fun initPermission() {

    }

    fun showLoading() {
        //默认样式
        LoadingDialog.make(this)
                .setMessage("加载中...")//提示消息
                .setCancelable(true)
                .show()
    }

    fun hideLoading() {
        //取消Loading
        LoadingDialog.cancel()
    }

    //拓展方法
    open fun <T> Observable<BaseGsonArray<T>>.arrayResult(): Observable<List<T>> = this
            .flatMap { t ->
                handleArray(t)
            }
            .doOnError({ t ->
                t.printStackTrace()
            })

    open fun <T> Observable<BaseGsonObject<T>>.objectResult(): Observable<BaseGsonObject<T>> = this
            .flatMap { t ->
                handleObject(t)
            }
            .doOnError({ t ->
                t.printStackTrace()
            })

    private fun <T> handleArray(gsonArray: BaseGsonArray<T>): Observable<List<T>> {
        return when (gsonArray.status) {
            0 -> {
                Observable.just(gsonArray.detail)
            }
            999 -> {
                SingleLoginManager.navigateToLogin(this@BaseActivity)
                Observable.empty<List<T>>()
            }
            else -> {
                Observable.error<List<T>>(BaseException(gsonArray.status, gsonArray.message))
            }
        }
    }

    private fun <T> handleObject(gsonObject: BaseGsonObject<T>): Observable<BaseGsonObject<T>> {
        return when (gsonObject.status) {
            0 -> {
                Observable.just(gsonObject)
            }
            999 -> {
                SingleLoginManager.navigateToLogin(this@BaseActivity)
                Observable.empty<BaseGsonObject<T>>()
            }
            else -> {
                Observable.error<BaseGsonObject<T>>(BaseException(gsonObject.status, gsonObject.message))
            }
        }
    }
}