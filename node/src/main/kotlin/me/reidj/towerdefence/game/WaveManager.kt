package me.reidj.towerdefence.game

import me.func.mod.util.after
import me.reidj.towerdefence.app
import me.reidj.towerdefence.clock.ClockInject
import me.reidj.towerdefence.util.clear
import org.bukkit.Bukkit

/**
 * @project : tower-defence
 * @author : Рейдж
 **/
class WaveManager : ClockInject {

    override fun run(tick: Int) {
        if (tick % 20 != 0) {
            return
        }
        Bukkit.getOnlinePlayers()
            .mapNotNull { app.getUser(it) }
            .filter { it.session != null }
            .forEach {
                val wave = it.session!!.wave
                val now = System.currentTimeMillis()
                if ((now - wave.startTime) / 1000 == 60.toLong() || wave.aliveMobs.isEmpty()) {
                    wave.aliveMobs.clear(it.player!!)
                    after { wave.end() }
                }
            }
    }
}