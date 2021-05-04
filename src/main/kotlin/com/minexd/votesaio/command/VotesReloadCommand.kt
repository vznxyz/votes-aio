package com.minexd.votesaio.command

import com.minexd.votesaio.VotesAIO
import com.minexd.votesaio.reward.VoteRewardHandler
import net.evilblock.cubed.command.Command
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

object VotesReloadCommand {

    @Command(
        names = ["votes reload", "votes-reload"],
        description = "Reload the VotesAIO configuration",
        permission = "op"
    )
    @JvmStatic
    fun execute(sender: CommandSender) {
        VotesAIO.instance.reloadConfig()
        VoteRewardHandler.loadRewards()

        sender.sendMessage("${ChatColor.GREEN}Successfully reloaded the VotesAIO configuration!")
    }

}