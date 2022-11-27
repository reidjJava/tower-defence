package me.reidj.towerdefence.player

import dev.implario.kensuke.KensukeSession
import dev.implario.kensuke.impl.bukkit.IBukkitKensukeUser
import me.func.mod.util.after
import me.reidj.towerdefence.Stat
import org.bukkit.entity.Player
import java.util.*

/**
 * @project : tower-defence
 * @author : Рейдж
 **/
class User(session: KensukeSession, stat: Stat?): IBukkitKensukeUser {
    var stat: Stat

    var isArmLock = false

    private var player: Player? = null
    override fun setPlayer(p0: Player?) {
        player = p0
    }

    override fun getPlayer() = player

    private var session: KensukeSession
    override fun getSession(): KensukeSession {
        return session
    }

    init {
        this.stat = stat ?: Stat(
            UUID.fromString(session.userId),
            0
        )
        this.session = session
    }

    fun armLock(handler: () -> Unit) {
        if (isArmLock) {
            return
        }
        isArmLock = true
        handler.invoke()
        after(5) { isArmLock = false }
    }
}