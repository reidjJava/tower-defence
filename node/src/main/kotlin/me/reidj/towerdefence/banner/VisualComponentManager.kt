package me.reidj.towerdefence.banner

import me.func.mod.reactive.ReactiveProgress
import me.func.protocol.data.color.GlowColor
import me.func.protocol.data.element.Banner
import me.reidj.towerdefence.TowerDefenceGame

/**
 * @project : tower-defence
 * @author : Рейдж
 **/
class VisualComponentManager(game: TowerDefenceGame) {

    companion object {
        lateinit var healthBanner: Banner
        lateinit var waveProgress: ReactiveProgress
    }

    init {
        healthBanner = BannerUtil.create(game.map.getLabel("banner-health"), game.updateHealth())
        waveProgress = ReactiveProgress.builder()
            .location(game.map.getLabel("banner-state").also {
                it.x += 0.5
                it.y += 2.0
                it.z += 0.5
            })
            .scale(2.0)
            .text("${game.wave.level} волна")
            .color(GlowColor.BLUE)
            .build()
    }
}