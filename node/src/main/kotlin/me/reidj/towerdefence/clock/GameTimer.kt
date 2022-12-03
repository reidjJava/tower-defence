package me.reidj.towerdefence.clock

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.bukkit.scheduler.BukkitRunnable

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/

@FunctionalInterface
interface ClockInject {
    fun run(tick: Int)
}

class GameTimer(private val injects: Set<ClockInject>) : () -> Unit, BukkitRunnable() {

    private var tick = 0

    private val scope = CoroutineScope(Dispatchers.Default)
    private val mutex = Mutex()

    override fun invoke() {
        if (mutex.isLocked) return
        scope.launch {
            mutex.withLock {
                tick++
                injects.forEach { it.run(tick) }
            }
        }
    }

    override fun run() {
        invoke()
    }
}