package com.handysparksoft.screenlockertile

import android.content.ContextWrapper
import android.util.Log
import android.widget.Toast

/**
 * Log functions
 */
fun ContextWrapper.logd(message: String) {
    Log.d(this::class.simpleName, "*** $message")
}

fun ContextWrapper.toast(message: String) {
    Log.d(this::class.simpleName, message)
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun ContextWrapper.logdAndToast(message: String) {
    this.logd(message)
    this.toast(message)
}
