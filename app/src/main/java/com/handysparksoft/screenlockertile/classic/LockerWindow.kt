package com.handysparksoft.screenlockertile.classic

import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import com.handysparksoft.screenlockertile.R

class LockerWindow(context: Context, private val onCloseWindow: () -> Unit) {

    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val rootView = layoutInflater.inflate(R.layout.locker_window_layout, null)

    private val windowFlags = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
    private val windowParams = WindowManager.LayoutParams(
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.MATCH_PARENT,                // Display it on top of other application windows
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,    // Don't let it grab the input focus
        windowFlags,                                            // Window Flags
        PixelFormat.TRANSLUCENT                                 // Make the underlying application window visible
    )

    init {
        initWindowParams()
        initWindow()
    }

    private fun initWindowParams() {
        windowParams.gravity = Gravity.CENTER
    }

    private fun initWindow() {
        rootView.findViewById<View>(R.id.unlockButton).setOnLongClickListener {
            close()
            true
        }
    }

    fun open() {
        try {
            windowManager.addView(rootView, windowParams)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun close() {
        try {
            windowManager.removeView(rootView)
            onCloseWindow.invoke()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
