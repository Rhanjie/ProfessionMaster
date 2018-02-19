package me.rhanjie.ProfessionMaster.listeners

import me.rhanjie.ProfessionMaster.Main
import me.rhanjie.ProfessionMaster.utility.FileManager
import me.rhanjie.ProfessionMaster.utility.UsersCache
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent

class MiningListener: Listener {
    @EventHandler(priority = EventPriority.NORMAL)
    fun onDestroyBlock(event: BlockBreakEvent): Boolean {
        if(event.player.gameMode == GameMode.SURVIVAL) {
            val level = (Main.access.usersCache).checkLevel(UsersCache.Specializations.MINING, (event.player.uniqueId).toString(), event.player.name)
            val needPickaxeLevel = (Main.access.config).getInt("mining.${event.player.inventory.itemInMainHand.type.name.toLowerCase()}.level")
            val needMineralLevel = (Main.access.config).getInt("mining.${(event.block.type).toString().toLowerCase()}.needLevel")

            if(/*needPickaxeLevel == 0 || */needMineralLevel == 0)
                return false

            if(level < needPickaxeLevel || level < needMineralLevel){
                event.isCancelled = true

                event.player.sendMessage("${FileManager.get("user.notEnoughLevel")}")
                return false
            }

            (Main.access.usersCache).addExp(UsersCache.Specializations.MINING, (Main.access.config).getInt("mining.${(event.block.type).toString().toLowerCase()}.experience"), (event.player.uniqueId).toString(), event.player.name)
            return true
        }

        return true
    }
}