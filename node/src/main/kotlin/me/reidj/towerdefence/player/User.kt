package me.reidj.towerdefence.player

import dev.implario.kensuke.KensukeSession
import dev.implario.kensuke.impl.bukkit.IBukkitKensukeUser
import me.func.mod.conversation.ModTransfer
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

    var money: Int = 0

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
        ModTransfer(this.money).send("td:money-update", player)
    }
}