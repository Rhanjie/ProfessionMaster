package me.rhanjie.ProfessionMaster

import me.rhanjie.ProfessionMaster.commands.InfoCommand
import me.rhanjie.ProfessionMaster.listeners.MiningListener
import me.rhanjie.ProfessionMaster.listeners.RegisterPlayer
import me.rhanjie.ProfessionMaster.utility.FileManager
import me.rhanjie.ProfessionMaster.utility.MySQL
import me.rhanjie.ProfessionMaster.utility.UsersCache
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class Main: JavaPlugin() {
    companion object {
        lateinit var access: Main
        lateinit var language: String
    }

    lateinit var usersCache: UsersCache
    val PLUGIN_NAME = "ProfessionMaster"
    val PLUGIN_VERSION = "0.2.2 Public"
    val PLUGIN_INFO = this.getPluginInformation()

    override fun onEnable() {
        access = this

        FileManager.loadAll()

        val ip       = config.getString("MySQL.IP")
        val port        = config.getInt   ("MySQL.Port")
        val login    = config.getString("MySQL.Login")
        val password = config.getString("MySQL.Password")
        usersCache = UsersCache(MySQL(ip, port, login, password))

        this.registerCommands()
        this.registerListeners()

        Bukkit.getConsoleSender().sendMessage(FileManager.get("initMess"))
    }

    override fun onDisable() {
        usersCache.saveAll()
    }

    private fun registerCommands() {
        this.getCommand("level").executor = InfoCommand()
        this.getCommand("lvl").executor = InfoCommand()
    }
    private fun registerListeners() {
        (server.pluginManager).registerEvents(RegisterPlayer(), this)
        (server.pluginManager).registerEvents(MiningListener(), this)

        //TODO - Other professions listeners
    }

    /* Modifications to this method are not allowed! */
    private fun getPluginInformation(): String{
        val text = StringBuilder()
            text.append("§f--------------------------------------     \n")
            text.append("§f§lPlugin name: §6${PLUGIN_NAME}            \n")
            text.append("§f§lPlugin version: §6${PLUGIN_VERSION}      \n")
            text.append("§f§lPlugin author: §6Marcin (RhAnjiE) Dyla   \n")
            text.append("§f--------------------------------------     \n")

        return text.toString()
    }
}