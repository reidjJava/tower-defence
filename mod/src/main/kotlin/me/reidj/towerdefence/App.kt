package me.reidj.towerdefence

import me.reidj.towerdefence.mob.MobManager
import me.reidj.towerdefence.player.PlayerBar
import ru.cristalix.clientapi.KotlinMod
import ru.cristalix.uiengine.UIEngine

/**
 * @project : tower-defence
 * @author : Рейдж
 **/
class App : KotlinMod() {

    override fun onEnable() {
        UIEngine.initialize(this)

        MobManager()
        PlayerBar()
    }
}