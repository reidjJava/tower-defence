package me.reidj.towerdefence.listener

import me.func.mod.Anime
import me.func.mod.util.after
import me.func.protocol.ui.indicator.Indicators
import me.reidj.towerdefence.app
import me.reidj.towerdefence.games5e.Games5e
import me.reidj.towerdefence.hub
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import ru.cristalix.core.formatting.Formatting
import ru.cristalix.core.transfer.ITransferService

/**
 * @project : tower-defence
 * @author : Рейдж
 **/
class JoinHandler : Listener {

    @EventHandler
    fun PlayerJoinEvent.handle() {
        val user = app.getUser(player)
        val uuid = player.uniqueId

        if (user == null) {
            player.sendMessage(Formatting.error("Нам не удалось прогрузить Вашу статистику."))
            after(10) { ITransferService.get().transfer(uuid, hub) }
            return
        }

        after(5) {
            Anime.hideIndicator(player, Indicators.EXP, Indicators.HEALTH, Indicators.HUNGER)

            user.player = player
        }
    }

    @EventHandler
    fun PlayerQuitEvent.handle() {
        Games5e.leaveQueue(player)
    }
}