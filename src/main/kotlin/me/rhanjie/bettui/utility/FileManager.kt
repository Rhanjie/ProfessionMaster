package me.rhanjie.ProfessionMaster.utility

import me.rhanjie.ProfessionMaster.Main
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

class FileManager {
    companion object {
        var lang = hashMapOf(
                "pl_PL" to File(Main.access.dataFolder.absolutePath+"/lang", "pl_PL.yml"),
                "en_EN" to File(Main.access.dataFolder.absolutePath+"/lang", "en_EN.yml")
        )

        private lateinit var messages: YamlConfiguration
            private set

        val PLUGIN_NAME = Main.access.PLUGIN_NAME
        lateinit var minerals: MutableList<String>
        lateinit var pickaxes: MutableList<String>


        fun get(path: String): String{
            if(messages.getString(path) == null)
                return messages.getString("admin.textDoesNotExist")

            return messages.getString(path)
        }

        fun loadAll() {
            minerals = mutableListOf("stone", "cobblestone", "coal_ore", "iron_ore", "gold_ore", "diamond_ore")
            pickaxes = mutableListOf("wooden_pickaxe", "stone_pickaxe", "iron_pickaxe", "gold_pickaxe", "diamond_pickaxe")

            if((Main.access.dataFolder).exists())
                (Main.access.dataFolder).mkdir()

            this.createDefaultConfigIfNotExist()
            this.createDefaultMessagesIfNotExist()

            this.loadValues()
        }

        private fun createDefaultConfigIfNotExist(){
            (Main.access.config).options().copyDefaults(true);

            (Main.access.config).addDefault("language", "en_EN")
            (Main.access.config).addDefault("level.multiplier", 2)

            (Main.access.config).addDefault("MySQL.IP", "localhost")
            (Main.access.config).addDefault("MySQL.Port", 3306)
            (Main.access.config).addDefault("MySQL.Login", "root")
            (Main.access.config).addDefault("MySQL.Password", "");

            for(it in minerals){
                (Main.access.config).addDefault("mining.$it.needLevel", 1)
                (Main.access.config).addDefault("mining.$it.experience", 100)
            }

            for(it in pickaxes)
                (Main.access.config).addDefault("mining.$it.level", 1)

            (Main.access).saveConfig();
        }
        private fun createDefaultMessagesIfNotExist(){
            messages = YamlConfiguration.loadConfiguration(lang.get("en_EN"))
            messages.options().copyDefaults(true);

            messages.addDefault("initMess",                     "§9[$PLUGIN_NAME] §aApplication language is set to English!")
            messages.addDefault("mySQL.addPlayer",              "§9[$PLUGIN_NAME] §aNew player has been successfully added to the database!")
            messages.addDefault("mySQL.connectionProblem",      "§9[$PLUGIN_NAME] §cProblem with connecting to the database!")
            messages.addDefault("mySQL.closeProblem",           "§9[$PLUGIN_NAME] §cProblem with closing the connection to the database!")
            messages.addDefault("mySQL.updateProblem",          "§9[$PLUGIN_NAME] §c...")
            messages.addDefault("mySQL.queryProblem",           "§9[$PLUGIN_NAME] §c...")
            messages.addDefault("admin.reload",                 "§9[$PLUGIN_NAME] §a...")
            messages.addDefault("admin.sqlChange",              "§9[$PLUGIN_NAME] §a...")
            messages.addDefault("admin.clearNickNull",          "§9[$PLUGIN_NAME] §c...")
            messages.addDefault("admin.textDoesNotExist",       "§9[$PLUGIN_NAME] §c...")

            messages.addDefault("user.undefinedComm",           "§c...")
            messages.addDefault("user.nickNotExist",            "§c...")
            messages.addDefault("user.permissionNull",          "§c...")
            messages.addDefault("user.notEnoughLevel",          "§c...")
            messages.addDefault("user.miningNewLevel",          "§a...")

            messages.save(lang.get("en_EN"))


            messages = YamlConfiguration.loadConfiguration(lang.get("pl_PL"))
            messages.options().copyDefaults(true);

            messages.addDefault("initMess",                     "§9[$PLUGIN_NAME] §aJezyk aplikacji ustawiony na Polski")
            messages.addDefault("mySQL.addPlayer",              "§9[$PLUGIN_NAME] §aNowy gracz zostal dodany do bazy danych")
            messages.addDefault("mySQL.connectionProblem",      "§9[$PLUGIN_NAME] §cProblem z polaczeniem do bazy danych!")
            messages.addDefault("mySQL.closeProblem",           "§9[$PLUGIN_NAME] §cProblem z zamknieciem polaczenia do bazy danych!")
            messages.addDefault("mySQL.updateProblem",          "§9[$PLUGIN_NAME] §cProblem z zaaktualizowaniem rekordow w bazie danych!")
            messages.addDefault("mySQL.queryProblem",           "§9[$PLUGIN_NAME] §cProblem z pobraniem rekordow z bazy danych!")
            messages.addDefault("admin.reload",                 "§9[$PLUGIN_NAME] §aPlugin został przeladowany!")
            messages.addDefault("admin.sqlChange",              "§9[$PLUGIN_NAME] §aBaza danych zostala zaktualizowana!")
            messages.addDefault("admin.clearNickNull",          "§9[$PLUGIN_NAME] §cMusisz podac nazwe gracza albo argument 'all'!")
            messages.addDefault("admin.textDoesNotExist",       "§9[$PLUGIN_NAME] §cTekst, do ktorego plugin probuje sie odwolac nie istnieje!")

            messages.addDefault("user.undefinedComm",           "§cNiepoprawna komenda!")
            messages.addDefault("user.nickNotExist",            "§cGracz o podanym nicku nie istnieje!")
            messages.addDefault("user.permissionNull",          "§cNie posiadasz uprawnien do wykonania tej akcji!")
            messages.addDefault("user.notEnoughLevel",          "§cPosiadasz zbyt niski poziom, aby wykonac te akcje!")
            messages.addDefault("user.miningNewLevel",          "§aUzyskales nowy poziom w gornictwie!")

            messages.save(lang.get("pl_PL"))

        }

        private fun loadValues(){
            Main.language = (Main.access.config).getString("language")
            if(Main.language != "en_EN" && Main.language != "pl_PL") {
                Bukkit.getConsoleSender().sendMessage("§9[${Main.access.PLUGIN_NAME}] §cIncorrect language! Set default")

                Main.language = "en_EN"
            }

            messages = YamlConfiguration.loadConfiguration(lang.get(Main.language))
        }
    }
}