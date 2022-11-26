package me.reidj.towerdefence.protocol

import me.reidj.towerdefence.data.Stat
import ru.cristalix.core.network.CorePackage
import java.util.*

/**
 * @project : tower
 * @author : Рейдж
 **/
data class SaveUserPackage(val uuid: UUID, val stat: Stat): CorePackage()