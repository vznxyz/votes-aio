package com.minexd.votesaio.command

import com.minexd.votesaio.VotesAIO
import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

object VotePartySetProgressCommand {

    @Command(
        names = ["vote-party set-progress"],
        description = "Sets the Vote Party progress",
        permission = "op"
    )
    @JvmStatic
    fun execute(sender: CommandSender, @Param(name = "amount") amount: Int) {
        VotesAIO.instance.voteParty = amount
        VotesAIO.instance.saveVotePartyProgress()

        sender.sendMessage("${ChatColor.GREEN}Set Vote Party progress to $amount!")
    }



}