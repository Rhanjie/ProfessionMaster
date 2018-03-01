package me.rhanjie.ProfessionMaster.commands

import me.rhanjie.ProfessionMaster.Main
import me.rhanjie.ProfessionMaster.utility.FileManager
import me.rhanjie.ProfessionMaster.utility.UsersCache
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
                return this.listOfSkillsCommand(sender, args)

            else when (args[0]) {
                "reload" -> return this.reloadCommand(sender)
                "reset"  -> return this.clearDatabaseCommand(sender, args)
                "top"    -> return this.toplistCommand(sender, args)
                "author" -> sender.sendMessage(Main.access.PLUGIN_INFO)
                else     -> sender.sendMessage("${FileManager.get("user.undefinedComm")}")
            }

            return true
        }

        return false
    }

    //TODO - Configurable messages
    private fun listOfSkillsCommand(sender: CommandSender, args: Array<out String>?): Boolean{
        if(sender.hasPermission("ProfessionMaster.listOfSkills")) {
            val nextLevelExp = (Main.access.config).getInt("nextLevelExp")

            if(sender !is Player){
                sender.sendMessage(FileManager.get("admin.consolePermissionNull"))

                return false
            }

            val user = Main.access.usersCache.getUser(sender.uniqueId.toString(), sender.name)

            val mining      = FileManager.get("words.mining")
            val level       = FileManager.get("words.level")
            val exp         = FileManager.get("words.exp")
            val newLevel    = FileManager.get("words.newLevel")

            if (args!!.size == 0) {
                val text = StringBuilder()
                    text.append("§f-----------------[ ${FileManager.get("words.skills")} ]-----------------\n")
                    text.append("§f§l$mining: §f$level §6${user.miningLevel}, §f$exp §6${user.miningExp}, §f$newLevel §6${user.miningLevel * nextLevelExp}\n")
                //TODO - More professions

                sender.sendMessage(text.toString())
            } else {
                when (args[0]) {
                    "mining" -> sender.sendMessage("§f§l$mining:\n" +
                                                   "§f- $level: §6${user.miningLevel}\n" +
                                                   "§f- $exp: §6${user.miningExp}\n" +
                                                   "§f- $newLevel: §6${user.miningLevel * nextLevelExp}\n")

                    //TODO - More professions
                    else -> sender.sendMessage(FileManager.get("user.incorrectArgument"))
                }
            }

            return true
        }

        sender.sendMessage(FileManager.get("user.permissionNull"))
        return false
    }

    private fun toplistCommand(sender: CommandSender, args: Array<out String>?): Boolean{
        if(sender.hasPermission("ProfessionMaster.toplist")) {
            if (args!!.size == 0) {
                sender.sendMessage(FileManager.get("user.incorrectArgument"))

                return false
            }

            var users = hashMapOf<String, Int>()
            val limit: Int = try{ args[1].toInt() }
            catch (exception: NumberFormatException){ 5 }

            when(args[0]){
                "mining" -> users = Main.access.usersCache.database.getTopRecords("miningLevel", limit)
                //TODO: Other professions
            }

            val text = StringBuilder()
                text.append("§f-----------------[ ${FileManager.get("words.skills")} ]-----------------\n")

            var i = 1
            for(it in users)
                text.append("§f[${i++}] §l§6${it.key}§f = ${it.value}")

            return true
        }

        return false
    }

    private fun reloadCommand(sender: CommandSender): Boolean{
        if(sender.hasPermission("ProfessionMaster.reload")) {
            (Main.access.usersCache).clearCache()
            (Main.access).reloadConfig()

            FileManager.loadAll()

            sender.sendMessage(FileManager.get("admin.reload"))
            return true
        }

        sender.sendMessage(FileManager.get("user.permissionNull"))
        return false
    }

    private fun clearDatabaseCommand(sender: CommandSender, args: Array<out String>?): Boolean{
        if(sender.hasPermission("ProfessionMaster.clear")) {
            if(args!!.size == 1) {
                sender.sendMessage(FileManager.get("admin.clearNickNull"))

                return false
            }

            if(args[1] == "all") {
                (Main.access.usersCache).resetAll()

                sender.sendMessage(FileManager.get("admin.sqlChange"))
                return true
            }

            val player = Bukkit.getPlayer(args[1]) //TODO - Need offline players support
            if(player == null) {
                sender.sendMessage(FileManager.get("user.nickNotExist"))

                return false
            }

            (Main.access.usersCache).resetPlayer(player.uniqueId.toString())

            sender.sendMessage(FileManager.get("admin.sqlChange"))
            return true
        }

        sender.sendMessage(FileManager.get("user.permissionNull"))
        return false
    }
}