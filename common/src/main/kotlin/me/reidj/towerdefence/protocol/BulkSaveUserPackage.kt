package me.reidj.towerdefence.protocol

import ru.cristalix.core.network.CorePackage

data class BulkSaveUserPackage(val packages: List<SaveUserPackage>): CorePackage()
