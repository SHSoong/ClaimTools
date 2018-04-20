package com.gt.gzclaimapp.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class TextWatcherUtils(private val etText: EditText, private val maxInt: Int) : TextWatcher {

    private var deleteLastChar: Boolean = false// 是否需要删除末尾

    override fun afterTextChanged(s: Editable?) {
        if (s == null) {
            return
        }

        // 1.截取小数点后面的
        if (deleteLastChar) {
            etText.setText(s.toString().substring(0, s.toString().length - 1))
            etText.setSelection(etText.text.length)
        }

        // 2.截取小数点前面的
        if (!s.toString().contains(".")) {
            if (s.isNotBlank() && s.toString().toDouble() > maxInt) {
                etText.setText(s.toString().substring(0, s.toString().length - 1))
                etText.setSelection(etText.text.length)
            }
        } else {
            val bef = s.toString().substring(0, s.toString().indexOf("."))
            val aft = s.toString().substring(s.toString().indexOf("."), s.toString().lastIndex + 1)
            if (bef.isNotBlank() && bef.toDouble() > maxInt) {
                val newStr = bef.substring(0, bef.length - 1) + aft
                etText.setText(newStr)
                etText.setSelection(bef.length - 1)
            }
        }

        // 以小数点开头，前面自动加上 "0"
        if (s.toString().startsWith(".")) {
            val newStr = "0$s"
            etText.setText(newStr)
            etText.setSelection(etText.text.length)
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (s.toString().contains(".")) {
            // 如果点后面有超过三位数值,则删掉最后一位
            val length = s!!.length - s.toString().lastIndexOf(".")
            // 说明后面有三位数值
            deleteLastChar = length >= 4
        }
    }

}