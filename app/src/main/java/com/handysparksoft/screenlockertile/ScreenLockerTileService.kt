package com.handysparksoft.screenlockertile

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
        updateTileState(true)
    }

    override fun onTileRemoved() {
        super.onTileRemoved()
    }

    override fun onClick() {
        super.onClick()
        val activeState = qsTile.state == Tile.STATE_ACTIVE
        if (activeState) {
            // Turn off
            ScreenLockerService.stopService(this)
        } else {
            // Turn on
            ScreenLockerService.startService(this)
        }
        updateTileState(!activeState)
    }

    private fun checkIntent(intent: Intent?) {
        intent?.action?.let { action ->
            when (action) {
                ACTION_TILE_START -> updateTileState(true)
                ACTION_TILE_STOP -> updateTileState(false)
            }
        }
    }

    private fun updateTileState(activeState: Boolean) {
        qsTile?.state = if (activeState) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        qsTile?.updateTile()
    }

    companion object {

        private const val ACTION_TILE_START = "ACTION_TILE_START"
        private const val ACTION_TILE_STOP = "ACTION_TILE_STOP"

        fun startTileService(context: ContextWrapper) {
            Intent(context, ScreenLockerTileService::class.java).also {
                it.action = ACTION_TILE_START
                context.startService(it)
                context.logdAndToast("Tile started")
            }
        }

        fun stopTileService(context: ContextWrapper) {
            Intent(context, ScreenLockerTileService::class.java).also {
                it.action = ACTION_TILE_STOP
                context.startService(it)
                context.logdAndToast("Tile stopped")
            }
        }
    }
}
