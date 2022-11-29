package me.reidj.towerdefence.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import me.func.mod.conversation.ModTransfer
import me.reidj.towerdefence.game.mob.Mob
import org.bukkit.entity.Player

/**
 * @project : tower-defence
 * @author : Рейдж
 **/

fun coroutine() = CoroutineScope(Dispatchers.IO)

fun MutableList<Mob>.clear(player: Player) {
    forEach { ModTransfer(it.uuid.toString(), "").send("td:mob-kill", player) }
    clear()
}