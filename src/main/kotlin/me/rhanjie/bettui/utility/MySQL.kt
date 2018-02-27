package me.rhanjie.ProfessionMaster.utility

import me.rhanjie.ProfessionMaster.Main
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException


class MySQL constructor(var ip: String, var port: Int, var login: String, var password: String) {
    private lateinit var connection: Connection

    init{
        this.createDatabase()
        this.loadAllData()
    }

    fun loadAllData(): HashMap<String, User> {
        var users = HashMap<String, User>()

        if(!this.openConnection())
            return users

        var result = connection.createStatement().executeQuery("SELECT * FROM users")
        while(result.next()){
            val user = User()
                user.name = result.getString("name")
                user.miningLevel = result.getInt("miningLevel")
                user.miningExp = result.getInt("miningExp")
            //TODO - Here need fill specializations

            users.put(result.getString("uuid"), user)
        }

        this.closeConnection()
        return users
    }
    fun saveAllData(users: HashMap<String, User>) {
        if(!this.openConnection())
            return

        for(user in users){
            var builder = StringBuilder()
                builder.append("INSERT INTO users (uuid, name, miningLevel, miningExp) VALUES (")
                builder.append("'${user.key}',")
                builder.append("'${user.value.name}',")
                builder.append("'${user.value.miningLevel}',")
                builder.append("'${user.value.miningExp}')")
                builder.append("ON DUPLICATE KEY UPDATE ")
                builder.append("miningLevel='${user.value.miningLevel}',")
                builder.append("miningExp='${user.value.miningExp}'")
            //TODO - Here need fill specializations

            connection.createStatement().executeUpdate(builder.toString())
        }

        this.closeConnection()
    }

    fun checkPlayer(uuid: String, name: String): User? { //TODO - Remove this method
        if(!this.openConnection())
            return null

        var builder = StringBuilder()
            builder.append("SELECT * FROM users WHERE uuid='$uuid'")

        try {
            var result = connection.createStatement().executeQuery(builder.toString())
            if(!result.next()) {
                this.addPlayer(uuid, name)
                this.closeConnection()

                return null
            }

            val user = User()
                user.name = result.getString("name")
                user.miningLevel = result.getInt("MiningLevel")
                user.miningExp = result.getInt("MiningExp")
            //TODO - Here need fill specializations

            this.closeConnection()
            return user
        }
        catch (exception: SQLException) {
            Bukkit.getConsoleSender().sendMessage("${FileManager.get("mySQL.queryProblem")}")

            this.closeConnection()
            return null
        }
    }

    fun updatePlayer(uuid: String, user: User): Boolean{
        if(!this.openConnection())
            return false

        var builder = StringBuilder()
            builder.append("UPDATE users SET name='${user.name}', miningLevel='${user.miningLevel}', miningExp='${user.miningExp}' WHERE uuid='${uuid}'")

        try{connection.createStatement().executeUpdate(builder.toString())}
        catch(exception: SQLException){
            Bukkit.getConsoleSender().sendMessage("${FileManager.get("mySQL.updateProblem")}")

            this.closeConnection()
            return false
        }

        this.closeConnection()
        return true
    }

    fun resetPlayer(uuid: String): Boolean{
        if(!this.openConnection())
            return false

        var builder = StringBuilder()
            builder.append("UPDATE users SET miningLevel='1', miningExp='0' WHERE uuid='$uuid'")
        //TODO - Here need fill specializations

        try{connection.createStatement().executeUpdate(builder.toString())}
        catch(exception: SQLException){
            Bukkit.getConsoleSender().sendMessage("${FileManager.get("mySQL.updateProblem")}")

            this.closeConnection()
            return false
        }

        this.closeConnection()
        return true
    }
    fun resetAll(): Boolean{
        if(!this.openConnection())
            return false

        var builder = StringBuilder()
            builder.append("UPDATE users SET miningLevel='1', miningExp='0' WHERE 1")
        //TODO - Here need fill specializations

        try{connection.createStatement().executeUpdate(builder.toString())}
        catch(exception: SQLException){
            Bukkit.getConsoleSender().sendMessage("${FileManager.get("mySQL.updateProblem")}")

            this.closeConnection()
            return false
        }

        this.closeConnection()
        return true
    }

    private fun addPlayer(uuid: String, name: String) {
        var builder = StringBuilder()
            builder.append("INSERT INTO users(uuid, name, miningLevel, miningExp) VALUES ('$uuid', '$name', 1, 0)")

        connection.createStatement().executeUpdate(builder.toString())

        Bukkit.getConsoleSender().sendMessage("${FileManager.get("mySQL.addPlayer")} (${ChatColor.GREEN}$name${ChatColor.RESET})")
    }

    private fun openConnection(): Boolean {
        try { connection = DriverManager.getConnection("jdbc:mysql://$ip:$port/ProfessionMaster?user=$login&password=$password") }
        catch (exception: SQLException) {
            Bukkit.getConsoleSender().sendMessage(FileManager.get("mySQL.connectionProblem"))

            return false
        }

        return true
    }
    private fun createDatabase(): Boolean {
        try { connection = DriverManager.getConnection("jdbc:mysql://$ip:$port?user=$login&password=$password") }
        catch (exception: SQLException) {
            Bukkit.getConsoleSender().sendMessage(FileManager.get("mySQL.connectionProblem"))
            Bukkit.getPluginManager().disablePlugin(Main.access)

            return false
        }

        var builder = StringBuilder()
        builder.append("CREATE DATABASE IF NOT EXISTS ProfessionMaster;")

        connection.createStatement().executeUpdate(builder.toString())

        builder = StringBuilder()
        builder.append("CREATE TABLE IF NOT EXISTS ProfessionMaster.users(")
        builder.append("uuid VARCHAR(100) NOT NULL,")
        builder.append("name VARCHAR(50) NOT NULL,")
        builder.append("miningLevel INT(5) NOT NULL,")
        builder.append("miningExp INT(100) NOT NULL,")
        builder.append("PRIMARY KEY(uuid));")
        //TODO - Here need fill specializations

        connection.createStatement().executeUpdate(builder.toString())

        this.closeConnection()
        return true
    }
    private fun closeConnection() {
        try{connection.close()}
        catch(exception: SQLException){
            Bukkit.getConsoleSender().sendMessage(FileManager.get("mySQL.closeProblem"))
        }
    }
}