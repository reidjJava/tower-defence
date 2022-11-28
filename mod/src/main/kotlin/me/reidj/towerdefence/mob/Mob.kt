package me.reidj.towerdefence.mob

import dev.xdark.clientapi.entity.EntityLivingBase
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.utility.V3
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
    val timeSpawn: Double,
) {

    fun create(): EntityLivingBase {
        val mob =
            UIEngine.clientApi.entityProvider().newEntity(id, UIEngine.clientApi.minecraft().world) as EntityLivingBase
        val firstLocation = MobManager.route.first()
        mob.entityId = (Math.random() * Int.MAX_VALUE).toInt()
        mob.setUniqueId(uuid)
        mob.teleport(firstLocation.x, firstLocation.y, firstLocation.z)
        mob.health = hp.toFloat()
        mob.alwaysRenderNameTag = true
        mob.aiMoveSpeed = moveSpeed
        UIEngine.clientApi.minecraft().world.spawnEntity(mob)
        return mob
    }

    fun getPosition(mobLocations: HashSet<V3>, timeFromSpawn: Double, velocity: Double): V3 {
        var firstLocation = mobLocations.first()
        var timeTotal = timeFromSpawn
        mobLocations.drop(1).forEach { nextLocation ->
            val timeForThisLine =
                sqrt((firstLocation.x - nextLocation.x).pow(2.0) + (firstLocation.z - nextLocation.z).pow(2.0)) / velocity
            if (timeTotal > timeForThisLine) {
                firstLocation = nextLocation
                timeTotal -= timeForThisLine
                return@forEach
            }
            val percent = timeTotal / timeForThisLine
            val dX = nextLocation.x - firstLocation.x
            val dZ = nextLocation.z - firstLocation.z
            return V3(firstLocation.x + dX * percent, firstLocation.y, firstLocation.z + dZ * percent)
        }
        return firstLocation
    }
}
