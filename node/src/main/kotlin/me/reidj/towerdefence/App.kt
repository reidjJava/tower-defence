package me.reidj.towerdefence

import com.google.gson.Gson
import dev.implario.bukkit.platform.Platforms
import dev.implario.games5e.node.CoordinatorClient
import dev.implario.games5e.node.DefaultGameNode
import dev.implario.games5e.node.GameCreator
import dev.implario.games5e.node.linker.SessionBukkitLinker
import dev.implario.platform.impl.darkpaper.PlatformDarkPaper
import me.func.mod.Anime
import me.func.mod.Kit
import me.func.mod.conversation.ModLoader
import org.bukkit.plugin.java.JavaPlugin
import ru.cristalix.core.internal.BukkitInternals
import ru.cristalix.core.internal.FastBukkitInternals
import ru.cristalix.core.network.ISocketClient

/**
 * @project : tower-defence
 * @author : Рейдж
 **/

lateinit var app: App

class App : JavaPlugin() {

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

        Anime.include(Kit.NPC, Kit.EXPERIMENTAL, Kit.STANDARD, Kit.HEALTH_BAR, Kit.LOOTBOX)

        ModLoader.loadAll("mods")
    }
}

fun client(): ISocketClient = ISocketClient.get()