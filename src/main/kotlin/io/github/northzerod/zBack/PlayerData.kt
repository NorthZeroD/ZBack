package io.github.northzerod.zBack

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.NamespacedKey
import io.github.northzerod.zBack.ZBack.Companion.plg

data class PlayerData(
    val uuid: String?,
    val dimension: String,
    val x: Double, val y: Double, val z: Double,
    val yaw: Float, val pitch: Float,
    val isUsedBack: Boolean = false
) {
    fun toLocation(): Location? {
        when (val worldKey = NamespacedKey.fromString(dimension)) {
            null -> plg.logger.warning("WorldKey '$dimension' is null")
            else -> return Location(Bukkit.getWorld(worldKey), x, y, z, yaw, pitch)
        }
        return null
    }

    override fun toString(): String {
        return """
            [uuid] $uuid
            [dimension] $dimension
            [x, y, z] $x, $y, $z
            [yaw, pitch] $yaw, $pitch
            [is_used_back] $isUsedBack
        """.trimIndent()
    }
}
