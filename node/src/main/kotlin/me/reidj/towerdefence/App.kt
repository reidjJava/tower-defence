package me.reidj.towerdefence

import com.google.gson.Gson
import dev.implario.bukkit.platform.Platforms
import dev.implario.games5e.node.CoordinatorClient
import dev.implario.games5e.node.DefaultGameNode
import dev.implario.games5e.node.GameCreator
import dev.implario.games5e.node.linker.SessionBukkitLinker
import dev.implario.kensuke.Kensuke
import dev.implario.kensuke.Scope
import dev.implario.kensuke.impl.bukkit.BukkitKensuke
import dev.implario.kensuke.impl.bukkit.BukkitUserManager
import dev.implario.platform.impl.darkpaper.PlatformDarkPaper
import me.func.mod.Anime
import me.func.mod.Kit
import me.func.mod.conversation.ModLoader
import me.reidj.towerdefence.player.User
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import ru.cristalix.core.internal.BukkitInternals
import ru.cristalix.core.internal.FastBukkitInternals
import java.util.*

/**
 * @project : tower-defence
 * @author : Рейдж
 **/

lateinit var app: App

class App : JavaPlugin() {

    val statScope = Scope("tower-defence", Stat::class.java)
    var userManager = BukkitUserManager(
        listOf(statScope),
        { session, context -> User(session, context.getData(statScope)) },
        { user, context -> context.store(statScope, user.stat) }
    )
    lateinit var kensuke: Kensuke

    override fun onEnable() {
        app = this

        Platforms.set(PlatformDarkPaper())

        BukkitInternals.setInstance(FastBukkitInternals())

        val node = DefaultGameNode()
        val gson = Gson()

        node.supportedImagePrefixes.add("TWD")
        node.supportedImagePrefixes.add("tower-defence")
        node.linker = SessionBukkitLinker.link(node)
        node.gameCreator = GameCreator { gameId, _, settings ->
            TowerDefenceGame(gameId, gson.fromJson(settings, TowerDefenceSettings::class.java))
        }

        CoordinatorClient(node).enable()

        // Kensuke moment
        kensuke = BukkitKensuke.setup(app)
        kensuke.addGlobalUserManager(userManager)
        kensuke.globalRealm = "TWD-1"
        userManager.isOptional = true

        Anime.include(Kit.NPC, Kit.EXPERIMENTAL, Kit.STANDARD, Kit.HEALTH_BAR, Kit.LOOTBOX)

        ModLoader.loadAll("mods")
    }

    fun getUser(player: Player) = getUser(player.uniqueId)

    fun getUser(uuid: UUID) = userManager.getUser(uuid)
}