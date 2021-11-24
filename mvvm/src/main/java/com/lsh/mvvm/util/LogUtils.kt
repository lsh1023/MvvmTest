package com.lsh.mvvm.util

import android.util.Log
import java.lang.Exception

/**
 *
 * @Description:    常用的日志打印类
 * @CreateDate:     2021/11/23 20:13
 * @Version:        1.0
 */

const val TAG = "LogUtils"

private const val IS_SHOW_DEBUG: Boolean = true

fun showDebug(msg: String, tag: String = TAG) {
    if (IS_SHOW_DEBUG) {
        Log.d(tag, zipLogMsg(msg))
    }
}

fun showError(msg: String, error: Exception? = null, tag: String = TAG) {
    if (IS_SHOW_DEBUG) {
        error?.let {
            Log.e(tag, msg, error)
        } ?: Log.e(tag, zipLogMsg(msg))
    }
}

fun showInfo(msg: String, tag: String = TAG) {
    if (IS_SHOW_DEBUG) {
        Log.w(tag, zipLogMsg(msg))
    }
}


fun showThrowable(msg: String, throwable: Throwable? = null, tag: String = TAG) {
    if (IS_SHOW_DEBUG) {
        throwable?.let {
            Log.e(tag, msg, throwable)
        } ?: Log.e(tag, zipLogMsg(msg))
    }
}

private fun zipLogMsg(msg: String): String {
    val stackTrace = Thread.currentThread().stackTrace
    val buffer = StringBuffer()
    stackTrace.takeIf { it.size > 5 }?.let {
        val element = stackTrace[5]
        buffer.apply {
            append(getShortClassName(element.className))
            append(".")
            append(element.methodName)
            append("(")
            append(element.fileName)
            append(":")
            append(element.lineNumber)
            append(")")
            append(":")
        }
    }
    return with(buffer) {
        append(msg).toString()
    }
}


private fun getShortClassName(className: String): String {
    if (className.isEmpty()) {
        return ""
    }
    return className.substring(className.lastIndexOf(".") + 1)
}

