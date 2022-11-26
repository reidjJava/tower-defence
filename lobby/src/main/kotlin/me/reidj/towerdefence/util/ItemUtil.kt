package me.reidj.towerdefence.util

import dev.implario.bukkit.item.item
import org.bukkit.Material

/**
 * @project : BridgeBuilders
 * @author : Рейдж
 **/
object ItemUtil {

    val game by lazy {
        item {
            type = Material.CLAY_BALL
            text("§aИграть")
            nbt("other", "guild_members")
        }
    }
    val cosmetic by lazy {
        item {
            type = Material.CLAY_BALL
            text("§aПерсонаж")
            nbt("other", "clothes")
            nbt("click", "menu")
        }
    }
    val back by lazy {
        item {
            type = Material.CLAY_BALL
            text("§cВыйти")
            nbt("other", "cancel")
            nbt("click", "leave")
        }
    }
}