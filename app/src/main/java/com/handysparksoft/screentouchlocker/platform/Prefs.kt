package com.handysparksoft.screentouchlocker.platform

import android.content.Context
import android.content.SharedPreferences

class Prefs constructor(context: Context) {

    private val prefsFilename = "com.handysparksoft.screentouchlocker.platform.prefs"
    private val prefs: SharedPreferences = context.getSharedPreferences(prefsFilename, 0)

    var showLockedTouches: Boolean
        get() = prefs.getBoolean(KEY_SHOW_LOCKED_TOUCHES, true)
        set(value) = prefs.edit().putBoolean(KEY_SHOW_LOCKED_TOUCHES, value).apply()

    var vibrate: Boolean
        get() = prefs.getBoolean(KEY_VIBRATE, true)
        set(value) = prefs.edit().putBoolean(KEY_VIBRATE, value).apply()

    var enableShakeAndLock: Boolean
        get() = prefs.getBoolean(KEY_ENABLE_SHAKE_AND_LOCK, true)
        set(value) = prefs.edit().putBoolean(KEY_ENABLE_SHAKE_AND_LOCK, value).apply()

    var enableShakeAndUnlock: Boolean
        get() = prefs.getBoolean(KEY_ENABLE_SHAKE_AND_UNLOCK, false)
        set(value) = prefs.edit().putBoolean(KEY_ENABLE_SHAKE_AND_UNLOCK, value).apply()

    companion object {

        private const val KEY_SHOW_LOCKED_TOUCHES = "key_show_locked_touches"
        private const val KEY_VIBRATE = "key_vibrate"
        private const val KEY_ENABLE_SHAKE_AND_LOCK = "key_enable_shake_and_lock"
        private const val KEY_ENABLE_SHAKE_AND_UNLOCK = "key_enable_shake_and_unlock"
    }
}
