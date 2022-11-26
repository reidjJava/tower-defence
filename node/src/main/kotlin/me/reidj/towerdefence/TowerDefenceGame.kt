package me.reidj.towerdefence

import com.destroystokyo.paper.event.player.PlayerAdvancementCriterionGrantEvent
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
import org.bukkit.event.block.*
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.hanging.HangingBreakByEntityEvent
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.*
import ru.cristalix.core.realm.RealmId
import ru.cristalix.core.transfer.TransferService
import java.util.*

/**
 * @project : tower-defence
 * @author : Рейдж
 **/

data class TowerDefenceSettings(val teams: Set<Set<UUID>>)

class TowerDefenceGame(gameId: UUID, settings: TowerDefenceSettings): Game(gameId) {

    val cristalix: Cristalix = Cristalix.connectToCristalix(this, "TWD", "TowerDefence")
    val map: WorldMeta = MapLoader.load(this, "TDSIM", "2")

    private val transferService = TransferService(cristalix.client)
    private val spawn: Label = map.getLabel("spawn")
    private val userMap = hashMapOf<UUID, User>()

    override fun acceptPlayer(event: AsyncPlayerPreLoginEvent) = cristalix.acceptPlayer(event)

    override fun getSpawnLocation(uuid: UUID) = spawn

    init {
        cristalix.updateRealmInfo()
        cristalix.setRealmInfoBuilder { it.lobbyFallback(RealmId.of("TWDL-1")) }

        context.on<AsyncPlayerPreLoginEvent> {
            registerIntent(app).apply {
                coroutine().launch {
                    val statPackage = client().writeAndAwaitResponse<LoadUserPackage>(LoadUserPackage(uniqueId)).await()
                    var stat = statPackage.stat
                    if (stat == null) stat = Stat(uniqueId, 0)
                    userMap[uniqueId] = User(stat)
                    completeIntent(app)
                }
            }
        }
        context.on<PlayerJoinEvent> {
            after(5) {
                val user = getUser(player) ?: return@after

                user.player = player
            }
        }
        context.on<PlayerQuitEvent> {
            val uuid = player.uniqueId
            val user = userMap.remove(uuid) ?: return@on
            client().write(SaveUserPackage(uuid, user.stat))
        }

        context.on<BlockRedstoneEvent> { newCurrent = oldCurrent }
        context.on<BlockPlaceEvent> { isCancelled = true }
        context.on<CraftItemEvent> { isCancelled = true }
        context.on<PlayerInteractEntityEvent> { isCancelled = true }
        context.on<PlayerDropItemEvent> { isCancelled = true }
        context.on<BlockFadeEvent> { isCancelled = true }
        context.on<BlockSpreadEvent> { isCancelled = true }
        context.on<BlockGrowEvent> { isCancelled = true }
        context.on<BlockPhysicsEvent> { isCancelled = true }
        context.on<BlockFromToEvent> { isCancelled = true }
        context.on<HangingBreakByEntityEvent> { isCancelled = true }
        context.on<BlockBurnEvent> { isCancelled = true }
        context.on<EntityExplodeEvent> { isCancelled = true }
        context.on<PlayerArmorStandManipulateEvent> { isCancelled = true }
        context.on<PlayerAdvancementCriterionGrantEvent> { isCancelled = true }
        context.on<PlayerSwapHandItemsEvent> { isCancelled = true }
        context.on<InventoryClickEvent> { isCancelled = true }
        context.on<InventoryOpenEvent> { isCancelled = inventory.type != InventoryType.PLAYER }
        context.on<FoodLevelChangeEvent> { foodLevel = 20 }
        context.on<EntityDamageByEntityEvent> { isCancelled = true }
        context.on<CreatureSpawnEvent> { isCancelled = true }

        transferService.transferBatch(settings.teams.flatten(), cristalix.realmId)
    }

    fun getUser(player: Player) = getUser(player.uniqueId)

    fun getUser(uuid: UUID) = userMap[uuid]
}