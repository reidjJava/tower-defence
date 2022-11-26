package me.reidj.towerdefence.listener

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.launch
import me.func.mod.util.after
import me.reidj.towerdefence.app
import me.reidj.towerdefence.client
import me.reidj.towerdefence.data.Stat
import me.reidj.towerdefence.games5e.Games5e
import me.reidj.towerdefence.hub
import me.reidj.towerdefence.player.User
import me.reidj.towerdefence.protocol.LoadUserPackage
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
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
    fun AsyncPlayerPreLoginEvent.handle() = registerIntent(app).apply {
        CoroutineScope(Dispatchers.IO).launch {
            val statPackage = client().writeAndAwaitResponse<LoadUserPackage>(LoadUserPackage(uniqueId)).await()
            var stat = statPackage.stat
            if (stat == null)
                stat = Stat(uniqueId,)
            app.userMap.putIfAbsent(uniqueId, User(stat))
            completeIntent(app)
        }
    }

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
            user.player = player
        }
    }

    @EventHandler
    fun PlayerQuitEvent.handle() {
        Games5e.leaveQueue(player)
    }
}