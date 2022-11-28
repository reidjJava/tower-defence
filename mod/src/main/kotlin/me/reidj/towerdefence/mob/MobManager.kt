package me.reidj.towerdefence.mob

import dev.xdark.clientapi.entity.EntityLivingBase
import dev.xdark.clientapi.event.lifecycle.GameLoop
import ru.cristalix.clientapi.KotlinModHolder.mod
import ru.cristalix.clientapi.readId
import ru.cristalix.uiengine.utility.V3
import java.lang.Math.atan2

/**
 * @project : tower-defence
 * @author : Рейдж
 **/
class MobManager {

    private val mobs = hashMapOf<EntityLivingBase, Mob>()

    companion object {
        val route = hashSetOf<V3>()
    }

    init {
        mod.registerChannel("td:route-create") {
            val x = readDouble()
            val y = readDouble()
            val z = readDouble()
            route.add(V3(x, y, z))
        }

        mod.registerChannel("td:mob-init") {
            val uuid = readId()
            val id = readInt()
            val hp = readDouble()
            val moveSpeed = readDouble()
            val timeSpawn = readDouble()
            Mob(uuid, id, hp, moveSpeed.toFloat(), timeSpawn).also { mobs[it.create()] = it }
        }

        var lastTick = System.currentTimeMillis()

        mod.registerHandler<GameLoop> {
            if (mobs.isEmpty() || route.isEmpty()) {
                return@registerHandler
            }
            val now = System.currentTimeMillis().toDouble()
            mobs.forEach { entry ->
                val entity = entry.key
                val mob = entry.value
                val position = mob.getPosition(
                    route,
                    now - mob.timeSpawn,
                    entity.aiMoveSpeed.toDouble()
                )

                val rotation = Math.toDegrees(-atan2(position.x - entity.x, position.z - entity.z)).toFloat()

                entity.rotationYawHead = rotation
                entity.setYaw(rotation)
                entity.teleport(position.x, position.y, position.z)
            }
        }
    }
}