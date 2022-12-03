package me.reidj.towerdefence.game

import me.func.mod.Anime
import me.func.mod.conversation.ModTransfer
import me.func.mod.util.after
import me.reidj.towerdefence.app
import me.reidj.towerdefence.game.mob.Mob
import org.bukkit.Bukkit

/**
 * @project : tower-defence
 * @author : Рейдж
 **/
data class Wave(
    var startTime: Long,
    var level: Int,
    val aliveMobs: MutableList<Mob>
) {

    fun start() {
        startTime = System.currentTimeMillis()

        Bukkit.getOnlinePlayers().forEach { ModTransfer("$level волна. До следующей волны", 60).send("td:bar", it) }

        repeat(2 + level * 2) {
            Bukkit.getScheduler().runTaskLater(app, { createMob() }, minOf(it.toLong() * 75, 400))
        }
    }

    fun end() {
        level++
        Bukkit.getOnlinePlayers().forEach {
            val user = app.getUser(it) ?: return
            user.giveMoney(5)
            Anime.counting321(it)
        }
        after(3 * 20) { start() }
    }

    private fun createMob() {
        val hpFormula = level * 0.3
        aliveMobs.add(Mob.Builder()
            .hp(0.1 + hpFormula)
            .damage(2.0)
            .attackSpeed(5)
            .moveSpeed(0.01F)
            .build()
            .show(*Bukkit.getOnlinePlayers().toTypedArray()))
    }
}
