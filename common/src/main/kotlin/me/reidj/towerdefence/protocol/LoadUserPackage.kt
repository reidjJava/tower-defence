package me.reidj.towerdefence.protocol

import me.reidj.towerdefence.data.Stat
import ru.cristalix.core.network.CorePackage
import java.util.*

/**
 * @project : tower
 * @author : Рейдж
 **/
data class LoadUserPackage(val uuid: UUID): CorePackage() {
    var stat: Stat? = null
}
