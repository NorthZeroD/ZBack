package io.github.northzerod.zBack

import io.github.northzerod.zBack.ZBack.Companion.log
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import java.text.DecimalFormat

data class PlayerData(
    val uuid: String,
    val name: String?,
    val dimension: String,
    val x: Double,
    val y: Double,
    val z: Double,
    val yaw: Float,
    val pitch: Float,
    val isUsedBack: Boolean = false,
) {
    constructor(player: Player) : this(
        player.uniqueId.toString(),
        player.name,
        player.world.key.toString(),
        player.location.x,
        player.location.y,
        player.location.z,
        player.location.yaw,
        player.location.pitch,
        false,
    )

    fun toLocation(): Location? {
        when (val worldKey = NamespacedKey.fromString(dimension)) {
            null -> log.warning("WorldKey '$dimension' is null")
            else -> return Location(Bukkit.getWorld(worldKey), x, y, z, yaw, pitch)
        }
        return null
    }

    override fun toString(): String {
        val fmt = { x: Double -> DecimalFormat("#.##").format(x) }
        return """
            [uuid] $uuid
            [name] $name
            [dimension] $dimension
            [x] ${fmt(x)}
            [y] ${fmt(y)}
            [z] ${fmt(z)}
            [yaw] ${fmt(yaw.toDouble())}
            [pitch] ${fmt(pitch.toDouble())}
            [isUsedBack] $isUsedBack
        """.trimIndent()
    }
}
