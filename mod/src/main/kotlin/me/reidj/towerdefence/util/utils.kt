package me.reidj.towerdefence.util

import dev.xdark.clientapi.entity.EntityLivingBase

/**
 * @project : tower-defence
 * @author : Рейдж
 **/

fun EntityLivingBase.updateNameHealth() {
    customNameTag = "§4${Formatter.toFormat(health.toDouble())} §f\uE19A"
}