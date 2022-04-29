package com.handysparksoft.screentouchlocker

import android.content.ComponentName
import android.content.ContextWrapper
import android.content.Intent
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

class ScreenTouchLockerTileService : TileService() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.action?.let { action ->
            when (action) {
                TileAction.ActionTileStart.name -> updateTileState(true)
                TileAction.ActionTileStop.name -> updateTileState(false)
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onTileAdded() {
        super.onTileAdded()
        updateTileState(false)
    }

    override fun onClick() {
        super.onClick()
        val activeState = qsTile.state == Tile.STATE_ACTIVE
        if (activeState) {
            // Turn off
            updateTileState(false)
            ScreenTouchLockerService.startTheService(context = this, action = ScreenTouchLockerAction.ActionUnlock)
        } else {
            // Turn on
            if (drawOverOtherAppsEnabled()) {
                updateTileState(true)
                ScreenTouchLockerService.startTheService(context = this, action = ScreenTouchLockerAction.ActionLock)
                ShakeDetectorService.startTheService(context = this)
            } else {
                updateTileState(false)
                startActivityAndCollapse(getOverlayPermissionIntent())
            }
        }
    }

    private fun updateTileState(activeState: Boolean) {
        if (qsTile != null) {
            qsTile?.state = if (activeState) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
            qsTile?.updateTile()
        } else {
            requestListeningState(this, ComponentName(this, ScreenTouchLockerTileService::class.java))
        }
    }

    companion object {

        fun startTileService(context: ContextWrapper) {
            Intent(context, ScreenTouchLockerTileService::class.java).also {
                it.action = TileAction.ActionTileStart.name
                context.startService(it)
                context.logdAndToast("Tile started")
            }
        }

        fun stopTileService(context: ContextWrapper) {
            Intent(context, ScreenTouchLockerTileService::class.java).also {
                it.action = TileAction.ActionTileStop.name
                context.startService(it)
                context.logdAndToast("Tile stopped")
            }
        }
    }
}

enum class TileAction { ActionTileStart, ActionTileStop }
