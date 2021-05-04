package com.minexd.votesaio.command

import com.minexd.votesaio.menu.VoteMenu
import net.evilblock.cubed.command.Command
import org.bukkit.entity.Player

object VoteCommand {

    @Command(
        names = ["vote", "votes"],
        description = "Opens the Vote menu"
    )
    @JvmStatic
    fun execute(player: Player) {
        VoteMenu().openMenu(player)
    }

}