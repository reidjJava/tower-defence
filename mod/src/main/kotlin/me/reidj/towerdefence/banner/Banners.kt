package me.reidj.towerdefence.banner

import dev.xdark.clientapi.event.render.RenderTickPre
import dev.xdark.clientapi.opengl.GlStateManager
import ru.cristalix.clientapi.KotlinModHolder.mod
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.Context3D
import ru.cristalix.uiengine.element.RectangleElement
import ru.cristalix.uiengine.utility.*
import java.util.*
import kotlin.collections.set

object Banners {

    val banners = mutableMapOf<UUID, Pair<Banner, Context3D>>()
    private val sizes = mutableMapOf<Pair<UUID, Int>, Double>()

    private fun toBlackText(string: String) = "¨222200" + string.replace(Regex("(§[0-9a-fA-F]|¨......)"), "¨222200")

    init {
        mod.registerHandler<RenderTickPre> {
            val player = UIEngine.clientApi.minecraft().player
            val timer = UIEngine.clientApi.minecraft().timer
            val yaw =
                (player.rotationYaw - player.prevRotationYaw) * timer.renderPartialTicks + player.prevRotationYaw
            val pitch =
                (player.rotationPitch - player.prevRotationPitch) * timer.renderPartialTicks + player.prevRotationPitch

            banners.filter { it.value.first.watchingOnPlayer }.forEach { entry ->
                entry.value.second.rotation = Rotation(-yaw * Math.PI / 180 + Math.PI, 0.0, 1.0, 0.0)
                entry.value.second.children.onEach { it.rotation = Rotation(-pitch * Math.PI / 180, 1.0, 0.0, 0.0) }
            }
        }
    }

    fun text(text: String, banner: Banner, rectangle: RectangleElement) {
        rectangle.children.onEach { UIEngine.overlayContext.removeChild(it) }.clear()

        text.split("\n").forEachIndexed { index, line ->
            val currentSize = sizes[banner.uuid to index] ?: 1.0

            rectangle + text {
                align = TOP
                origin = TOP
                content = toBlackText(line)
                size = V3(banner.weight.toDouble(), banner.height.toDouble())
                color = Color(0, 0, 0, 0.82)
                offset.z = -0.005
                offset.y = -(-3 - index * 12 - 0.75) * currentSize
                offset.x += 0.75 * currentSize
                scale = V3(1.1, 1.1, 1.1)
            }
            rectangle + text {
                align = TOP
                origin = TOP
                content = line
                size = V3(banner.weight.toDouble(), banner.height.toDouble())
                color = WHITE
                offset.z = -0.01
                offset.y = -(-3 - index * 12) * currentSize
                scale = V3(1.1, 1.1, 1.1)
            }
        }
    }

    fun create(uuid: UUID, x: Double, y: Double, z: Double, text: String, toScale: Double = 1.0, depth: Boolean = false): Banner {
        val banner = Banner(
            uuid,
            true,
            mutableMapOf(
                "yaw" to 0,
                "pitch" to 0,
            ), text,
            x,
            y,
            z,
            0,
            0,
            "",
            0,
            0,
            0,
            1.0
        )

        val context = Context3D(V3(banner.x, banner.y, banner.z)).apply {
            if (depth) {
                beforeRender = {
                    GlStateManager.disableDepth()
                }
                afterRender = {
                    GlStateManager.enableDepth()
                }
            }
        }
        banners[uuid] = banner to context

        context.addChild(rectangle {
            if (banner.texture.isNotEmpty()) {
                val parts = banner.texture.split(":")
                textureLocation = UIEngine.clientApi.resourceManager().getLocation(parts[0], parts[1])
            }

            if (text.isNotEmpty())
                text(banner.content, banner, this@rectangle)

            size = V3(banner.weight.toDouble(), banner.height.toDouble())
            color = Color(banner.red, banner.green, banner.blue, banner.opacity)
            context.rotation =
                Rotation(Math.toRadians(banner.motionSettings["yaw"].toString().toDouble()), 0.0, 1.0, 0.0)
            rotation =
                Rotation(Math.toRadians(banner.motionSettings["pitch"].toString().toDouble()), 1.0, 0.0, 0.0)
            val to = V3(toScale, toScale, toScale)
            scale = to

            children.onEach { scale = to }
        })
        UIEngine.worldContexts.add(context)
        return banner
    }

    fun remove(uuid: UUID) {
        banners[uuid]?.let {
            UIEngine.worldContexts.remove(it.second)
            banners.remove(uuid)
        }
    }
}