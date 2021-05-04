package com.minexd.votesaio.reward

import org.bukkit.Bukkit
import org.bukkit.entity.Player

class VoteReward(
    val name: String,
    val commands: List<String>
) {

    var milestone: Int? = null
    var chance: Double? = null

    fun execute(player: Player) {
        for (command in commands) {
            val processedCommand = command
                .replace("{playerUUID}", player.uniqueId.toString())
                .replace("{playerName}", player.name)

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), processedCommand)
        }
    }

}