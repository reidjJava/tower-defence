package me.reidj.towerdefence.npc

import me.func.mod.world.Npc
import me.func.mod.world.Npc.location
import me.func.mod.world.Npc.onClick
import me.func.protocol.world.npc.NpcBehaviour
import me.reidj.towerdefence.app

/**
 * @project : tower-defence
 * @author : Рейдж
 **/
class NpcManager {

    init {
        app.worldMeta.labels("simplenpc").forEach {
            val data = app.config.getConfigurationSection("npc." + it.tag)
            val skin = data.getString("skin")
            Npc.npc {
                name = data.getString("title")
                behaviour = NpcBehaviour.STARE_AT_PLAYER
                pitch = it.pitch
                yaw = it.yaw
                skinUrl = "https://webdata.c7x.dev/textures/skin/$skin"
                skinDigest = skin
                location(it.also {
                    it.x += 0.5
                    it.z += 0.5
                })
                onClick { event ->
                    val player = event.player
                    val user = app.getUser(player) ?: return@onClick
                    user.armLock { player.performCommand(data.getString("command")) }
                }
            }
        }
    }
}