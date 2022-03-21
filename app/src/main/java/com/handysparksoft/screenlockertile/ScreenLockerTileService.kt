package com.handysparksoft.screenlockertile

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

class ScreenLockerTileService : TileService() {

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

    private fun updateTileState(activeState: Boolean) {
        qsTile.state = if (activeState) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        qsTile.updateTile()
    }
}
