package io.github.northzerod.zBack

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Logger


class ZBack : JavaPlugin() {
    companion object {
        lateinit var plg: ZBack
        lateinit var dbm: DatabaseManager
    }

    override fun onEnable() {
        logger.info("Hello!")
        plg = this
        saveDefaultConfig()
        dbm = DatabaseManager("jdbc:sqlite:plugins/ZBack/database.db")
        server.pluginManager.registerEvents(PlayerDeathListener(), this)
        lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS) { commands ->
            commands.registrar().register(BackCommand().get())
        }
    }

    override fun onDisable() {
        dbm.close()
    }

    override fun getLogger(): Logger {
        return super.getLogger()
    }

    override fun saveDefaultConfig() {
        super.saveDefaultConfig()
    }

}
