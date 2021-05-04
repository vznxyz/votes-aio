package com.minexd.votesaio.reward

import com.minexd.votesaio.VotesAIO
import org.bukkit.ChatColor
import java.util.concurrent.ConcurrentHashMap

object VoteRewardHandler {

    private var rewardSets: MutableMap<String, VoteRewardSet> = ConcurrentHashMap()

    fun loadRewards() {
        val newMap: ConcurrentHashMap<String, VoteRewardSet> = ConcurrentHashMap()

        val config = VotesAIO.instance.config
        for (key in config.getConfigurationSection("reward-sets").getKeys(false)) {
            val section = config.getConfigurationSection("reward-sets.$key")

            val rewardSet = VoteRewardSet(
                id = key,
                name = ChatColor.translateAlternateColorCodes('&', section.getString("name")),
                order = section.getInt("order", 999)
            )

            rewardSet.lines.addAll(section.getStringList("lines")
                .map { ChatColor.translateAlternateColorCodes('&', it) })

            if (section.contains("rewards")) {
                (section.getList("rewards") as List<Map<String, Any>>).let { rewards ->
                    for (rewardData in rewards) {
                        val reward = VoteReward(
                            name = rewardData["name"] as String,
                            commands = rewardData["commands"] as List<String>
                        )

                        if (rewardData.containsKey("milestone")) {
                            reward.milestone = rewardData["milestone"] as Int
                        }

                        if (rewardData.containsKey("chance")) {
                            reward.chance = (rewardData["chance"] as Number).toDouble()
                        }

                        rewardSet.rewards.add(reward)
                    }
                }
            }

            newMap[key.toLowerCase()] = rewardSet
        }

        rewardSets = newMap
    }

    fun getRewardSets(): Collection<VoteRewardSet> {
        return rewardSets.values
    }

    fun getRewardSetById(id: String): VoteRewardSet? {
        return rewardSets[id.toLowerCase()]
    }

}