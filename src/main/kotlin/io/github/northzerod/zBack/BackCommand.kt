package io.github.northzerod.zBack

import com.mojang.brigadier.Command
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.entity.Player
import io.github.northzerod.zBack.ZBack.Companion.dbm


class BackCommand() {
    fun get(): LiteralCommandNode<CommandSourceStack> {

        val back = Commands.literal("back").executes { ctx ->
            when (val sender = ctx.source.sender) {
                is Player -> when (val playerData = dbm.query(sender.uniqueId.toString())) {
                    null -> sender.sendRichMessage("<gold>你还未死亡过")
                    else -> when (val location = playerData.toLocation()) {
                        null -> sender.sendRichMessage("<red>location is null")
                        else -> when {
                            playerData.isUsedBack -> sender.sendRichMessage("<gold>你已经回到过死亡地点了")
                            else -> {
                                sender.teleport(location)
                                dbm.update(playerData.copy(isUsedBack = true))
                                sender.sendRichMessage("<green>已回到上次死亡地点")
                            }
                        }
                    }
                }

                else -> sender.sendPlainMessage("Only players can use!")
            }
            Command.SINGLE_SUCCESS
        }

        val info = Commands.literal("info").executes { ctx ->
            when (val sender = ctx.source.sender) {
                is Player -> when (val playerData = dbm.query(sender.uniqueId.toString())) {
                    null -> sender.sendRichMessage("<gold>你还未死亡过")
                    else -> sender.sendRichMessage("<gold>$playerData")
                }

                else -> sender.sendPlainMessage("Only players can use!")
            }
            Command.SINGLE_SUCCESS
        }

        back.then(info)
        return back.build()
    }
}
