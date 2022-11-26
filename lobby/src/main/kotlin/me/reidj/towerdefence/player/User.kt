package me.reidj.towerdefence.player

import me.reidj.towerdefence.data.Stat
import org.bukkit.entity.Player

/**
 * @project : tower-defence
 * @author : Рейдж
 **/
class User(stat: Stat) {

    var stat: Stat

    lateinit var player: Player

    init {
        this.stat = stat
    }
}