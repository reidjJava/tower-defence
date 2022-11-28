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
import me.func.mod.ui.Glow
import me.func.mod.util.after
import me.func.protocol.data.color.GlowColor
import me.func.protocol.data.status.EndStatus
import me.reidj.towerdefence.clock.GameTimer
import me.reidj.towerdefence.game.WaveManager
import me.reidj.towerdefence.player.User
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import ru.cristalix.core.internal.BukkitInternals
import ru.cristalix.core.internal.FastBukkitInternals
import ru.cristalix.core.realm.RealmId
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

        Bukkit.getScheduler().runTaskTimerAsynchronously(this, GameTimer(setOf(WaveManager())), 0, 1)

        Anime.createReader("td:playerhit") { player, buffer ->
            node.runningGames.values.filter { player in it.players }.forEach { game ->
                val user = app.getUser(player) ?: return@createReader
                val pair = buffer.toString(Charsets.UTF_8)
                val session = user.session!!
                var health = session.health
                val towerDefenceGame = game as TowerDefenceGame

                Glow.animate(player, .5, GlowColor.RED)
                health -= pair.toDouble()

                if (health <= 0) {
                    Anime.showEnding(player, EndStatus.LOSE, "Волн пройдено:", "${session.wave.level}")
                    after(5 * 20) { towerDefenceGame.close() }
                }
            }
        }
    }

    fun getLobbyRealm() = RealmId.of("TWDL-1")

    fun getUser(player: Player) = getUser(player.uniqueId)

    fun getUser(uuid: UUID) = userManager.getUser(uuid)
}