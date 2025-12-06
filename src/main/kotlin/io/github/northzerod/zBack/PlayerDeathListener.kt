package io.github.northzerod.zBack

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import io.github.northzerod.zBack.ZBack.Companion.dbm


class PlayerDeathListener() : Listener {
    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        dbm.update(PlayerData(event.player))
    }
}
