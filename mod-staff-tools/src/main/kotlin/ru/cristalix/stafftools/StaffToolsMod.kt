package ru.cristalix.stafftools

import dev.xdark.clientapi.ClientApi
import dev.xdark.clientapi.entry.ModMain
import dev.xdark.clientapi.event.chat.ChatReceive
import dev.xdark.clientapi.event.chat.ChatSend
import dev.xdark.clientapi.event.input.KeyPress
import org.lwjgl.input.Keyboard
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.UIEngine.clientApi
import ru.cristalix.uiengine.UIEngine.registerHandler
import ru.cristalix.uiengine.element.RectangleElement
import ru.cristalix.uiengine.element.TextElement
import ru.cristalix.uiengine.utility.*


class StaffToolsMod : ModMain {
    private lateinit var element: RectangleElement
    private lateinit var gui: RectangleElement
    private lateinit var start: RectangleElement
    private lateinit var stats: RectangleElement

    override fun load(api: ClientApi) {
        UIEngine.initialize(api)
        UIEngine.registerHandler(KeyPress::class.java) {
            if (key == Keyboard.KEY_PAUSE) unload()
        }
        registerHandler(ChatSend::class.java) {
            if (message == "/close") {
                isCancelled = true
                UIEngine.overlayContext.removeChild(element)
            }
            if (message == "/start") {
                isCancelled = true
                UIEngine.overlayContext.addChild(element)
            }
        }
        start = rectangle {
            align = Relative.TOP_RIGHT
            origin = Relative.TOP_RIGHT
            offset = V3(-1.0, 1.0)
            size = V3(15.0, 15.0)
            color = Color(0, 0, 0, 0.311)
            onHover = { element, hovered ->
                color.alpha = if (hovered) 1.0 else 0.62
            }
            onClick = { e, buttonDown, button ->
                if (buttonDown) {
                    UIEngine.overlayContext.removeChild(e)
                    UIEngine.overlayContext.addChild(element)
                }
            }
            addChild(text {
                align = Relative.TOP_RIGHT
                origin = Relative.TOP_RIGHT
                content = "≡"
                shadow = true
                scale = V3(1.5, 1.5)
                offset = V3(-2.0, 0.0)
            })
        }
        val guiClose = rectangle {
            align = Relative.TOP_RIGHT
            color = Color(191, 64, 64, 0.5)
            size = V3(10.0, 10.0)
            offset = V3(-11.0, 1.0)
            onClick = { e, buttonDown, button ->
                if (buttonDown) {
                    UIEngine.overlayContext.removeChild(gui)
                }
            }
            onHover = { element, hovered ->
                color.alpha = if (hovered) 1.0 else 0.62
            }
            addChild(text {
                align = Relative.CENTER
                origin = Relative.CENTER
                content = "x"
                color = Color(255, 255, 255)
                offset = V3(0.5, -1.0)
            })
        }
        val elementMenu = rectangle {
            align = Relative.BOTTOM_RIGHT
            color = Color(140, 217, 140, 0.5)
            size = V3(15.0, 15.0)
            offset = V3(-48.0, -16.0)
            onHover = { element, hovered ->
                color.alpha = if (hovered) 1.0 else 0.62
            }
            onClick = { e, buttonDown, button ->
                if (buttonDown) {
                    UIEngine.overlayContext.removeChild(e)
                    UIEngine.overlayContext.addChild(gui)
                }
            }
            addChild(text {
                align = Relative.CENTER
                origin = Relative.CENTER
                content = "㝃"
                shadow = true
                color = Color(255, 255, 255)
            })
        }
        val elementClose = rectangle {
            align = Relative.BOTTOM_RIGHT
            color = Color(64, 64, 64, 0.5)
            size = V3(15.0, 15.0)
            offset = V3(-16.0, -16.0)
            onClick = { e, buttonDown, button ->
                if (buttonDown) {
                    UIEngine.overlayContext.removeChild(element, gui)
                    UIEngine.overlayContext.addChild(start)
                }
            }
            onHover = { element, hovered ->
                color.alpha = if (hovered) 1.0 else 0.62
            }
            addChild(text {
                align = Relative.CENTER
                origin = Relative.CENTER
                content = "✖"
                color = Color(255, 255, 255)
            })
        }
        val elementStats = rectangle {
            align = Relative.BOTTOM_RIGHT
            color = Color(0, 102, 255, 0.5)
            size = V3(15.0, 15.0)
            offset = V3(-32.0, -16.0)
            onHover = { element, hovered ->
                color.alpha = if (hovered) 1.0 else 0.62
            }
            onClick = { e, buttonDown, button ->
                if (buttonDown) {
                    UIEngine.overlayContext.removeChild(element, gui)
                    UIEngine.overlayContext.addChild(stats)
                }
            }
            addChild(text {
                align = Relative.CENTER
                origin = Relative.CENTER
                content = "㧵"
                shadow = true
                color = Color(255, 255, 255)
            })
        }
        element = rectangle {
            align = Relative.TOP_RIGHT
            origin = Relative.TOP_RIGHT
            offset = V3(-1.0, 1.0) //блять или нахуй x = вертикаль, y = горизонталь
            color = Color(0, 0, 0, 0.311)
            size = V3(49.0, 55.0) //сука тупой x = горизонталь, y = вертикаль блять
            addChild(rectangle {
                textureLocation = UIEngine.clientApi.clientConnection().getPlayerInfo(clientApi.minecraft().player.name)?.locationSkin
                textureFrom = V2(8.0 / 64, 8.0 / 64)
                textureSize = V2(8.0 / 64, 8.0 / 64)
                color = WHITE
                size.x = 32.0
                size.y = 32.0
                origin = Relative.CENTER
                offset = V3(25.0, 20.0)
            })
            addChild(rectangle {
                textureLocation = UIEngine.clientApi.clientConnection().getPlayerInfo(clientApi.minecraft().player.name)?.locationSkin
                textureFrom = V2(40.0 / 64, 8.0 / 64)
                textureSize = V2(8.0 / 64, 8.0 / 64)
                color = WHITE
                size.x = 32.0
                size.y = 32.0
                scale = V3(1.125, 1.125, 1.0)
                origin = Relative.CENTER
                offset = V3(25.0, 20.0)
            })
            addChild(elementMenu, elementClose, elementStats)
        }
        gui = rectangle {
            align = Relative.TOP_RIGHT
            origin = Relative.TOP_RIGHT
            color = Color(0, 0, 0, 0.311)
            size = V3(250.0, 45.0)
            offset = V3(-85.0, 1.0)
            addChild(guiClose)
        }
        stats = rectangle {
            align = Relative.TOP_RIGHT
            origin = Relative.TOP_RIGHT
            offset = V3(-1.0, 1.0) //блять или нахуй x = вертикаль, y = горизонталь
            color = Color(0, 0, 0, 0.311)
            size = V3(49.0, 70.0) //сука тупой x = вертикаль, y = горизонталь блять
            addChild(guiClose)

        }
        UIEngine.overlayContext.addChild(element)
    }


    override fun unload() {
        UIEngine.uninitialize()
    }
}