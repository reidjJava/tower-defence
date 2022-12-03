package me.reidj.towerdefence.banner

import me.func.mod.world.Banners
import me.func.mod.world.Banners.location
import org.bukkit.Location

/**
 * @project : tower-defence
 * @author : Рейдж
 **/
object BannerUtil {

    fun create(location: Location, content: String) {
        Banners.new {
            watchingOnPlayer = true
            this.content = content
            opacity = 0.0
            location(location.clone().also {
                it.x += 0.5
                it.y += 2.0
                it.z += 0.5
            })
        }
    }
}