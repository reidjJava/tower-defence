package me.reidj.towerdefence.top

import me.reidj.towerdefence.data.Stat


class PlayerTopEntry<V>(stat: Stat, value: V) : TopEntry<Stat, V>(stat, value) {
    var userName: String? = null
    var displayName: String? = null
}