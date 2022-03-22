package com.handysparksoft.screenlockertile

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Color
import android.os.IBinder
import androidx.core.app.NotificationCompat

class ScreenLockerService : Service() {

    private var manuallyStopped: Boolean = false

    override fun onCreate() {
        super.onCreate()

        startService()
        this.logdAndToast("Service created")
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        val action = intent.action

        // Stop the service if we receive the Stop action.
        // START_NOT_STICKY is important here, we don't want the service to be relaunched.
        if (action == ScreenLockerAction.ActionStop.name) {
            manuallyStopped = true
            stopService()
            this.logdAndToast("Action stop")
            return START_NOT_STICKY
        }

        if (action == ScreenLockerAction.ActionLock.name) {
            this.logdAndToast("Action Lock")
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        ScreenLockerTileService.stopTileService(this)
        if (!manuallyStopped) {
            forceRestartService()
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun forceRestartService() {
        //        val broadcastIntent = Intent()
        //        broadcastIntent.action = LocationRestartForegroundService.RESTART_SERVICE_ACTION
        //        broadcastIntent.setClass(this, LocationRestartForegroundService::class.java)
        //        this.sendBroadcast(broadcastIntent)
    }

    private fun startService() {
        createNotificationChannelAndStartForeground()
        ScreenLockerTileService.startTileService(this)
    }

    private fun createNotificationChannelAndStartForeground() {
        val notificationChannelId = "screenLockerForegroundService"
        val channelName = "Foreground service ScreenLocker"
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
            val notificationIntent = Intent(this, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
            val notification = notificationBuilder
                .setOngoing(true)
                .setContentTitle(getString(R.string.foreground_notification_content_title))
                .setContentText(getString(R.string.foreground_notification_content_text))
                .setSmallIcon(R.drawable.ic_screen_locker_tile)
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setContentIntent(pendingIntent)
                .setStyle(
                    NotificationCompat.BigTextStyle().bigText(getString(R.string.foreground_notification_content_text_big))
                )
                .addAction(getStopAction())
                .build()

            startForeground(1, notification)
        }
    }

    private fun getStopAction(): NotificationCompat.Action {
        val intent = Intent(this, ScreenLockerService::class.java)
        intent.action = ScreenLockerAction.ActionStop.toString() //ACTION_STOP
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

        //        private const val ACTION_STOP = "ACTION_STOP"

        fun startService(context: ContextWrapper, action: ScreenLockerAction = ScreenLockerAction.ActionStart) {
            val serviceIntent = Intent(context, ScreenLockerService::class.java)
            serviceIntent.action = action.toString()
            context.startForegroundService(serviceIntent)
        }

        fun stopService(context: ContextWrapper) {
            val serviceIntent = Intent(context, ScreenLockerService::class.java)

            // This action will stop the service manually (So no conflict with autorestart feature)
            serviceIntent.action = ScreenLockerAction.ActionStop.toString() //ACTION_STOP
            context.startForegroundService(serviceIntent)
        }
    }
}

enum class ScreenLockerAction { ActionStart, ActionStop, ActionLock }

//    override fun toString(): String = this::class.java.simpleName
