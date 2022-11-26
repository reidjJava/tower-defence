package me.reidj.towerdefence.protocol

import me.reidj.towerdefence.top.PlayerTopEntry
import ru.cristalix.core.network.CorePackage

data class TopPackage(val topType: String, val limit: Int): CorePackage() {

    lateinit var entries: List<PlayerTopEntry<Any>>
}
