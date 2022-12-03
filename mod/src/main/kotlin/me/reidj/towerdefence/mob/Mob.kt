package me.reidj.towerdefence.mob

import dev.xdark.clientapi.entity.EntityLivingBase
import me.reidj.towerdefence.util.Location
import me.reidj.towerdefence.util.updateNameHealth
import ru.cristalix.uiengine.UIEngine
import java.util.*
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * @project : tower-defence
 * @author : Рейдж
 **/
data class Mob(
    private val uuid: UUID,
    private val id: Int,
    private var hp: Double,
    private val moveSpeed: Float,
    val timeSpawn: Long,
) {

    val entity: EntityLivingBase by lazy {
        val mob =
            UIEngine.clientApi.entityProvider().newEntity(id, UIEngine.clientApi.minecraft().world) as EntityLivingBase
        val firstLocation = MobManager.route.first()
        mob.entityId = (Math.random() * Int.MAX_VALUE).toInt()
        mob.setUniqueId(uuid)
        mob.teleport(firstLocation.x, firstLocation.y, firstLocation.z)
        mob.health = hp.toFloat()
        mob.alwaysRenderNameTag = true
        mob.updateNameHealth()
        UIEngine.clientApi.minecraft().world.spawnEntity(mob)
        mob
    }

    fun kill() {
        UIEngine.clientApi.minecraft().world.removeEntity(entity)
    }

    fun hurtAnimation() {
        entity.performHurtAnimation()
    }

    // TODO не поддерживает разную скорость и пути назад
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
            return Location(firstLocation.x + dX * percent, firstLocation.y, firstLocation.z + dZ * percent, firstLocation.yaw)
        }
        return firstLocation
    }
}
