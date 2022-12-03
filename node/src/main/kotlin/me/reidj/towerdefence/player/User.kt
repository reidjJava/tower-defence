package me.reidj.towerdefence.player

import dev.implario.kensuke.KensukeSession
import dev.implario.kensuke.impl.bukkit.IBukkitKensukeUser
import me.func.mod.reactive.ReactivePanel
import me.func.protocol.data.emoji.Emoji
import me.reidj.towerdefence.Stat
import org.bukkit.entity.Player
import java.util.*

/**
 * @project : tower-defence
 * @author : Рейдж
 **/
class User(kensukeSession: KensukeSession, stat: Stat?) : IBukkitKensukeUser {

    var stat: Stat

    private var player: Player? = null

    val moneyPanel = ReactivePanel.builder().build()

    var money: Int = 0
        set(value) {
            field = value
            moneyPanel.text = Emoji.COIN + " " + field
        }

    override fun setPlayer(p0: Player?) {
        player = p0
    }

    override fun getPlayer() = player

    private var kensukeSession: KensukeSession
    override fun getSession(): KensukeSession {
        return kensukeSession
    }

    init {
        this.stat = stat ?: Stat(
            UUID.fromString(kensukeSession.userId),
            0,
        )
        this.kensukeSession = kensukeSession
    }

    fun giveMoney(money: Int) {
        this.money += money
    }
}