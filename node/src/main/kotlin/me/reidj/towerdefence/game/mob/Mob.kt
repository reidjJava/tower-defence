package me.reidj.towerdefence.game.mob

import me.func.mod.conversation.ModTransfer
import me.reidj.towerdefence.util.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import java.util.*
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * @project : tower-defence
 * @author : Рейдж
 **/
data class Mob(
    val uuid: UUID,
    var type: EntityType,
    var damage: Double,
    var hp: Double,
    var moveSpeed: Float,
    var timeSpawn: Long,
    val attackSpeed: Int
) {

    private constructor(builder: Builder) : this(
        builder.uuid,
        builder.type,
        builder.damage,
        builder.hp,
        builder.moveSpeed,
        builder.timeSpawn,
        builder.attackSpeed
    )

    class Builder {

        val uuid = UUID.randomUUID()

        val timeSpawn = System.currentTimeMillis()

        var type = EntityType.ZOMBIE

        var hp = 3.0

        var damage = 1.0

        var moveSpeed = 0.01F

        var attackSpeed = 5

        fun type(type: EntityType) = apply { this.type = type }

        fun hp(hp: Double) = apply { this.hp = hp }

        fun damage(damage: Double) = apply { this.damage = damage }

        fun moveSpeed(moveSpeed: Float) = apply { this.moveSpeed = moveSpeed }

        fun attackSpeed(attackSpeed: Int) = apply { this.attackSpeed = attackSpeed }

        fun build() = Mob(this)
    }

    fun show(vararg players: Player) = apply {
        ModTransfer()
            .uuid(uuid)
            .integer(type.typeId.toInt())
            .double(hp)
            .double(moveSpeed.toDouble())
            .long(System.currentTimeMillis())
            .send("td:mob-init", *players)
    }

    fun playHurtAnimation(vararg players: Player) = apply {
        ModTransfer()
            .uuid(uuid)
            .send("td:hurt-animation", *players)
    }

    fun getPosition(mobLocations: List<Location>, bornTimestamp: Long): Location {
        var firstLocation = mobLocations.first()
        var timeTotal = System.currentTimeMillis() - bornTimestamp
        mobLocations.drop(1).forEach { nextLocation ->
            val timeForThisLine =
                sqrt((firstLocation.x - nextLocation.x).pow(2.0) + (firstLocation.z - nextLocation.z).pow(2.0)) / moveSpeed
            if (timeTotal > timeForThisLine) {
                firstLocation = nextLocation
                timeTotal -= timeForThisLine.toLong()
                return@forEach
            }
            val percent = timeTotal / timeForThisLine
            val dX = nextLocation.x - firstLocation.x
            val dZ = nextLocation.z - firstLocation.z
            return Location(
                firstLocation.x + dX * percent,
                firstLocation.y,
                firstLocation.z + dZ * percent,
                firstLocation.yaw
            )
        }
        return firstLocation
    }
}
