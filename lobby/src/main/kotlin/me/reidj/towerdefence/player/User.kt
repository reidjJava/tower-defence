package me.reidj.towerdefence.player

import me.func.mod.util.after
import me.reidj.towerdefence.data.Stat
import org.bukkit.entity.Player

/**
 * @project : tower-defence
 * @author : Рейдж
 **/
class User(stat: Stat) {

    var stat: Stat

    lateinit var player: Player

    var isArmLock = false

    init {
        this.stat = stat
    }

    fun armLock(handler: () -> Unit) {
        if (isArmLock) {
            return
        }
        isArmLock = true
        handler.invoke()
        after(5) { isArmLock = false }
    }
}