package me.reidj.towerdefence.player

import me.reidj.towerdefence.util.Formatter
import ru.cristalix.clientapi.KotlinModHolder.mod
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.utility.*

/**
 * @project : tower-defence
 * @author : Рейдж
 **/
class PlayerBar {

    private val moneyContent = text {
        origin = CENTER
        align = CENTER
        shadow = true
        content = "Загрузка..."
    }
    private val moneyBox = carved {
        origin = BOTTOM
        align = BOTTOM
        offset = V3(46.0, -24.0)
        size = V3(90.0, 13.0)
        color = Color(0, 0, 0, 0.62)
        enabled = false
        +moneyContent
    }
    private val healthContent = text {
        origin = CENTER
        align = CENTER
        shadow = true
        content = "Загрузка..."
    }
    private val healthBox = carved {
        origin = BOTTOM
        align = BOTTOM
        offset = V3(-46.0, -24.0)
        size = V3(90.0, 13.0)
        color = Color(0, 0, 0, 0.62)
        enabled = false
        +healthContent
    }

    init {
        UIEngine.overlayContext.addChild(healthBox, moneyBox)

        mod.registerChannel("td:health-update") {
            if (!healthBox.enabled) {
                healthBox.enabled = true
            }
            val health = Formatter.toFormat(readDouble())
            val maxHealth = Formatter.toFormat(readDouble())
            healthContent.content = "\uE19A $health из $maxHealth"
        }

        mod.registerChannel("td:money-update") {
            if (!moneyBox.enabled) {
                moneyBox.enabled = true
            }
            val money = readInt()
            moneyContent.content = "\uE03C $money"
        }
    }
}