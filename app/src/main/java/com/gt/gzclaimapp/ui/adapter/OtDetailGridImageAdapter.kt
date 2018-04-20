package com.gt.gzclaimapp.ui.adapter

import android.content.Context
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.gt.gzclaimapp.R
import com.gt.gzclaimapp.bean.UploadFileBean

class OtDetailGridImageAdapter(private val context: Context)
    : BaseQuickAdapter<UploadFileBean, BaseViewHolder>(R.layout.item_view_apply_gridimage) {

    companion object {
        val TYPE_ADD = 1
        val TYPE_SHOW = 2
        val selectMax = 3
    }

    override fun convert(helper: BaseViewHolder, item: UploadFileBean?) {
        val mIv = helper.getView<ImageView>(R.id.mImageView)
        val mDel = helper.getView<ImageView>(R.id.ivDelete)
        when (helper.itemViewType) {
            TYPE_ADD -> {
                mDel.visibility = View.GONE
            }
            TYPE_SHOW -> {
                Glide.with(context).load(item?.path).into(mIv)
                mDel.visibility = View.VISIBLE
                mDel.setOnClickListener {
                    this.data.remove(item)
                    notifyDataSetChanged()
                }
            }
        }

    }

    override fun getItemCount(): Int {
        return if (this.data.size < selectMax) {
            this.data.size + 1
        } else {
            this.data.size
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (isShowAddItem(position)) {
            TYPE_ADD
        } else {
            TYPE_SHOW
        }
    }

    private fun isShowAddItem(position: Int): Boolean {
        val size = if (this.data.size == 0) 0 else this.data.size
        return position == size
    }

}