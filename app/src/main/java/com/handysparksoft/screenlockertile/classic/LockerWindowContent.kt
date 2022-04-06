package com.handysparksoft.screenlockertile.classic

import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.util.AttributeSet
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import com.handysparksoft.screenlockertile.R
import com.handysparksoft.screenlockertile.logdAndToast

class LockerWindowContent @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    init {
        View.inflate(context, R.layout.locker_window_content_layout, this)
        bindUI()
    }

    private fun bindUI() {
        val screenContent = rootView.findViewById<View>(R.id.lockScreenContent)

        setUnlockButton()
        setLockedTouchFeedback(screenContent)
    }

    private fun setUnlockButton() {
        val unlockButton = rootView.findViewById<View>(R.id.unlockButton)
        unlockButton.animate().scaleXBy(SCALE_FACTOR).scaleYBy(SCALE_FACTOR).setDuration(SCALE_DURATION).withEndAction {
            unlockButton.animate().scaleXBy(-SCALE_FACTOR).scaleYBy(-SCALE_FACTOR).setDuration(SCALE_DURATION).start()
        }.start()
        unlockButton.animate().alpha(0.4f).setDuration(FADE_DURATION).start()
    }

    private fun setLockedTouchFeedback(screenContent: View) {
        val touchLockedImage = rootView.findViewById<View>(R.id.touchLockedImage)
        screenContent.setOnTouchListener { _, motionEvent ->
            locateTouch(touchLockedImage, motionEvent)

            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    performHapticFeedback(screenContent)
                    animateTouch(touchLockedImage, motionEvent)
                    (context as ContextWrapper).logdAndToast("Touch!")
                }

                MotionEvent.ACTION_UP -> {
                    hideTouch(touchLockedImage)
                }
            }
            screenContent.performClick()
            true
        }
    }

    private fun performHapticFeedback(screenContent: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            screenContent.performHapticFeedback(HapticFeedbackConstants.REJECT)
        } else {
            screenContent.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
        }
    }

    private fun locateTouch(touchLockedImage: View, motionEvent: MotionEvent) {
        touchLockedImage.x = motionEvent.x - (touchLockedImage.width / 2)
        touchLockedImage.y = motionEvent.y - (touchLockedImage.height / 2)
    }

    private fun animateTouch(touchLockedImage: View, motionEvent: MotionEvent) {
        touchLockedImage.visibility = View.VISIBLE
        touchLockedImage.animate().scaleX(3f).scaleY(3f)
    }

    private fun hideTouch(touchLockedImage: View) {
        touchLockedImage.animate().scaleX(1f).scaleY(1f).setDuration(50).withEndAction {
            touchLockedImage.visibility = View.INVISIBLE
        }
    }

    companion object {

        private const val SCALE_FACTOR = 2f
        private const val SCALE_DURATION = 1000L
        private const val FADE_DURATION = 5000L
    }
}
