package me.reidj.towerdefence.player

import me.func.mod.conversation.ModTransfer
import me.reidj.towerdefence.game.Wave
import org.bukkit.entity.Player

/**
 * @project : tower-defence
 * @author : Рейдж
 **/
data class Session(val maxHealth: Double, var health: Double, val wave: Wave) {

    fun setHealth(health: Double, player: Player) {
        this.health += health
        ModTransfer(this.health, maxHealth).send("td:health-update", player)
    }
}
