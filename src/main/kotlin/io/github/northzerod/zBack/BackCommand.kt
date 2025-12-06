package io.github.northzerod.zBack

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.tree.LiteralCommandNode
import io.github.northzerod.zBack.ZBack.Companion.dbm
import io.github.northzerod.zBack.ZBack.Companion.plg
import io.github.northzerod.zBack.ZBack.Companion.sch
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
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
                        null -> sender.sendMessage(
                            Component.translatable(
                                "nzd.zback.you_have_never_died", NamedTextColor.GOLD
                            )
                        )

                        else -> when (val location = playerData.toLocation()) {
                            null -> sender.sendRichMessage("<red>location is null")
                            else -> when {
                                playerData.isUsedBack -> sender.sendMessage(
                                    Component.translatable(
                                        "nzd.zback.can_only_use_once", NamedTextColor.GOLD
                                    )
                                )

                                else -> {
                                    sender.teleport(location)
                                    dbm.update(playerData.copy(isUsedBack = true))
                                    sender.sendMessage(
                                        Component.translatable(
                                            "nzd.zback.returned_to_location", NamedTextColor.GREEN
                                        )
                                    )
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
                        null -> sender.sendMessage(
                            Component.translatable(
                                "nzd.zback.you_have_never_died", NamedTextColor.GOLD
                            )
                        )

                        else -> sender.sendRichMessage("<green>$playerData")
                    }

                    else -> sender.sendPlainMessage("Only players can use!")
                }
                Command.SINGLE_SUCCESS
            }.requires { sender -> sender.sender.hasPermission("nzd.back.info") || sender.sender.isOp }

            val targetArg = Commands.argument("target", StringArgumentType.word()).executes { ctx ->
                val name = ctx.getArgument<String>("target", String::class.java)
                val sender = ctx.source.sender
                when (name.matches("^[a-zA-Z0-9_]{3,16}$".toRegex())) {
                    false -> sender.sendMessage(
                        Component.translatable(
                            "nzd.zback.username_requirements", NamedTextColor.RED
                        )
                    )

                    else -> sch.runTaskAsynchronously(plg) { _ ->
                        when (val playerData = dbm.query(Bukkit.getOfflinePlayer(name).uniqueId.toString())) {
                            null -> sender.sendMessage(
                                Component.translatable(
                                    "nzd.zback.sb_have_never_died",
                                    NamedTextColor.GOLD,
                                    Component.text(name),
                                )
                            )

                            else -> sender.sendRichMessage("<green>$playerData")
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
