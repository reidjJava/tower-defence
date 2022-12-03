package me.reidj.towerdefence.game.mob

import me.reidj.towerdefence.TowerDefenceGame
import me.reidj.towerdefence.clock.ClockInject
import me.reidj.towerdefence.util.Location
import org.bukkit.Bukkit
import kotlin.math.pow

/**
 * @project : tower-defence
 * @author : Рейдж
 **/
class MobManager(private val game: TowerDefenceGame) : ClockInject {

    private val routes = game.routes.map { Location(it.x, it.y, it.z, it.yaw) }

    override fun run(tick: Int) {
        val mobs = game.wave.aliveMobs
        if (mobs.isEmpty() || routes.isEmpty()) {
            return
        }
        mobs.forEach { mob ->
            val position = mob.getPosition(routes, mob.timeSpawn)
            val lastRoute = routes.last()
            if ((lastRoute.x - position.x).pow(2.0) + (lastRoute.z - position.z).pow(2.0) <= 1.0) {
                if (tick % (mob.attackSpeed * 20) == 0 && game.health > 0) {
                    Bukkit.getOnlinePlayers().forEach {
                        mob.playHurtAnimation(it)
                        game.playerHit(it, mob.damage)
                    }
                }
            }
        }
    }
}