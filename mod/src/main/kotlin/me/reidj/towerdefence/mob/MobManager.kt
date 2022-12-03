package me.reidj.towerdefence.mob

import dev.xdark.clientapi.event.lifecycle.GameLoop
import me.reidj.towerdefence.Location
import me.reidj.towerdefence.banner.Banners
import ru.cristalix.clientapi.KotlinModHolder.mod
import ru.cristalix.clientapi.readId
import ru.cristalix.clientapi.readUtf8
import ru.cristalix.uiengine.UIEngine
import java.util.*

/**
 * @project : tower-defence
 * @author : Рейдж
 **/
class MobManager {

    companion object {
        val mobs = hashMapOf<UUID, Mob>()
        var route = listOf<Location>()
    }

    init {
        mod.registerChannel("td:route-create") {
            route = MutableList(readInt()) {
                Location(readDouble(), readDouble(), readDouble(), readFloat())
            }
            println("route size "+ route.size)
        }

        mod.registerChannel("td:kill-all") {
            mobs.values.forEach(Mob::kill)
            mobs.clear()
        }

        mod.registerChannel("td:mob-init") {
            val uuid = readId()
            val id = readInt()
            val hp = readDouble()
            val moveSpeed = readDouble()
            val timeSpawn = readLong()
            Mob(uuid, id, hp, moveSpeed.toFloat(), timeSpawn).also { mobs[uuid] = it }
        }

        mod.registerChannel("td:mob-kill") {
            val uuid = readId()
            val text = readUtf8()
            val mob = mobs[uuid] ?: return@registerChannel

            if (text.isNotEmpty()) {
                Banners.create(uuid, mob.entity.x, mob.entity.y + 2, mob.entity.z, text)
                UIEngine.schedule(2) { Banners.remove(uuid) }
            }

            mob.kill()

            mobs.remove(uuid)
        }

        mod.registerHandler<GameLoop> {
            if (mobs.isEmpty() || route.isEmpty()) {
                return@registerHandler
            }
            mobs.forEach { entry ->
                val entity = entry.value.entity
                val mob = entry.value
                val position = mob.getPosition(
                    route,
                    mob.timeSpawn
                )

                entity.rotationYawHead = position.yaw
                entity.setYaw(position.yaw)
                entity.teleport(position.x, position.y, position.z)
            }
        }
    }
}