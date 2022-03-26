package com.handysparksoft.screenlockertile

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.widget.Toast

/**
 * Permissions functions
 */
fun Context.getOverlayPermissionIntent() = Intent(
    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
    Uri.parse("package:$packageName")
).apply {
    flags = Intent.FLAG_ACTIVITY_NEW_TASK
}

fun Context.drawOverOtherAppsEnabled(): Boolean {
    return Settings.canDrawOverlays(this)
}

fun Context.requestOverlayPermission() {
    startActivity(getOverlayPermissionIntent())
}

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
