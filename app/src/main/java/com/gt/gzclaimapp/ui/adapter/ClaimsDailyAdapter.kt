package com.gt.gzclaimapp.ui.adapter

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.gt.gzclaimapp.BR
import com.gt.gzclaimapp.R
import com.gt.gzclaimapp.databinding.ClaimsDailyData

class ClaimsDailyAdapter : BaseQuickAdapter<ClaimsDailyData, ClaimsDailyAdapter.ClaimsDailyHolder>(R.layout.item_claims_daily_list) {

    override fun convert(helper: ClaimsDailyHolder, data: ClaimsDailyData) {
        val binding = helper.binding
        binding.setVariable(BR.data, data)
        binding.executePendingBindings()
    }

    override fun getItemView(layoutResId: Int, parent: ViewGroup): View {
        val binding = DataBindingUtil.inflate<ViewDataBinding>(mLayoutInflater, layoutResId, parent, false)
                ?: return super.getItemView(layoutResId, parent)
        val view = binding.root
        view.setTag(R.id.ClaimsDailyAdapter, binding)
        return view
    }

    inner class ClaimsDailyHolder(view: View) : BaseViewHolder(view) {
        val binding: ViewDataBinding
            get() = itemView.getTag(R.id.ClaimsDailyAdapter) as ViewDataBinding
    }

}