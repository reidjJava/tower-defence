package me.reidj.towerdefence.game

import me.func.mod.Anime
import me.func.mod.conversation.ModTransfer
import me.func.mod.util.after
import me.reidj.towerdefence.app
import me.reidj.towerdefence.game.mob.Mob
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player

/**
 * @project : tower-defence
 * @author : Рейдж
 **/
data class Wave(
    var startTime: Long,
    var level: Int,
    val aliveMobs: MutableList<Mob>,
    private val player: Player
) {

    fun start() {
        startTime = System.currentTimeMillis()

        ModTransfer("$level волна. До следующей волны", 60).send("td:bar", player)

        /*repeat(2 + level * 2) {
            Bukkit.getScheduler().runTaskLater(app, { drawMob() }, minOf(it.toLong() * 75, 400))
        }*/
    }

    fun end() {
        val user = app.getUser(player) ?: return

        level++

        user.giveMoney(5)

        Anime.counting321(player)
        after(3 * 20) { start() }
    }

    private fun createMob() {
        val hpFormula = level * 0.3
        Mob {
            hp = 0.1 + hpFormula
            moveSpeed = 0.1F
            type = EntityType.ZOMBIE
        }.create(player).also { aliveMobs.add(it) }
    }
}
