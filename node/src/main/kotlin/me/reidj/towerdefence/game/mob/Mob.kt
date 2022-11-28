package me.reidj.towerdefence.game.mob

import me.func.mod.conversation.ModTransfer
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import java.util.*

/**
 * @project : tower-defence
 * @author : Рейдж
 **/
data class Mob(
    val uuid: UUID = UUID.randomUUID(),
    var type: EntityType = EntityType.ZOMBIE,
    var hp: Double = 1.0,
    var moveSpeed: Float = 0.01f
) {
    constructor(init: Mob.() -> Unit) : this() {
        this.init()
    }

    fun create(player: Player) = apply {
        ModTransfer()
            .uuid(uuid)
            .integer(type.typeId.toInt())
            .double(hp)
            .double(moveSpeed.toDouble())
            .double(0.01)
            .send("td:mob-init", player)
    }
}
