package com.gt.gzclaimapp.ui.ot

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import com.blankj.utilcode.util.TimeUtils
import com.gt.gzclaimapp.R
import com.gt.gzclaimapp.bean.GetOtInfoBean
import com.gt.gzclaimapp.bean.UploadFileBean
import com.gt.gzclaimapp.gson.BaseGsonObject
import com.gt.gzclaimapp.gson.OTDetailInfo
import com.gt.gzclaimapp.manager.IoMainScheduler
import com.gt.gzclaimapp.manager.RetrofitManager
import com.gt.gzclaimapp.ui.adapter.OtDetailGridImageAdapter
import com.gt.gzclaimapp.ui.base.BaseActivity
import com.gt.gzclaimapp.utils.*
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.tools.PictureFileUtils
import kotlinx.android.synthetic.main.activity_apply.*
import kotlinx.android.synthetic.main.common_title_bar.*
import kotlinx.android.synthetic.main.popwin_takephoto.view.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created on 2018-03-26.
 */
class OtClaimDetailActivity : BaseActivity() {

    private var sessionId by PreferenceUtils(this, ConstantsUtils.SAVE_SESSION_ID, "")
    private var checkInId = -1
    private var trafficSubsidies: Double = 0.0
    private lateinit var mApplyGridImageAdapter: OtDetailGridImageAdapter
    private val typeString = arrayOf(R.string.apply_type_1, R.string.apply_type_2, R.string.apply_type_3, R.string.apply_type_4)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apply)
        init()
        initAdapter()
        callDefAPI()
    }

    private fun init() {
        checkInId = intent.getIntExtra(ConstantsUtils.INTENT_CHECK_IN_ID, -1)

        tv_title.text = getString(R.string.apply_title)
        leftBack.visibility = View.VISIBLE
        leftBack.setOnClickListener { finish() }

        btnSubmit.isEnabled = true
        btnSubmit.setOnClickListener { initSubmit() }

        etPrice.addTextChangedListener(TextWatcherUtils(etPrice, 9999))
    }

    private fun initSubmit() {
        val price = etPrice.text.toString()
        if (price.isBlank()) {
            tipsDialog().show()
            return
        }
        trafficSubsidies = price.toDouble()
        if (trafficSubsidies <= 0) {
            ToastUtils.showToast(this@OtClaimDetailActivity, "请输入有效金额！")
            return
        }
        if (mApplyGridImageAdapter.data.size <= 0) {
            tipsDialog().show()
            return
        }

        callAPI()
    }

    private fun callDefAPI() {
        val disposable = RetrofitManager.service.checkInGetOtInfo(sessionId, checkInId)
                .compose(IoMainScheduler())
                .objectResult()
                .map { obj -> mapData(obj) }
                .subscribe({ detail ->
                    setDefInfo(detail)
                    showScrollView(true)
                }, { _: Throwable? -> })
        addSubscription(disposable)
    }

    private fun setDefInfo(bean: GetOtInfoBean) {
        tvProName.text = bean.projectName
        tvTime.text = TimeUtils.millis2String(bean.checkInDate, SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.CHINESE))
        tvFoodSubsidies.text = getString(R.string.apply_food_tips, bean.foodSubsidies.toString())
        tvCheckInType.text = getString(typeString[bean.checkInType - 1])
        showLlCheckInTypeDes(bean.checkInType)
        if (bean.trafficSubsidies > 0)
            etPrice.setText(bean.trafficSubsidies.toString())

        mApplyGridImageAdapter.addData(bean.images!!)
    }

    private fun mapData(obj: BaseGsonObject<OTDetailInfo>): GetOtInfoBean {
        return GetOtInfoBean(obj.detail)
    }

    private fun callAPI() {
        showLoading()
        btnSubmit.isEnabled = false
        val parts = filesToMultipartBodyParts(getFiles())//新增上传
        var imageIds = ""//已经上传的图片id，后台需要','拼接
        (0 until mApplyGridImageAdapter.data.size)
                .filterNot { mApplyGridImageAdapter.data[it].local }
                .forEach {
                    imageIds += if (it == mApplyGridImageAdapter.data.size - 1) {
                        "${mApplyGridImageAdapter.data[it].id}"
                    } else {
                        "${mApplyGridImageAdapter.data[it].id},"
                    }
                }

        val disposable = RetrofitManager.service.checkInAddOTInfo(sessionId, checkInId, trafficSubsidies, imageIds, parts)
                .compose(IoMainScheduler())
                .objectResult()
                .map { it }
                .subscribe({ _ ->
                    hideLoading()
                    btnSubmit.isEnabled = true
                    //包括裁剪和压缩后的缓存，要在上传成功后调用，注意：需要系统 sd 卡权限
                    PictureFileUtils.deleteCacheDirFile(this@OtClaimDetailActivity)
                    uploadOkDialog().show()
                }, { _ ->
                    hideLoading()
                    btnSubmit.isEnabled = true
                    ToastUtils.showToast(this, getString(R.string.network_error))
                })
        addSubscription(disposable)
    }

    private fun tipsDialog(): Dialog {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.apply_dialog_title))
                .setMessage(getString(R.string.apply_dialog_empty))
                .setPositiveButton(getString(R.string.apply_dialog_close), { _, _ ->

                })
                .setCancelable(false)
        return builder.create()
    }

    private fun uploadOkDialog(): Dialog {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.apply_dialog_title))
                .setMessage(getString(R.string.apply_dialog_suc))
                .setPositiveButton(getString(R.string.apply_dialog_close), { _, _ ->
                    finish()
                })
                .setCancelable(false)
        return builder.create()
    }

    private fun filesToMultipartBodyParts(files: List<File>): List<MultipartBody.Part> {
        val parts = ArrayList<MultipartBody.Part>(files.size)
        for (file in files) {
            // TODO:  这里为了简单起见，没有判断file的其它类型
            val requestBody = RequestBody.create(MediaType.parse("image/png"), file)
            val part = MultipartBody.Part.createFormData("multipartFiles", file.name, requestBody)
            parts.add(part)
        }
        return parts
    }

    private fun getFiles(): List<File> {
        val fileList = ArrayList<File>()
        (0 until mApplyGridImageAdapter.data.size)
                .filter { mApplyGridImageAdapter.data[it].local }
                .mapTo(fileList) { File(mApplyGridImageAdapter.data[it].path) }
        return fileList
    }

    private fun initAdapter() {
        mRecyclerView.isNestedScrollingEnabled = false
        // 设置布局管理器
        mRecyclerView.layoutManager = GridLayoutManager(this@OtClaimDetailActivity, 3)
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        mRecyclerView.setHasFixedSize(true)
        // 实例化adapter
        mApplyGridImageAdapter = OtDetailGridImageAdapter(this@OtClaimDetailActivity)
        val selectList: List<UploadFileBean> = ArrayList()
        mApplyGridImageAdapter.setNewData(selectList)
        // 设置adapter
        mRecyclerView.adapter = mApplyGridImageAdapter
        mApplyGridImageAdapter.setOnItemClickListener { adapter, _, position ->
            if (adapter.getItemViewType(position) == OtDetailGridImageAdapter.TYPE_ADD) {
                AosPhoneUtils.hideSoftInput(this)
                showPopWin()
            }
            if (adapter.getItemViewType(position) == OtDetailGridImageAdapter.TYPE_SHOW) {
                AosPhoneUtils.hideSoftInput(this)
                showPic(position)
            }
        }
    }

    private var mPopupWindow: PopupWindow? = null
    private fun showPopWin() {
        if (mPopupWindow == null)
            mPopupWindow = PopupWindow(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                val inflate = View.inflate(this@OtClaimDetailActivity, R.layout.popwin_takephoto, null)
                contentView = inflate

                contentView.tvCancel.setOnClickListener {
                    dismiss()
                }
                contentView.tvPickPhoto.setOnClickListener {
                    selectPic()
                    dismiss()
                }
                contentView.tvTakePhoto.setOnClickListener {
                    checkTakeCamera()
                    dismiss()
                }
            }
        mPopupWindow?.animationStyle = R.style.popwin_anim
        mPopupWindow?.isOutsideTouchable = false
        mPopupWindow?.isFocusable = true
        mPopupWindow?.showAsDropDown(topLayout, Gravity.BOTTOM, 0, 0)
    }

    private fun selectPic() {
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .maxSelectNum(OtDetailGridImageAdapter.selectMax - mApplyGridImageAdapter.data.size)// 最大图片选择数量 int
                .compress(true)// 是否压缩 true or false
                .forResult(PictureConfig.CHOOSE_REQUEST)//结果回调onActivityResult code
    }

    override fun takePic() {
        PictureSelector.create(this)
                .openCamera(PictureMimeType.ofImage())
                .compress(true)// 是否压缩 true or false
                .forResult(PictureConfig.CHOOSE_REQUEST)
    }

    private fun showPic(position: Int) {
        val medias = ArrayList<LocalMedia>()
        for (i in 0 until mApplyGridImageAdapter.data.size) {
            val mLocalMedia = LocalMedia()
            mLocalMedia.path = mApplyGridImageAdapter.data[i].path
            medias.add(mLocalMedia)
        }
        PictureSelector.create(this)
                .externalPicturePreview(position, medias)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mPopupWindow != null) {
                mPopupWindow?.dismiss()
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    @Suppress("DEPRECATED_IDENTITY_EQUALS")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode === RESULT_OK) {
            when (requestCode) {
                PictureConfig.CHOOSE_REQUEST -> {
                    // 图片选择结果回调
                    val selectList = PictureSelector.obtainMultipleResult(data)
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                    // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
                    val list = ArrayList<UploadFileBean>()
                    for (media in selectList) {
                        val b = UploadFileBean()
                        if (media.isCompressed) {
                            b.path = media.compressPath
                        } else {
                            b.path = media.path
                        }
                        b.local = true
                        Log.i("imagePath >>>>", media.path)
                        list.add(b)
                    }
                    mApplyGridImageAdapter.addData(list)
                    mApplyGridImageAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    private fun showScrollView(show: Boolean) {
        mScrollView.visibility = if (show) View.VISIBLE else View.GONE
        rlEmpty.visibility = if (!show) View.VISIBLE else View.GONE
    }

    private fun showLlCheckInTypeDes(checkInType: Int) {
        llCheckInTypeDes.visibility = if (checkInType > 2) View.VISIBLE else View.GONE
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        requestPermissionResult(requestCode, grantResults)
    }
}
