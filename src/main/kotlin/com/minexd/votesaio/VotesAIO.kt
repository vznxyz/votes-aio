package com.minexd.votesaio

import com.minexd.votesaio.command.VoteCommand
import com.minexd.votesaio.command.VotePartySetProgressCommand
import com.minexd.votesaio.command.VotesReloadCommand
import com.minexd.votesaio.listener.VoteListeners
import com.minexd.votesaio.reward.VoteRewardHandler
import net.evilblock.cubed.command.CommandHandler
import org.bukkit.ChatColor
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class VotesAIO : JavaPlugin() {

    companion object {
        @JvmStatic
        lateinit var instance: VotesAIO

        @JvmStatic
        val CHAT_PREFIX: String = "${ChatColor.GREEN}${ChatColor.BOLD}Votes > ${ChatColor.GRAY}"
    }

    var voteParty: Int = 0

    override fun onEnable() {
        instance = this

        saveDefaultConfig()

        voteParty = config.getInt("vote-party-progress", 0)
        VoteRewardHandler.loadRewards()

        CommandHandler.registerClass(VotesReloadCommand.javaClass)
        CommandHandler.registerClass(VoteCommand.javaClass)
        CommandHandler.registerClass(VotePartySetProgressCommand.javaClass)

        server.pluginManager.registerEvents(VoteListeners, this)
    }

    fun isVotePartyEnabled(): Boolean {
        return config.getBoolean("vote-party-enabled")
    }

    fun isRewardsEnabled(): Boolean {
        return config.getBoolean("rewards-enabled")
    }

    fun getVoteLinks(): List<String> {
        return config.getStringList("vote-links")
    }

    fun saveVotePartyProgress() {
        config.set("vote-party-progress", voteParty)
        config.save(File(dataFolder, "config.yml"))
    }

    fun getVotePartyGoal(): Int {
        return config.getInt("vote-party-goal", 100)
    }

    fun getVotePartyCommands(): List<String> {
        return config.getStringList("vote-party-commands")
    }

}