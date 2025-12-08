package io.github.northzerod.zBack

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import net.kyori.adventure.key.Key
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitScheduler
import java.util.logging.Logger


class ZBack : JavaPlugin() {
    companion object {
        lateinit var plg: ZBack
        lateinit var log: Logger
        lateinit var dbm: DatabaseManager
        lateinit var sch: BukkitScheduler
        lateinit var worldUuid: String
    }

    override fun onEnable() {
        plg = this
        log = plg.logger
        worldUuid = Bukkit.getWorld(Key.key("minecraft:overworld"))?.uid.toString().replace("-", "")
        saveDefaultConfig()
        TranslationRegister()
        dbm = DatabaseManager("jdbc:sqlite:plugins/ZBack/database.db")
        sch = this.server.scheduler
        server.pluginManager.registerEvents(PlayerDeathListener(), this)
        lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS) { commands ->
            commands.registrar().register(BackCommand.backCommand)
            commands.registrar().register(BackCommand.bCommand)
        }
        log.info("Enabled ZBack :D")
    }

    override fun onDisable() {
        dbm.close()
    }
}
