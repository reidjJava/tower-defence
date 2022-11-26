package me.reidj.towerdefence.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPhysicsEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.block.LeavesDecayEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerPickupItemEvent

/**
 * @project : BridgeBuilders
 * @author : Рейдж
 **/
class UnusedHandler : Listener {

    @EventHandler
    fun LeavesDecayEvent.handle() {
        isCancelled = true
    }

    @EventHandler
    fun EntityDamageEvent.handle() {
        isCancelled = true
    }

    @EventHandler
    fun BlockPhysicsEvent.handle() {
        cancel = true
    }

    @EventHandler
    fun FoodLevelChangeEvent.handle() {
        level = 20
    }

    @EventHandler
    fun BlockBreakEvent.handle() {
        cancel = true
    }

    @EventHandler
    fun BlockPlaceEvent.handle() {
        cancel = true
    }

    @EventHandler
    fun PlayerPickupItemEvent.handle() {
        isCancelled = true
    }

    @EventHandler
    fun PlayerDropItemEvent.handle() {
        cancel = true
    }
}