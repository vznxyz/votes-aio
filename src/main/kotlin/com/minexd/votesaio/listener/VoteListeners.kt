package com.minexd.votesaio.listener

import com.minexd.core.CoreXD
import com.minexd.core.profile.Profile
import com.minexd.core.profile.ProfileHandler
import com.minexd.core.profile.vote.VoteRecord
import com.minexd.rift.bukkit.RiftBukkitPlugin
import com.minexd.votesaio.VotesAIO
import com.minexd.votesaio.event.VoteEvent
import com.minexd.votesaio.event.VotePartyEvent
import com.minexd.votesaio.reward.VoteRewardHandler
import com.vexsoftware.votifier.model.VotifierEvent
import net.evilblock.cubed.Cubed
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.math.Chance
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

object VoteListeners : Listener {

    @EventHandler
    fun onVotifierEvent(event: VotifierEvent) {
        Tasks.async {
            val uuid = Cubed.instance.uuidCache.uuid(event.vote.username) ?: return@async

            val record = VoteRecord(
                uuid = uuid,
                service = event.vote.serviceName,
                address = event.vote.address,
                server = RiftBukkitPlugin.instance.readServerId()
            )

            val profile: Profile? = if (ProfileHandler.isProfileCached(uuid)) {
                ProfileHandler.getProfile(uuid)
            } else {
                null
            }

            profile?.votes?.add(record)

            Bukkit.getPlayer(uuid)?.also { player ->
                try {
                    VoteEvent(player, record).call()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                if (VotesAIO.instance.isRewardsEnabled()) {
                    VoteRewardHandler.getRewardSetById("regular")?.also { rewardSet ->
                        for (reward in rewardSet.rewards) {
                            player.sendMessage("${VotesAIO.CHAT_PREFIX}You received ${reward.name} ${ChatColor.GRAY}for voting!")
                            reward.execute(player)
                        }
                    }

                    if (profile != null) {
                        VoteRewardHandler.getRewardSetById("milestone")?.also { rewardSet ->
                            val eligibleRewards = rewardSet.rewards.filter { it.milestone != null && it.milestone!! > 0 }
                            if (eligibleRewards.isNotEmpty()) {
                                val totalVotes = profile.votes.filter { it.server == RiftBukkitPlugin.instance.readServerId() }.size

                                val matching = eligibleRewards.firstOrNull { it.milestone!! == totalVotes }
                                if (matching != null) {
                                    player.sendMessage("${VotesAIO.CHAT_PREFIX}You received ${matching.name} ${ChatColor.GRAY}for reaching the $totalVotes Votes Milestone!")
                                    matching.execute(player)
                                }
                            }
                        }
                    }

                    VoteRewardHandler.getRewardSetById("lucky")?.also { rewardSet ->
                        val eligibleRewards = rewardSet.rewards.filter { it.chance != null && it.chance!! > 0.0 }
                        if (eligibleRewards.isNotEmpty()) {
                            val selectedReward = eligibleRewards.random()
                            if (Chance.percent(selectedReward.chance!!)) {
                                player.sendMessage("${VotesAIO.CHAT_PREFIX}You got lucky and received an extra reward: ${selectedReward.name}")
                                selectedReward.execute(player)
                            }
                        }
                    }
                }
            }

            try {
                val response = CoreXD.instance.profilesService.countVote(uuid, record).execute()
                if (!response.isSuccessful) {
                    VotesAIO.instance.logger.severe("Failed to count vote for ${event.vote.username} (${event.vote.serviceName})!")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            if (VotesAIO.instance.isVotePartyEnabled()) {
                VotesAIO.instance.voteParty++

                if (VotesAIO.instance.voteParty >= VotesAIO.instance.getVotePartyGoal()) {
                    VotesAIO.instance.voteParty = 0

                    for (command in VotesAIO.instance.getVotePartyCommands()) {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command)
                    }

                    try {
                        VotePartyEvent().call()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                VotesAIO.instance.saveVotePartyProgress()
            }
        }
    }

}