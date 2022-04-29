package com.handysparksoft.screentouchlocker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.handysparksoft.screentouchlocker.classic.LockerWindow

class ScreenTouchLockerService : Service() {

    private val lockerWindow by lazy {
        LockerWindow(
            context = this,
            onCloseWindow = {
                startTheService(context = this, action = ScreenTouchLockerAction.ActionUnlock)
            }
        )
    }

    override fun onCreate() {
        super.onCreate()

        startService()
        this.logdAndToast("Service created")
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        val action = intent.action

        /** Stop the service if we receive the Stop action.
         *  START_NOT_STICKY is important here, we don't want the service to be relaunched.
         */
        if (action == ScreenTouchLockerAction.ActionStop.name) {
            lockerWindow.close()
            stopService()
            this.logdAndToast("Action stop")
            return START_NOT_STICKY
        }

        if (action == ScreenTouchLockerAction.ActionLock.name) {
            if (!drawOverOtherAppsEnabled()) {
                requestOverlayPermission()
            } else {
                ScreenTouchLockerTileService.startTileService(this)
                lockerWindow.open()
                this.logdAndToast("Action Lock")
            }
        }

        if (action == ScreenTouchLockerAction.ActionUnlock.name) {
            ScreenTouchLockerTileService.stopTileService(this)
            lockerWindow.close()
            this.logdAndToast("Action Unlock")
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        ScreenTouchLockerTileService.stopTileService(this)
        ShakeDetectorService.stopShakeDetectorService(this)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun startService() {
        createNotificationChannelAndStartForeground()
    }

    private fun createNotificationChannelAndStartForeground() {
        val notificationChannelId = "screenTouchLockerForegroundService"
        val channelName = "Foreground service ScreenTouchLocker"
        val notificationChannel = NotificationChannel(
            notificationChannelId,
            channelName,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationChannel.lightColor = Color.RED

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as? NotificationManager
        notificationManager?.let {

            it.createNotificationChannel(notificationChannel)

            val notificationBuilder = NotificationCompat.Builder(this, notificationChannelId)
            val notificationIntent = Intent(this, MainActivity::class.java).apply {
                action = ScreenTouchLockerAction.ActionUnlock.toString()
            }
            val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
            val notification = notificationBuilder
                .setOngoing(true)
                .setContentTitle(getString(R.string.foreground_notification_content_title))
                .setContentText(getString(R.string.foreground_notification_content_text))
                .setSmallIcon(R.drawable.ic_screen_locker_icon)
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setContentIntent(pendingIntent)
                .setStyle(
                    NotificationCompat.BigTextStyle().bigText(getString(R.string.foreground_notification_content_text_big))
                )
                .addAction(getLockAction())
                .addAction(getUnlockAction())
                .addAction(getStopAction())
                .build()

            startForeground(1, notification)
        }
    }

    private fun getLockAction(): NotificationCompat.Action {
        val intent = Intent(this, ScreenTouchLockerService::class.java)
        intent.action = ScreenTouchLockerAction.ActionLock.toString()
        val pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        return NotificationCompat.Action(
            R.drawable.ic_screen_lock_portrait,
            getString(R.string.foreground_notification_lock_action),
            pendingIntent
        )
    }

    private fun getUnlockAction(): NotificationCompat.Action {
        val intent = Intent(this, ScreenTouchLockerService::class.java)
        intent.action = ScreenTouchLockerAction.ActionUnlock.toString()
        val pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        return NotificationCompat.Action(
            R.drawable.ic_screen_portrait,
            getString(R.string.foreground_notification_unlock_action),
            pendingIntent
        )
    }

    private fun getStopAction(): NotificationCompat.Action {
        val intent = Intent(this, ScreenTouchLockerService::class.java)
        intent.action = ScreenTouchLockerAction.ActionStop.toString()
        val pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        return NotificationCompat.Action(
            R.drawable.ic_stop,
            getString(R.string.foreground_notification_stop_action),
            pendingIntent
        )
    }

    /**
     * Remove the foreground notification and stop the service.
     */
    private fun stopService() {
        stopForeground(true)
        stopSelf()
        this.logdAndToast("Service stopped")
    }

    companion object {

        fun startTheService(context: Context, action: ScreenTouchLockerAction = ScreenTouchLockerAction.ActionStart) {
            val serviceIntent = Intent(context, ScreenTouchLockerService::class.java)
            serviceIntent.action = action.toString()
            context.startForegroundService(serviceIntent)
        }

        fun stopTheService(context: Context) {
            val serviceIntent = Intent(context, ScreenTouchLockerService::class.java)
            serviceIntent.action = ScreenTouchLockerAction.ActionStop.toString()
            context.startService(serviceIntent)
        }
    }
}

enum class ScreenTouchLockerAction { ActionStart, ActionStop, ActionLock, ActionUnlock }
