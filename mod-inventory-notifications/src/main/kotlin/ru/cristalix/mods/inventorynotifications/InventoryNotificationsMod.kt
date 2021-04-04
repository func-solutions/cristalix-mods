package ru.cristalix.mods.inventorynotifications

import dev.xdark.clientapi.ClientApi
import dev.xdark.clientapi.entry.ModMain
import dev.xdark.clientapi.event.input.KeyPress
import dev.xdark.clientapi.event.network.PluginMessage
import dev.xdark.clientapi.resource.ResourceLocation
import dev.xdark.feder.NetUtil
import org.lwjgl.input.Keyboard
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.RectangleElement
import ru.cristalix.uiengine.utility.*

class InventoryNotificationsMod: ModMain {

    var elem: RectangleElement? = null

    override fun load(clientApi: ClientApi) {
        UIEngine.initialize(clientApi)

        render(93.0, -110.0, 120.0, 40.0, "§e§lСкидки до 60%\n" +
                "Проверь ежедневное\n" +
                "предложение")

        UIEngine.registerHandler(PluginMessage::class.java) {
            if (channel == "invnotif") {
                val b = data.readBoolean()
                if (!b) {
                    elem?.let {UIEngine.postOverlayContext.removeChild(it)}
                    return@registerHandler
                }
                val x = data.readDouble()
                val y = data.readDouble()
                val width = data.readDouble()
                val height = data.readDouble()
                val text = NetUtil.readUtf8(data)
                render(x, y, width, height, text)
            }
        }


        UIEngine.registerHandler(KeyPress::class.java) {
            if (key == Keyboard.KEY_J) unload()
        }
    }

    fun render(x: Double, y: Double, width: Double, height: Double, text: String) {

        val lines = text.split("\n")

        UIEngine.postOverlayContext.addChild(rectangle {
            align = Relative.CENTER
            offset = V3(x, y)
            size = V3(width - 3, height - 3)
            color = WHITE
            val textureLoc = ResourceLocation.of("minecraft", "textures/gui/demo_background.png")
            textureLocation = textureLoc
            textureSize = V2((width - 3) / 256.0, (height - 3) / 256.0)
            addChild(rectangle {
                align = Relative.BOTTOM_LEFT
                size = V3(width - 3, 3.0)
                textureLocation = textureLoc
                textureFrom = V2(0.0, 163.0 / 256.0)
                textureSize = V2((width - 3) / 256.0, 3.0 / 256.0)
                color = WHITE
            })
            addChild(rectangle {
                align = Relative.TOP_RIGHT
                size = V3(3.0, height - 3.0)
                textureLocation = textureLoc
                textureFrom = V2(245.0 / 256.0, 0.0)
                textureSize = V2(3.0 / 256.0, (height - 3.0) / 256.0)
                color = WHITE
            })
            addChild(rectangle {
                align = Relative.BOTTOM_RIGHT
                size = V3(3.0, 3.0)
                textureLocation = textureLoc
                textureFrom = V2(245.0 / 256.0, 163.0 / 256.0)
                textureSize = V2(3.0 / 256.0, 3.0 / 256.0)
                color = WHITE
            })

            lines.forEachIndexed { i, text ->
                addChild(text {
                    offset.x = 5.0
                    offset.y = 5.0 + i * 10.0
                    color = BLACK
                    content = text
                })
            }

        })
    }

    override fun unload() {
        UIEngine.uninitialize()
    }

}