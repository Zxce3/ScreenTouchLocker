package com.handysparksoft.screenlockertile

import android.content.ComponentName
import android.content.ContextWrapper
import android.content.Intent
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

class ScreenLockerTileService : TileService() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        checkIntent(intent)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onTileAdded() {
        super.onTileAdded()
        updateTileState(false)
    }

    override fun onStartListening() {
        super.onStartListening()
        val a = "dd"
    }

    override fun onClick() {
        super.onClick()
        val activeState = qsTile.state == Tile.STATE_ACTIVE
        if (activeState) {
            // Turn off
            ScreenLockerService.startService(context = this, action = ScreenLockerAction.ActionUnlock)
        } else {
            // Turn on
            if (drawOverOtherAppsEnabled()) {
                updateTileState(true)
                ScreenLockerService.startService(context = this, action = ScreenLockerAction.ActionLock)
            } else {
                startActivityAndCollapse(getOverlayPermissionIntent())
                updateTileState(false)
                //showDialog(PermissionRequiredDialog)
            }
        }
    }

    private fun checkIntent(intent: Intent?) {
        intent?.action?.let { action ->
            when (action) {
                TileAction.ActionTileStart.name -> updateTileState(true)
                TileAction.ActionTileStop.name -> updateTileState(false)
            }
        }
    }

    private fun updateTileState(activeState: Boolean) {
        if (qsTile != null) {
            qsTile?.state = if (activeState) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
            qsTile?.updateTile()
        } else {
            requestListeningState(this, ComponentName(this, ScreenLockerTileService::class.java))
        }
    }

    companion object {

        fun startTileService(context: ContextWrapper) {
            Intent(context, ScreenLockerTileService::class.java).also {
                it.action = TileAction.ActionTileStart.name
                context.startService(it)
                context.logdAndToast("Tile started")
            }
        }

        fun stopTileService(context: ContextWrapper) {
            Intent(context, ScreenLockerTileService::class.java).also {
                it.action = TileAction.ActionTileStop.name
                context.startService(it)
                context.logdAndToast("Tile stopped")
            }
        }
    }
}

enum class TileAction { ActionTileStart, ActionTileStop }
