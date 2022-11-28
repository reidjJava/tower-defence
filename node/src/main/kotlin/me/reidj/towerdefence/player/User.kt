package me.reidj.towerdefence.player

import dev.implario.kensuke.KensukeSession
import dev.implario.kensuke.impl.bukkit.IBukkitKensukeUser
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
    private var money: Long = 0

    var session: Session? = null

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

    fun giveMoney(money: Long) {
        this.money += money
    }
}