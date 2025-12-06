package io.github.northzerod.zBack

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import io.github.northzerod.zBack.ZBack.Companion.dbm


class PlayerDeathListener() : Listener {
    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        val player = event.player
        val location = player.location
        val uuid = player.uniqueId.toString()
        val dimension = player.world.key.toString()
        val x = location.x
        val y = location.y
        val z = location.z
        val yaw = location.yaw
        val pitch = location.pitch
        dbm.update(PlayerData(uuid, dimension, x, y, z, yaw, pitch))
    }
}
