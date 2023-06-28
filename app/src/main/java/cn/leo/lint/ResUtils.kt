package cn.leo.lint

import android.annotation.SuppressLint
import android.content.Context

@SuppressLint("StaticFieldLeak")
object ResUtils {

    var context: Context? = null

    fun getString(id: Int): String {
        return context?.resources?.getString(id) ?: ""
    }
}