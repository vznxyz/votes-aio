package com.minexd.votesaio.reward

class VoteRewardSet(val id: String, val name: String, val order: Int) {

    val rewards: MutableList<VoteReward> = arrayListOf()
    val lines: MutableList<String> = arrayListOf()

}