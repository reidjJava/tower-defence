package me.reidj.towerdefence

import dev.implario.bukkit.platform.Platforms
import dev.implario.platform.impl.darkpaper.PlatformDarkPaper
import me.func.mod.Anime
import me.func.mod.Kit
import me.func.mod.util.listener
import me.func.world.MapLoader
import me.func.world.WorldMeta
import me.reidj.towerdefence.games5e.Games5e
import me.reidj.towerdefence.listener.JoinHandler
import me.reidj.towerdefence.listener.UnusedHandler
import me.reidj.towerdefence.player.User
import me.reidj.towerdefence.util.ItemUtil
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import ru.cristalix.core.CoreApi
import ru.cristalix.core.lobby.ILobbyService
import ru.cristalix.core.lobby.LobbyService
import ru.cristalix.core.network.ISocketClient
import ru.cristalix.core.permissions.IPermissionService
import ru.cristalix.core.realm.IRealmService
import ru.cristalix.core.realm.RealmId
import ru.cristalix.core.realm.RealmStatus
import java.util.*

/**
 * @project : tower-defence
 * @author : Рейдж
 **/

lateinit var app: App

val hub: RealmId = RealmId.of("HUB-11")

class App : JavaPlugin() {

    lateinit var worldMeta: WorldMeta

    val userMap = hashMapOf<UUID, User>()

    override fun onEnable() {
        app = this

        Platforms.set(PlatformDarkPaper())

        Anime.include(Kit.STANDARD, Kit.EXPERIMENTAL, Kit.NPC, Kit.LOOTBOX)

        worldMeta = MapLoader.load("ThePit", "ThePitReborn")

        CoreApi.get().registerService(ILobbyService::class.java, LobbyService())

        val permissionService = IPermissionService.get()
        IRealmService.get().currentRealmInfo.also {
            it.status = RealmStatus.WAITING_FOR_PLAYERS
            it.maxPlayers = 150
            it.readableName = "§cTowerDefence Lobby #${it.realmId.id}"
            it.groupName = "TowerDefence"
            it.isLobbyServer = true
            it.saveRealm = it.realmId
        }

        ILobbyService.get().also { lobbyService ->
            lobbyService.addSpawnLocation(worldMeta.label("spawn"))
            lobbyService.setAutoFlyCondition { permissionService.isDonator(it.uniqueId) || permissionService.isStaffMember(it.uniqueId) }
            lobbyService.setItem(0, ItemUtil.game, { true }) { Games5e.joinQueue(it) }
            lobbyService.setItem(4, ItemUtil.cosmetic, { true }) {}
            lobbyService.setItem(8, ItemUtil.back, { true }) {}
        }

        config.options().copyDefaults(true)
        saveConfig()

        listener(JoinHandler(), UnusedHandler())

        Games5e.register()
    }

    fun getUser(player: Player) = getUser(player.uniqueId)

    fun getUser(uuid: UUID) = userMap[uuid]
}

fun client(): ISocketClient = ISocketClient.get()