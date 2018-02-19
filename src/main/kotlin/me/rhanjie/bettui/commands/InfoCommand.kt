package me.rhanjie.ProfessionMaster.commands

import me.rhanjie.ProfessionMaster.Main
import me.rhanjie.ProfessionMaster.utility.FileManager
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class InfoCommand: CommandExecutor {
    override fun onCommand(sender: CommandSender?, command: Command?, label: String?, args: Array<out String>?): Boolean {
        if(sender != null) {
            if(args!!.size == 0)
                //sender.sendMessage("${FileManager.get("user.pluginAuthor")}")
                sender.sendMessage(Main.access.PLUGIN_INFO)

            else when (args[0]) {
                "reload" -> return this.reloadCommand(sender)
                "reset"  -> return this.clearDatabaseCommand(sender, args)
                 else    -> sender.sendMessage("${FileManager.get("user.undefinedComm")}")
            }

            return true
        }

        return false
    }

    private fun reloadCommand(sender: CommandSender): Boolean{
        if(sender.hasPermission("ProfessionMaster.reload")) {
            (Main.access.usersCache).clearCache()
            (Main.access).reloadConfig()

            FileManager.loadAll()

            sender.sendMessage("${FileManager.get("admin.reload")}")
            return true
        }

        sender.sendMessage("${FileManager.get("user.permissionNull")}")
        return false
    }

    private fun clearDatabaseCommand(sender: CommandSender, args: Array<out String>?): Boolean{
        if(sender.hasPermission("ProfessionMaster.clear")) {
            if(args!!.size == 1) {
                sender.sendMessage("${FileManager.get("admin.clearNickNull")}")

                return false
            }

            if(args[1] == "all") {
                (Main.access.usersCache).resetAll()

                sender.sendMessage("${FileManager.get("admin.sqlChange")}")
                return true
            }

            val player = Bukkit.getPlayer(args[1]) //TODO - Need offline players support
            if(player == null) {
                sender.sendMessage("${FileManager.get("user.nickNotExist")}")

                return false
            }

            (Main.access.usersCache).resetPlayer(player.uniqueId.toString())

            sender.sendMessage("${FileManager.get("admin.sqlChange")}")
            return true
        }

        sender.sendMessage("${FileManager.get("user.permissionNull")}")
        return false
    }
}