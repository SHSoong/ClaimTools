package com.gt.gzclaimapp.ui.claim

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import com.gt.gzclaimapp.R
import com.gt.gzclaimapp.gson.BaseGsonObject
import com.gt.gzclaimapp.gson.ClaimRecordDetail
import com.gt.gzclaimapp.manager.IoMainScheduler
import com.gt.gzclaimapp.manager.RetrofitManager
import com.gt.gzclaimapp.ui.base.BaseActivity
import com.gt.gzclaimapp.ui.claim.ClaimsMonthlyActivity.Companion.TYPE_ADM
import com.gt.gzclaimapp.ui.claim.ClaimsMonthlyActivity.Companion.TYPE_EQP
import com.gt.gzclaimapp.utils.*
import kotlinx.android.synthetic.main.activity_claimdetail.*
import kotlinx.android.synthetic.main.common_title_bar.*
import kotlinx.android.synthetic.main.popwin_select_adm.view.*
import kotlinx.android.synthetic.main.popwin_select_eqp.view.*

class ClaimsDetailActivity : BaseActivity() {

    private var sessionId: String by PreferenceUtils(this, ConstantsUtils.SAVE_SESSION_ID, "")

    private var type: Int = 0   //0 设备报销 | 1 行政报销
    private var claimType: Int = -1
    private var recordId: Int = -1
    private var remark = ""
    private var price = ""

