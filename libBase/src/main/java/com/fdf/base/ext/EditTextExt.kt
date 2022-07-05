package com.fdf.base.ext

import android.text.InputType
import android.widget.EditText

/**
 *    Created by Administrator on 2021/11/25.
 *    Desc :
 */
object EditTextExt {
    fun EditText.show(isChecked: Boolean) {
        inputType = if (isChecked) {
            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        } else {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        setSelection(string().length)
    }

    /**
     * 获取文本
     */
    fun EditText.string(): String {
        return this.text.toString()
    }
}