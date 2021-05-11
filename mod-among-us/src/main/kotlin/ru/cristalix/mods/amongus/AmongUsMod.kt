package ru.cristalix.mods.amongus

import com.google.gson.Gson
import dev.xdark.clientapi.ClientApi
import dev.xdark.clientapi.entry.ModMain
import dev.xdark.clientapi.event.network.PluginMessage
import dev.xdark.clientapi.event.render.GuiOverlayRender
import dev.xdark.feder.NetUtil
import org.lwjgl.opengl.GL11
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.utility.*
import java.lang.Math.PI

class AmongUsMod : ModMain {

    val minimapSize = 150.0

    val gson = Gson()
    val miraHq = MapData(
        90.0, 33.0,
        "minecraft:textures/among_us/yellow.png",
        "minecraft:textures/among_us/mirahq_map.png",
        256.0,
        listOf(
            Marker(89.0, 18.0, "Теплица"),
            Marker(76.0, 43.0, "Офис"),
            Marker(102.0, 43.0, "Управление"),
            Marker(117.0, 102.0, "Столовая"),
            Marker(97.0, 99.0, "Хранилище"),
            Marker(110.0, 124.0, "Балкон"),
            Marker(78.0, 119.0, "Медпункт"),
            Marker(78.0, 98.0, "Связь"),
            Marker(51.0, 109.0, "Раздевалка"),
            Marker(14.0, 103.0, "Площадка"),
            Marker(28.0, 71.0, "Реактор"),
            Marker(51.0, 71.0, "Лаборатория"),
        )
    )

    override fun load(api: ClientApi) {
        UIEngine.initialize(api)
        UIEngine.registerHandler(PluginMessage::class.java) {
            if (channel == "amongus") {
                val json = NetUtil.readUtf8(data, 65536)
                val map = gson.fromJson(json, MapData::class.java)
                createMinimap(map)
            }
        }

        createMinimap(miraHq)
    }

    private fun createMinimap(mapData: MapData) {
        val minimap = rectangle {
            size.x = mapData.textureSize
            size.y = mapData.textureSize

            color = WHITE
            scale = V3(2.5, 2.5, 1.0)

            align = Relative.CENTER
            val mapTexture = mapData.mapTexturePath.replace("minecraft:", "")
            textureLocation = UIEngine.clientApi.resourceManager().getLocation("minecraft", mapTexture)

            addChild(rectangle {
                color = WHITE
                size.x = 2.0
                size.y = 2.0
            })
            addChild(rectangle {
                color = WHITE
                color.green = 0
                size.x = 2.0
                size.y = 2.0
                align = Relative.BOTTOM_RIGHT
            })
//            beforeRender = {
//                GL11.glDepthFunc(GL11.GL_EQUAL)
//            }
//            afterRender = {
//                GL11.glDepthFunc(GL11.GL_LEQUAL)
//            }

            mask = true

            for (marker in mapData.markers) {
                addChild(text {
                    offset = V3(marker.x, marker.y)
                    scale.x /= 2.5
                    scale.y /= 2.5
                    color = Color(255, 255, 255, 0.6)
                    origin = Relative.CENTER
                    content = marker.text
                    shadow = true
                })
            }

        }

        val minimapBounds = rectangle {
            size.x = minimapSize
            size.y = minimapSize
            offset.z = 0.01
            origin = Relative.CENTER
            align = Relative.CENTER
            color = Color(18, 18, 18, 0.7)
            addChild(minimap)
            val playerTexture = mapData.playerTexturePath.replace("minecraft:", "")
            val location = UIEngine.clientApi.resourceManager().getLocation("minecraft", playerTexture)
            addChild(rectangle {
                size.x = 8.0
                size.y = 8.0
                textureLocation = location
                textureFrom = V2(8.0 / 64, 8.0 / 64)
                textureSize = V2(8.0 / 64, 8.0 / 64)
                color = WHITE
                align = Relative.CENTER
                origin = Relative.CENTER
            })
            addChild(rectangle {
                size.x = 8.0
                size.y = 8.0
                scale.x = 1.125
                scale.z = 1.125
                textureLocation = location
                textureFrom = V2(40.0 / 64, 8.0 / 64)
                textureSize = V2(8.0 / 64, 8.0 / 64)
                color = WHITE
                align = Relative.CENTER
                origin = Relative.CENTER
            })
        }

        val minimapContainer = rectangle {
            size.x = minimapSize + 4
            size.y = minimapSize + 4
            color = Color(18, 18, 18, 0.7)
            addChild(minimapBounds)
            origin = Relative.TOP_RIGHT
            align = Relative.TOP_RIGHT
        }

        UIEngine.overlayContext.addChild(minimapContainer)

        UIEngine.registerHandler(GuiOverlayRender::class.java) {
            val player = UIEngine.clientApi.minecraft().player

            val rotation = -player.rotationYaw * PI / 180
            minimap.rotation.degrees = rotation

            for (child in minimap.children) {
                child.rotation.degrees = -rotation
            }

            val partialTicks = UIEngine.clientApi.minecraft().timer.renderPartialTicks

            minimap.origin.x =
                -(player.lastX + (player.x - player.lastX) * partialTicks - mapData.maxX) / mapData.textureSize
            minimap.origin.y =
                -(player.lastZ + (player.z - player.lastZ) * partialTicks - mapData.maxZ) / mapData.textureSize

        }
    }

    override fun unload() {
        UIEngine.uninitialize()
    }
}