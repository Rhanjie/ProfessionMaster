package me.rhanjie.ProfessionMaster.utility

import me.rhanjie.ProfessionMaster.Main
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import java.util.*

class UsersCache constructor(val database: MySQL) {
    enum class Specializations {
        MINING //TODO - Here need fill specializations
    }

    private var users = database.loadAllData()

    fun checkPlayer(uuid: String, name: String){
        if(users.containsKey(uuid) == false){
            val user: User? = database.checkPlayer(uuid, name)

            if(user == null)
                this.addPlayerToCache(uuid, name)

            else users.put(uuid, user)
        }
    }

    fun updatePlayer(uuid: String, user: User){
        database.updatePlayer(uuid, user)
    }

    fun addExp(specializations: Specializations, amount: Int, uuid: String, name: String){
        this.checkPlayer(uuid, name)

        when (specializations) {
            Specializations.MINING -> users.get(uuid)!!.miningExp += amount
            //... TODO
        }

        if(this.serveLevel(specializations, uuid, users.get(uuid)!!))
            this.updatePlayer(uuid, users.get(uuid)!!)
    }

    fun checkLevel(specializations: Specializations, uuid: String, name: String): Int{
        this.checkPlayer(uuid, name)

        when (specializations) {
            Specializations.MINING -> return users.get(uuid)!!.miningLevel
        }
    }

    fun resetPlayer(uuid: String){
        if(users.containsKey(uuid)) {
            val user = users.get(uuid)!!

            user.miningLevel = 1
            user.miningExp = 0
            //TODO - Here need fill specializations
        }

        database.resetPlayer(uuid)
    }
    fun resetAll(){
        for(it in users){
            (it.value).miningLevel = 1
            (it.value).miningExp = 0
            //TODO - Here need fill specializations
        }

        database.resetAll()
    }

    fun clearCache(){
        users.clear()
    }
    fun saveAll(){
        database.saveAllData(users)
    }

    private fun serveLevel(specializations: Specializations, uuid: String, user: User): Boolean{
        var ifUpdateSql = false

        when(specializations) {
            Specializations.MINING -> {
                while(user.miningExp >= user.miningLevel * 100){
                    user.miningExp -= user.miningLevel * 100
                    user.miningLevel++

                    this.updatePlayer(uuid, user)
                    ifUpdateSql = true

                    Bukkit.getPlayer(UUID.fromString(uuid)).sendMessage("${FileManager.get("user.miningNewLevel")} ${ChatColor.GOLD}(${user.miningLevel})")
                }
            }
        }

        return ifUpdateSql
    }
    private fun addPlayerToCache(uuid: String, name: String){
        val user = User()

        user.name = name
        user.miningLevel = 1
        user.miningExp = 0

        users.put(uuid, user)
    }
}