    private val eqpTypeString = arrayOf(R.string.claim_type_eqp_0, R.string.claim_type_eqp_1, R.string.claim_type_eqp_2)
    private val admTypeString = arrayOf(R.string.claim_type_adm_0, R.string.claim_type_adm_1, R.string.claim_type_adm_2, R.string.claim_type_adm_3)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_claimdetail)
        init()
        if (recordId > 0) {
            callDefAPI()
        }
    }

    private fun init() {
        type = intent.getIntExtra(ConstantsUtils.INTENT_CLAIM_TYPE, -1)
        recordId = intent.getIntExtra(ConstantsUtils.INTENT_CLAIM_RECORD_ID, -1)

        tv_title.text = getTitleByType()
        leftBack.visibility = View.VISIBLE
        leftBack.setOnClickListener { finish() }

        tv_tips.visibility = View.GONE
        tvClaimSelectType.setOnClickListener {
            AosPhoneUtils.hideSoftInput(this)
            when (type) {
                TYPE_EQP ->
                    initEqpSelectClaimType()
                TYPE_ADM ->
                    initAdmSelectClaimType()
            }
        }
        btnSubmit.setOnClickListener { initSubmit() }

        etClaimPrice.addTextChangedListener(TextWatcherUtils(etClaimPrice, 999999))
        StringUtils.lengthFilter(this, etClaimRemark, 100, getString(R.string.claim_description))
    }

    private fun getTitleByType(): String {
        return when (type) {
            ClaimsMonthlyActivity.TYPE_EQP ->
                getString(R.string.claim_title_eqp)
            ClaimsMonthlyActivity.TYPE_ADM ->
                getString(R.string.claim_title_adm)
            else ->
                getString(R.string.app_name)
        }
    }

    private fun initSubmit() {
        if (claimType < 0) {
            ToastUtils.showToast(this@ClaimsDetailActivity, getString(R.string.claim_type_tips))
            return
        }
        price = etClaimPrice.text.toString()
        if (price.isBlank() || price.toDouble() <= 0) {
            ToastUtils.showToast(this@ClaimsDetailActivity, getString(R.string.claim_number_tips))
            return
        }

        remark = etClaimRemark.text.toString()
        callAPI()
    }

    private fun callAPI() {
        showLoading()
        btnSubmit.isEnabled = false
        val recordIdOrNull: Int? = if (recordId < 0) {
            null
        } else recordId

        val disposable = RetrofitManager.service.addClaimRecord(sessionId, recordIdOrNull, type, claimType, price.toDouble(), remark)
                .compose(IoMainScheduler())
                .objectResult()
                .map { obj -> obj }
                .subscribe({ _ ->
                    hideLoading()
                    btnSubmit.isEnabled = true
                    uploadOkDialog().show()
                }, { _: Throwable? ->
                    hideLoading()
                    btnSubmit.isEnabled = true
                    ToastUtils.showToast(this, getString(R.string.network_error))
                })
        addSubscription(disposable)
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

    private fun callDefAPI() {
        val disposable = RetrofitManager.service.getRecordDetail(sessionId, recordId)
                .compose(IoMainScheduler())
                .objectResult()
                .map { obj -> mapData(obj) }
                .subscribe({ detail ->
                    initData(detail)
                }, { _: Throwable? ->
                    ToastUtils.showToast(this, getString(R.string.network_error))
                })
        addSubscription(disposable)
    }

    private fun mapData(obj: BaseGsonObject<ClaimRecordDetail>): ClaimRecordDetail {
        return obj.detail
    }

    private fun initData(detail: ClaimRecordDetail) {
        setSelectTypeText(detail.claimType)
        etClaimPrice.setText(detail.totalPrice.toString())
        etClaimRemark.setText(detail.remark)
        if (!detail.editable) {
            tvClaimSelectType.isEnabled = false
            etClaimPrice.isEnabled = false
            etClaimRemark.isEnabled = false
            btnSubmit.isEnabled = false
            tv_tips.visibility = View.VISIBLE
        }
    }

    private var mPopupWindow: PopupWindow? = null
    private fun initEqpSelectClaimType() {
        if (mPopupWindow == null)
            mPopupWindow = PopupWindow(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                val inflate = View.inflate(this@ClaimsDetailActivity, R.layout.popwin_select_eqp, null)
                contentView = inflate

                contentView.tvClaimTypeEqp0.setOnClickListener {
                    setSelectTypeText(0)
                    dismiss()
                }
                contentView.tvClaimTypeEqp1.setOnClickListener {
                    setSelectTypeText(1)
                    dismiss()
                }
                contentView.tvClaimTypeEqp2.setOnClickListener {
                    setSelectTypeText(2)
                    dismiss()
                }
                contentView.tvClaimTypeEqpCancel.setOnClickListener {
                    dismiss()
                }
            }
        mPopupWindow?.animationStyle = R.style.popwin_anim
        mPopupWindow?.isOutsideTouchable = false
        mPopupWindow?.isFocusable = true
        mPopupWindow?.showAsDropDown(topLayout, Gravity.BOTTOM, 0, 0)
    }

    private fun initAdmSelectClaimType() {
        if (mPopupWindow == null)
            mPopupWindow = PopupWindow(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                val inflate = View.inflate(this@ClaimsDetailActivity, R.layout.popwin_select_adm, null)
                contentView = inflate

                contentView.tvClaimTypeAdm0.setOnClickListener {
                    setSelectTypeText(0)
                    dismiss()
                }
                contentView.tvClaimTypeAdm1.setOnClickListener {
                    setSelectTypeText(1)
                    dismiss()
                }
                contentView.tvClaimTypeAdm2.setOnClickListener {
                    setSelectTypeText(2)
                    dismiss()
                }
                contentView.tvClaimTypeAdm3.setOnClickListener {
                    setSelectTypeText(3)
                    dismiss()
                }
                contentView.tvClaimTypeAdmCancel.setOnClickListener {
                    dismiss()
                }
            }
        mPopupWindow?.animationStyle = R.style.popwin_anim
        mPopupWindow?.isOutsideTouchable = false
        mPopupWindow?.isFocusable = true
        mPopupWindow?.showAsDropDown(topLayout, Gravity.BOTTOM, 0, 0)
    }

    private fun setSelectTypeText(position: Int) {
        claimType = position

        tvClaimSelectType.text = when (type) {
            TYPE_ADM ->
                getString(admTypeString[position])
            else ->
                getString(eqpTypeString[position])
        }
    }

}