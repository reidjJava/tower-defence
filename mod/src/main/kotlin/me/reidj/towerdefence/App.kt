package me.reidj.towerdefence

import dev.xdark.clientapi.event.render.PlayerListRender
import me.reidj.towerdefence.banner.Banners
import me.reidj.towerdefence.mob.MobManager
import me.reidj.towerdefence.player.PlayerBar
import me.reidj.towerdefence.player.TimeBar
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
        TimeBar()

        Banners
    }
}