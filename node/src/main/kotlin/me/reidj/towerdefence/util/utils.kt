package me.reidj.towerdefence.util

import me.func.mod.Anime
import me.func.mod.conversation.ModTransfer
import me.reidj.towerdefence.game.mob.Mob
import me.reidj.towerdefence.player.User
import org.bukkit.entity.Player

/**
 * @project : tower-defence
 * @author : Рейдж
 **/

fun MutableList<Mob>.clear(player: Player) {
    forEach { ModTransfer(it.uuid.toString(), "").send("td:mob-kill", player) }
    clear()
}

fun User.killMobs() {
    Anime.sendEmptyBuffer("td:kill-all", player!!)
}