package me.reidj.towerdefence

import dev.implario.bukkit.event.on
import dev.implario.bukkit.world.Label
import dev.implario.games5e.node.Game
import dev.implario.games5e.sdk.cristalix.Cristalix
import dev.implario.games5e.sdk.cristalix.MapLoader
import dev.implario.games5e.sdk.cristalix.WorldMeta
import kotlinx.coroutines.future.await
import kotlinx.coroutines.launch
import me.reidj.towerdefence.data.Stat
import me.reidj.towerdefence.player.User
import me.reidj.towerdefence.protocol.LoadUserPackage
import me.reidj.towerdefence.protocol.SaveUserPackage
import me.reidj.towerdefence.util.coroutine
import org.bukkit.entity.Player
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import ru.cristalix.core.realm.RealmId
import java.util.*

/**
 * @project : tower-defence
 * @author : Рейдж
 **/

data class TowerDefenceSettings(val teams: Set<Set<UUID>>)

class TowerDefenceGame(gameId: UUID, settings: TowerDefenceSettings): Game(gameId) {

    val cristalix: Cristalix = Cristalix.connectToCristalix(this, "TWD", "TowerDefence")
    val map: WorldMeta = MapLoader.load(this, "TWD", "prod")

    private val spawn: Label = map.getLabel("spawn")
    private val userMap = hashMapOf<UUID, User>()

    override fun acceptPlayer(event: AsyncPlayerPreLoginEvent) = cristalix.acceptPlayer(event)

    override fun getSpawnLocation(uuid: UUID) = spawn

    init {
        cristalix.setRealmInfoBuilder { it.lobbyFallback(RealmId.of("TWDL-1")) }
        cristalix.updateRealmInfo()

        context.on<AsyncPlayerPreLoginEvent> {
            registerIntent(app).apply {
                coroutine().launch {
                    val statPackage = client().writeAndAwaitResponse<LoadUserPackage>(LoadUserPackage(uniqueId)).await()
                    var stat = statPackage.stat
                    if (stat == null) stat = Stat(uniqueId)
                    userMap[uniqueId] = User(stat)
                    completeIntent(app)
                }
            }
        }
        context.on<PlayerJoinEvent> {
            val user = getUser(player) ?: return@on

            user.player = player
        }
        context.on<PlayerQuitEvent> {
            val uuid = player.uniqueId
            val user = userMap.remove(uuid) ?: return@on
            client().write(SaveUserPackage(uuid, user.stat))
        }
    }

    fun getUser(player: Player) = getUser(player.uniqueId)

    fun getUser(uuid: UUID) = userMap[uuid]
}