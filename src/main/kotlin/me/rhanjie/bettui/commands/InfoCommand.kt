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
                return this.listOfSkillsCommand(sender, null)

            else when (args[0]) {
                "reload"      -> return this.reloadCommand(sender)
                "reset"       -> return this.clearDatabaseCommand(sender, args)
                "check",  "c" -> return this.listOfSkillsCommand(sender, args)
                "top",    "t" -> return this.toplistCommand(sender, args)
                "author", "a" -> sender.sendMessage(Main.access.PLUGIN_INFO)
                else          -> sender.sendMessage("${FileManager.get("user.undefinedComm")}")
            }

            return true
        }

        return false
    }

    private fun listOfSkillsCommand(sender: CommandSender, args: Array<out String>?): Boolean{
        if(sender.hasPermission("ProfessionMaster.listOfSkills")){
            val nextLevelExp = (Main.access.config).getInt("nextLevelExp")

            if(sender !is Player){
                sender.sendMessage(FileManager.get("admin.consolePermissionNull"))

                return false
            }

            val user = Main.access.usersCache.getUser(sender.uniqueId.toString(), sender.name)

            val level       = FileManager.get("words.level")
            val exp         = FileManager.get("words.exp")
            val newLevel    = FileManager.get("words.newLevel")
            val mining      = FileManager.get("words.mining")
            val woodcutting = FileManager.get("words.woodcutting")
            val gathering   = FileManager.get("words.gathering")
            val farming     = FileManager.get("words.farming")
            val combat      = FileManager.get("words.combat")

            if (args == null || args.size == 1){
                val text = StringBuilder()
                    text.append("§a§l * [${FileManager.get("words.skills").toUpperCase()}] *\n")
                    text.append("§f§l$mining: §f$level: §6${user.miningLevel} §f(§6${user.miningExp}§f/§6${user.miningLevel * nextLevelExp}§f)\n")
                    text.append("§f§l$woodcutting: §f$level: §6${user.woodcuttingLevel} §f(§6${user.woodcuttingExp}§f/§6${user.woodcuttingLevel * nextLevelExp}§f)\n")
                    text.append("§f§l$gathering: §f$level: §6${user.gatheringLevel} §f(§6${user.gatheringExp}§f/§6${user.gatheringLevel * nextLevelExp}§f)\n")
                    text.append("§f§l$farming: §f$level: §6${user.farmingLevel} §f(§6${user.farmingExp}§f/§6${user.farmingLevel * nextLevelExp}§f)\n")
                    text.append("§f§l$combat: §f$level: §6${user.combatLevel} §f(§6${user.combatExp}§f/§6${user.combatLevel * nextLevelExp}§f)\n")

                sender.sendMessage(text.toString())
            } else {
                when (args[1]) {
                    "mining", "m" -> sender.sendMessage(
                            "§f§l$mining:\n" +
                            "§f- $level: §6${user.miningLevel}\n" +
                            "§f- $exp: §6${user.miningExp}\n" +
                            "§f- $newLevel: §6${user.miningLevel * nextLevelExp}\n")
                    "woodcutting", "w" -> sender.sendMessage(
                            "§f§l$woodcutting:\n" +
                            "§f- $level: §6${user.woodcuttingLevel}\n" +
                            "§f- $exp: §6${user.woodcuttingExp}\n" +
                            "§f- $newLevel: §6${user.woodcuttingLevel * nextLevelExp}\n")
                    "gathering", "g" -> sender.sendMessage(
                            "§f§l$gathering:\n" +
                            "§f- $level: §6${user.gatheringLevel}\n" +
                            "§f- $exp: §6${user.gatheringExp}\n" +
                            "§f- $newLevel: §6${user.gatheringLevel * nextLevelExp}\n")
                    "farming", "f" -> sender.sendMessage(
                            "§f§l$farming:\n" +
                            "§f- $level: §6${user.farmingLevel}\n" +
                            "§f- $exp: §6${user.farmingExp}\n" +
                            "§f- $newLevel: §6${user.farmingLevel * nextLevelExp}\n")
                    "combat", "c" -> sender.sendMessage(
                            "§f§l$combat:\n" +
                            "§f- $level: §6${user.combatLevel}\n" +
                            "§f- $exp: §6${user.combatExp}\n" +
                            "§f- $newLevel: §6${user.combatLevel * nextLevelExp}\n")
                    else -> sender.sendMessage("${FileManager.get("user.incorrectArgument")}")
                }
            }

            return true
        }

        sender.sendMessage(FileManager.get("user.permissionNull"))
        return false
    }

    private fun toplistCommand(sender: CommandSender, args: Array<out String>?): Boolean{
        if(sender.hasPermission("ProfessionMaster.toplist")){
            if (args!!.size == 1) {
                sender.sendMessage(FileManager.get("user.notEnoughArguments"))

                return false
            }

            var columnName = ""
            var limit: Int = 5

            if(args.size > 2){
                try{limit = args[2].toInt()}
                catch(exception: NumberFormatException){}
            }

            when(args[1]){
                "mining",       "m" -> columnName = "miningLevel"
                "woodcutting",  "w" -> columnName = "woodcuttingLevel"
                "gathering",    "g" -> columnName = "gatheringLevel"
                "farming",      "f" -> columnName = "farmingLevel"
                "combat",       "c" -> columnName = "combatLevel"
            }

            if(columnName == ""){
                sender.sendMessage(FileManager.get("user.incorrectArgument"))

                return false
            }

            var users = Main.access.usersCache.database.getTopRecords(columnName, limit)

            var i = 1
            val level = FileManager.get("words.level").toLowerCase()
            val text = StringBuilder()
                text.append("§a§l * [${FileManager.get("words.${columnName.replace("Level", "")}").toUpperCase()}] *\n")


            for(it in users)
                text.append("§f[${i++}] §l§6${it.key}§f: ${it.value} $level")

            sender.sendMessage(text.toString())
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