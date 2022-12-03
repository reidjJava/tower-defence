package me.reidj.towerdefence.util

import java.text.DecimalFormat

/**
 * @project : tower-defence
 * @author : Рейдж
 **/
object Formatter {

    private val doubleFormat = DecimalFormat("#,###.##")

    fun toFormat(double: Double): String = doubleFormat.format(double)
}