package me.rhanjie.ProfessionMaster.listeners

import me.rhanjie.ProfessionMaster.Main
import me.rhanjie.ProfessionMaster.utility.FileManager
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class RegisterPlayer: Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent): Boolean{
        (Main.access.usersCache).checkPlayer((event.player.uniqueId).toString(), event.player.name)

        return true
    }
}