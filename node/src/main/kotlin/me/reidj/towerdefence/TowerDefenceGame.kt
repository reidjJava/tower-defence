package me.reidj.towerdefence

import com.destroystokyo.paper.event.player.PlayerAdvancementCriterionGrantEvent
import dev.implario.bukkit.event.on
import dev.implario.bukkit.world.Label
import dev.implario.games5e.node.Game
import dev.implario.games5e.sdk.cristalix.Cristalix
import dev.implario.games5e.sdk.cristalix.MapLoader
import dev.implario.games5e.sdk.cristalix.WorldMeta
import me.func.mod.Anime
import me.func.mod.conversation.ModLoader
import me.func.mod.conversation.ModTransfer
import me.func.protocol.ui.indicator.Indicators
import me.reidj.towerdefence.game.Wave
import me.reidj.towerdefence.player.Session
import org.bukkit.Bukkit
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
import ru.cristalix.core.formatting.Formatting
import ru.cristalix.core.transfer.ITransferService
import ru.cristalix.core.transfer.TransferService
import java.util.*

/**
 * @project : tower-defence
 * @author : Рейдж
 **/

data class TowerDefenceSettings(val teams: Set<Set<UUID>>)

class TowerDefenceGame(gameId: UUID, settings: TowerDefenceSettings) : Game(gameId) {

    val cristalix: Cristalix = Cristalix.connectToCristalix(this, "TWD", "TowerDefence")!!
    val map: WorldMeta = MapLoader.load(this, "TDSIM", "2")

    private val transferService = TransferService(cristalix.client)
    private val spawn: Label = map.getLabel("spawn")

    override fun acceptPlayer(event: AsyncPlayerPreLoginEvent) = cristalix.acceptPlayer(event)

    override fun getSpawnLocation(uuid: UUID) = spawn

    init {
        cristalix.updateRealmInfo()
        cristalix.setRealmInfoBuilder { it.lobbyFallback(app.getLobbyRealm()) }

        context.on<PlayerJoinEvent> {
            val user = app.getUser(player)

            if (user == null) {
                player.sendMessage(Formatting.error("Нам не удалось прогрузить Вашу статистику."))
                after(10) { ITransferService.get().transfer(player.uniqueId, app.getLobbyRealm()) }
                return@on
            }

            after(3) {
                Anime.hideIndicator(
                    player,
                    Indicators.EXP,
                    Indicators.ARMOR,
                    Indicators.HUNGER,
                    Indicators.HEALTH,
                    Indicators.VEHICLE,
                    Indicators.AIR_BAR
                )

                ModLoader.send("mod-bundle-1.0-SNAPSHOT.jar", player)

                map.getLabels("conveyor").sortedBy { it.tag.split(" ")[0].toInt() }.forEach {
                    ModTransfer(
                        it.x,
                        it.y,
                        it.z
                    ).send("td:route-create", player)
                }

                Anime.counting321(player)
                after(3 * 20) {
                    user.session = Session(50.0,50.0, Wave(System.currentTimeMillis(), 1, mutableListOf(), player))
                    user.session!!.wave.start()

                    user.giveMoney(0)
                    user.session!!.setHealth(0.0, player)
                }
            }
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

        after(10) { transferService.transferBatch(settings.teams.flatten(), cristalix.realmId) }
    }

    fun close() {
        transferService.transferBatch(players.map { it.uniqueId }, app.getLobbyRealm())

        after(10) {
            isTerminated = true
            Bukkit.unloadWorld(map.world, false)
            unregisterAll()
        }
    }
}