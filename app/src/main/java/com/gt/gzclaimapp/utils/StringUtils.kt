package com.gt.gzclaimapp.utils

import android.content.Context
import android.text.InputFilter
import android.text.Spanned
import android.widget.EditText
import java.text.DecimalFormat


class StringUtils {

    companion object {
        //邮箱验证
        fun isEmail(strEmail: String): Boolean {
            val strPattern = "^([a-zA-Z0-9_.\\-])+@(([a-zA-Z0-9\\-])+\\.)+([a-zA-Z0-9]{2,4})+\$"
            return if (strPattern.isBlank()) {
                false
            } else {
                strEmail.matches(strPattern.toRegex())
            }
        }

        fun moneyDecimalFormat(moneyDouble: Double): String {
            val decimalFormat = DecimalFormat("###################.###########")
            return decimalFormat.format(moneyDouble)
        }

        fun lengthFilter(context: Context, editText: EditText, maxLen: Int, errMsg: String) {
            val filters = arrayOfNulls<InputFilter>(1)
            filters[0] = object : InputFilter.LengthFilter(maxLen) {

                override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned, dstart: Int, dend: Int): CharSequence {
                    //获取字符个数(一个中文算2个字符)
                    val destLen = StringUtils.getCharacterNum(dest.toString())
                    val sourceLen = StringUtils.getCharacterNum(source.toString())
                    if (destLen + sourceLen > maxLen) {
                        if (errMsg.isNotBlank())
                            ToastUtils.showToast(context, errMsg)
                        return ""
                    }
                    return source
                }
            }
            editText.filters = filters
        }

        //获取一段字符串的字符个数(包含中英文,一个中文算2个字符)
        fun getCharacterNum(content: String?): Int {
            return if (null == content || "" == content) {
                0
            } else {
                content.length + getChineseNum(content)
            }
        }

        //返回字符串里中文字或者全角字符的个数
        private fun getChineseNum(s: String): Int {
            val myChar = s.toCharArray()
            return myChar.indices.count { myChar[it].toByte().toChar() != myChar[it] }
        }

    }

}