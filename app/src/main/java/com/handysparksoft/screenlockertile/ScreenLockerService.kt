package com.handysparksoft.screenlockertile;

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

class ScreenLockerService : Service() {

    private var manuallyStopped: Boolean = false
    private lateinit var screenLockerTileService: ScreenLockerTileService

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        startScreenLockerService()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        checkIntent(intent)
        return START_STICKY
    }

    private fun checkIntent(intent: Intent?) {
        intent?.action?.let { action ->
            when (action) {
                ACTION_STOP -> {
                    manuallyStopped = true
                    stopForeground(true)
                    stopSelf()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        if (!manuallyStopped) {
            forceRestartService()
        }
    }

    private fun forceRestartService() {
        //        val broadcastIntent = Intent()
        //        broadcastIntent.action = LocationRestartForegroundService.RESTART_SERVICE_ACTION
        //        broadcastIntent.setClass(this, LocationRestartForegroundService::class.java)
        //        this.sendBroadcast(broadcastIntent)
    }

    private fun startScreenLockerService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannelAndStartForeground()
        } else {
            startForeground(2, Notification())
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
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
            val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
            val notification = notificationBuilder
                .setOngoing(true)
                .setContentTitle(getString(R.string.foreground_notification_content_title))
                .setContentText(getString(R.string.foreground_notification_content_text))
                .setSmallIcon(R.drawable.ic_screen_locker_tile)
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setContentIntent(pendingIntent)
                //                .setStyle(
                //                    NotificationCompat.BigTextStyle()
                //                        .bigText(getString(R.string.foreground_notification_big_text))
                //                        .setSummaryText(getString(R.string.foreground_notification_summary))
                //                )
                .addAction(getStopAction())
                .build()

            startForeground(1, notification)
        }
    }

    private fun getStopAction(): NotificationCompat.Action? {
        val intent = Intent(this, ScreenLockerService::class.java)
        intent.action = ACTION_STOP
        val pendingIntent = PendingIntent.getService(this, 0, intent, 0)
        return NotificationCompat.Action(
            R.drawable.ic_stop,
            getString(R.string.foreground_notification_stop_action),
            pendingIntent
        )
    }

    companion object {

        const val ACTION_STOP = "ACTION_STOP"

        fun startService(context: ContextWrapper) {
            val serviceIntent = Intent(context, ScreenLockerService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
            context.logd("Service initialized")
            context.toast("Service initialized")
        }

        fun stopService(context: ContextWrapper) {
            val serviceIntent = Intent(context, ScreenLockerService::class.java)

            // This action will stop the service manually (So no conflict with autorestart feature)
            serviceIntent.action = ACTION_STOP
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
            context.logd("Service stopped")
            context.toast("Service stopped")
        }
    }
}

