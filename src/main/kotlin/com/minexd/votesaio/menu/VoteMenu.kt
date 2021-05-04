package com.minexd.votesaio.menu

import com.minexd.core.profile.ProfileHandler
import com.minexd.rift.bukkit.RiftBukkitPlugin
import com.minexd.votesaio.VotesAIO
import com.minexd.votesaio.reward.VoteRewardHandler
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.ItemUtils
import net.evilblock.cubed.util.math.Numbers
import net.evilblock.cubed.util.text.TextSplitter
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack
import java.util.concurrent.TimeUnit

class VoteMenu : Menu() {

    override fun getTitle(player: Player): String {
        return "&a&lVote"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        return hashMapOf<Int, Button>().also { buttons ->
            buttons[20] = LinksButton()
            buttons[22] = StatsButton()
            buttons[24] = RewardsButton()

            for (i in 0..45) {
                if (!buttons.containsKey(i)) {
                    buttons[i] = GlowingGlassButton()
                }
            }
        }
    }

    override fun size(buttons: Map<Int, Button>): Int {
        return 45
    }

    private inner class LinksButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.GREEN}${ChatColor.BOLD}Voting Links"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                desc.add("")
                desc.addAll(TextSplitter.split(text = "Vote to receive perks and rare items, and if you're lucky, an extra reward!"))
                desc.add("")
                desc.add("${ChatColor.GREEN}Click to view our voting links")
            }
        }

        override fun getMaterial(player: Player): Material {
            return Material.PAPER
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                for (line in VotesAIO.instance.getVoteLinks()) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', line))
                }
            }
        }
    }

    private inner class StatsButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Statistics"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                val profile = ProfileHandler.getProfile(player.uniqueId)

                val todaysVotes = profile.getVotesInRange(TimeUnit.DAYS.toMillis(1L)).filter { it.server == RiftBukkitPlugin.instance.readServerId() }.size
                val weeksVotes = profile.getVotesInRange(TimeUnit.DAYS.toMillis(7L)).filter { it.server == RiftBukkitPlugin.instance.readServerId() }.size
                val monthsVotes = profile.getVotesInRange(TimeUnit.DAYS.toMillis(30L)).filter { it.server == RiftBukkitPlugin.instance.readServerId() }.size
                val totalVotes = profile.votes.size

                desc.add("${ChatColor.AQUA}${Constants.THICK_VERTICAL_LINE} ${ChatColor.WHITE}Today: ${ChatColor.GRAY}${Numbers.format(todaysVotes)}")
                desc.add("${ChatColor.AQUA}${Constants.THICK_VERTICAL_LINE} ${ChatColor.WHITE}Week: ${ChatColor.GRAY}${Numbers.format(weeksVotes)}")
                desc.add("${ChatColor.AQUA}${Constants.THICK_VERTICAL_LINE} ${ChatColor.WHITE}Month: ${ChatColor.GRAY}${Numbers.format(monthsVotes)}")
                desc.add("${ChatColor.AQUA}${Constants.THICK_VERTICAL_LINE} ${ChatColor.WHITE}Total: ${ChatColor.GRAY}${Numbers.format(totalVotes)}")
            }
        }

        override fun getButtonItem(player: Player): ItemStack {
            return ItemBuilder.copyOf(ItemUtils.getPlayerHeadItem(player.name))
                .name(getName(player))
                .setLore(getDescription(player))
                .build()
        }
    }

    private inner class RewardsButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.GREEN}${ChatColor.BOLD}Rewards"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                desc.add("")

                val rewardSets = VoteRewardHandler.getRewardSets().sortedBy { it.order }
                for ((index, rewardSet) in rewardSets.withIndex()) {
                    desc.add(rewardSet.name)

                    for (line in rewardSet.lines) {
                        desc.add(line)
                    }

                    if (index != rewardSets.size - 1) {
                        desc.add("")
                    }
                }
            }
        }

        override fun getMaterial(player: Player): Material {
            return Material.EMERALD
        }
    }

    private inner class GlowingGlassButton : Button() {
        override fun getName(player: Player): String {
            return " "
        }

        override fun getDescription(player: Player): List<String> {
            return emptyList()
        }

        override fun getMaterial(player: Player): Material {
            return Material.STAINED_GLASS_PANE
        }

        override fun getDamageValue(player: Player): Byte {
            return 7.toByte()
        }

        override fun getButtonItem(player: Player): ItemStack {
            return ItemBuilder.copyOf(super.getButtonItem(player)).glow().build()
        }
    }

}