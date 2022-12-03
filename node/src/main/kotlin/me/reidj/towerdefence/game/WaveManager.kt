package me.reidj.towerdefence.game

import me.reidj.towerdefence.TowerDefenceGame
import me.reidj.towerdefence.clock.ClockInject

/**
 * @project : tower-defence
 * @author : Рейдж
 **/
class WaveManager(private val game: TowerDefenceGame) : ClockInject {

    override fun run(tick: Int) {
        if (tick % 20 != 0) {
            return
        }
        val now = System.currentTimeMillis()
        val wave = game.wave
        if ((now - wave.startTime) / 1000 == 60.toLong()) {
            wave.end()
        }
    }
}