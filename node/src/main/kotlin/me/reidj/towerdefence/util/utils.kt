package me.reidj.towerdefence.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

/**
 * @project : tower-defence
 * @author : Рейдж
 **/

fun coroutine() = CoroutineScope(Dispatchers.IO)