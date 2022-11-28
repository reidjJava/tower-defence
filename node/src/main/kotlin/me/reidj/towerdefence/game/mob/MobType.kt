package me.reidj.towerdefence.game.mob

/**
 * @project : tower-defence
 * @author : Рейдж
 **/
enum class MobType(
    val hp: Double,
    val moveSpeed: Float,
    val timeSpawn: Double,
    var wave: IntRange,
) {
    ZOMBIE(2.0, 0.01f, 15.0, 1..5),
    ;
}