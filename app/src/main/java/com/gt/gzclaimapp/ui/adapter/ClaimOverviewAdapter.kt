package com.gt.gzclaimapp.ui.adapter

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.gt.gzclaimapp.BR
import com.gt.gzclaimapp.R
import com.gt.gzclaimapp.databinding.ClaimOverviewData

class ClaimOverviewAdapter : BaseQuickAdapter<ClaimOverviewData, ClaimOverviewAdapter.ClaimOverviewHolder>(R.layout.item_claim_overview_list) {

    override fun convert(helper: ClaimOverviewHolder, data: ClaimOverviewData) {
        val binding = helper.binding
        binding.setVariable(BR.data,data)
        binding.executePendingBindings()
    }

    override fun getItemView(layoutResId: Int, parent: ViewGroup): View {
        val binding = DataBindingUtil.inflate<ViewDataBinding>(mLayoutInflater, layoutResId, parent, false) ?: return super.getItemView(layoutResId, parent)
        val view = binding.root
        view.setTag(R.id.ClaimOverviewAdapter, binding)
        return view
    }

    inner class ClaimOverviewHolder(view: View) : BaseViewHolder(view) {
        val binding: ViewDataBinding
            get() = itemView.getTag(R.id.ClaimOverviewAdapter) as ViewDataBinding
    }
}