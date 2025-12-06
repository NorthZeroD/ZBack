package io.github.northzerod.zBack

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.tree.LiteralCommandNode
import io.github.northzerod.zBack.ZBack.Companion.dbm
import io.github.northzerod.zBack.ZBack.Companion.plg
import io.github.northzerod.zBack.ZBack.Companion.sch
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.Bukkit
import org.bukkit.entity.Player


class BackCommand() {
    companion object {
        val backCommand = getNode("back")
        val bCommand = getNode("b")

        fun getNode(root: String): LiteralCommandNode<CommandSourceStack> {

            val back = Commands.literal(root).executes { ctx ->
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
            }.requires { sender -> sender.sender.hasPermission("nzd.back") || sender.sender.isOp }

            val info = Commands.literal("info").executes { ctx ->
                when (val sender = ctx.source.sender) {
                    is Player -> when (val playerData = dbm.query(sender.uniqueId.toString())) {
                        null -> sender.sendRichMessage("<gold>你还未死亡过")
                        else -> sender.sendRichMessage("<gold>$playerData")
                    }

                    else -> sender.sendPlainMessage("Only players can use!")
                }
                Command.SINGLE_SUCCESS
            }.requires { sender -> sender.sender.hasPermission("nzd.back.info") || sender.sender.isOp }

            val targetArg = Commands.argument("target", StringArgumentType.word()).executes { ctx ->
                val name = ctx.getArgument<String>("target", String::class.java)
                val sender = ctx.source.sender
                when (name.matches("^[a-zA-Z0-9_]{3,16}$".toRegex())) {
                    false -> sender.sendRichMessage("<red>玩家用户名应满足「仅包含半角大小写英文字母、数字、下划线，长度3~16字符」")
                    else -> sch.runTaskAsynchronously(plg) { _ ->
                        when (val playerData = dbm.query(Bukkit.getOfflinePlayer(name).uniqueId.toString())) {
                            null -> sender.sendRichMessage("<gold>$name 还未死亡过")
                            else -> sender.sendRichMessage("<gold>$playerData")
                        }
                    }
                }
                Command.SINGLE_SUCCESS
            }.requires { sender -> sender.sender.hasPermission("nzd.back.info.others") || sender.sender.isOp }

            back.then(info.then(targetArg))
            return back.build()
        }
    }
}
