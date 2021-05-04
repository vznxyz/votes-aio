package com.minexd.votesaio.event

import com.minexd.core.profile.vote.VoteRecord
import net.evilblock.cubed.plugin.PluginEvent
import org.bukkit.entity.Player

class VoteEvent(val player: Player, val vote: VoteRecord) : PluginEvent() {


}