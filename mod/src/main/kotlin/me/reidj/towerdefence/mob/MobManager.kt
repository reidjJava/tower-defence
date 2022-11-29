package me.reidj.towerdefence.mob

import dev.xdark.clientapi.entity.EntityLivingBase
import dev.xdark.clientapi.event.lifecycle.GameLoop
import dev.xdark.feder.NetUtil
import me.reidj.towerdefence.banner.Banners
import ru.cristalix.clientapi.KotlinModHolder.mod
import ru.cristalix.clientapi.readId
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.utility.V3
import java.lang.Math.atan2
import java.util.*

/**
 * @project : tower-defence
 * @author : Рейдж
 **/
class MobManager {

    companion object {
        val mobs = hashMapOf<EntityLivingBase, Mob>()
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

        mod.registerChannel("td:mob-kill") {
            val uuid = UUID.fromString(NetUtil.readUtf8(this))
            val text = NetUtil.readUtf8(this)
            val mob = mobs.keys.find { it.uniqueID == uuid } ?: return@registerChannel

            if (text.isNotEmpty()) {
                Banners.create(uuid, mob.x, mob.y + 2, mob.z, text)
                UIEngine.schedule(2) { Banners.remove(uuid) }
            }

            UIEngine.clientApi.minecraft().world.removeEntity(mob)

            mobs.remove(mob)
        }

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