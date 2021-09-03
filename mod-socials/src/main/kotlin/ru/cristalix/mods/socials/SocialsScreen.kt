package ru.cristalix.mods.socials

import dev.xdark.clientapi.resource.ResourceLocation
import dev.xdark.clientapi.text.ClickEvent
import dev.xdark.clientapi.text.Style
import dev.xdark.clientapi.text.Text
import implario.humanize.Humanize
import org.lwjgl.input.Mouse
import ru.cristalix.socials.data.friendsCommand
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.Context2D
import ru.cristalix.uiengine.element.RectangleElement
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.utility.*
import java.util.*


class SocialsScreen : Context2D(V3()) {

    var scrollPosition = 0.0

    val notifications = +flex {

        flexDirection = FlexDirection.DOWN
        flexSpacing = 11.0


    }

    val body = +rectangle {
        size.x = 200.0
        size.y = 20000.0
//        beforeTransform {
//            size.y = this@SocialsScreen.size.y
//        }
        color.alpha = 0.86
        align = TOP_RIGHT
        origin = TOP_LEFT
        onClick {
            closeMenu()
        }
        beforeRender {
            if (hovered) {
                val dWheel = Mouse.getDWheel()
                if (dWheel != 0 && lastParent != null) {
                    animate(0.1, Easings.SINE_OUT) {
                        scrollPosition += if (dWheel > 0) -50 else 50
                        val o = -(lastParent!!.size.y - friendList.size.y - friendList.offset.y)
                        scrollPosition = if (o <= 0) 0.0 else scrollPosition.coerceIn(0.0, o)
                        offset.y = -scrollPosition
                    }
                }
            }
        }

        +rectangle {
            size = V3(200.0, 30.0)
            color = hex("2a66bd", 0.26)
            offset.y = 18.0 + 32.0 + 12.0

            +rectangle {
                size = V3(16.0, 16.0)
                offset.x = 10.0
                origin = LEFT
                align = LEFT
                textureLocation = ResourceLocation.of("delfikpro", "friends16")
                color = WHITE
                color.alpha = 0.5
            }

            +rectangle {
                offset.x = -6.0
                align = RIGHT
                origin = RIGHT
                size = V3(75.0, 17.0)
                +text {
                    content = "Добавить"
                    align = CENTER
                    origin = CENTER
                }
                onClick {
                    if (!down) {
                        val text = Text.of("")
                        text.style = Style.create().setClickEvent(
                            ClickEvent.of(ClickEvent.Action.SUGGEST_COMMAND, "$friendsCommand add ")
                        )
                        UIEngine.clientApi.minecraft().currentScreen().handleTextClick(text)
                    }
                }

                color = hex("2a66bd", 0.28)
                onHover {
                    animate(0.2) {
                        color.alpha = if (this@onHover.hovered) 1.0 else 0.28
                    }
                }
            }

            +text {
                origin = LEFT
                align = LEFT
                content = ""
                beforeTransform {
                    content = "" + friendList.children.size + " " + Humanize.plurals("друг", "друга", "друзей", friendList.children.size)
                }
                offset.x = 72.0 / 2
            }
        }


    }

    var shown = false

    val toggleButton = body + rectangle {

        +rectangle {
            size = V3(16.0, 16.0)
            origin = CENTER
            align = CENTER
            textureLocation = ResourceLocation.of("delfikpro", "friends16")
            color = WHITE
        }

        onClick {
            if (!down) {
                shown = !shown
                animate(0.5, Easings.QUINT_OUT) {
                    body.origin.x = if (shown) 1.0 else 0.0
                }
            }
        }



        onHover {
            if (!shown) {
                animate(0.1, Easings.QUART_OUT) {
                    color = if (this@onHover.hovered) Color(0x2A, 0x66, 0xBD, 1.0) else
                        Color(0, 0, 0, 0.5)
                }
            }
        }

//        color = Color(0x2A, 0x66, 0xBD, 1.0)
        color.alpha = 0.5
        size = V3(32.0, 32.0)

        align = TOP_RIGHT
        origin = TOP_RIGHT
        offset = V3(-12 - body.size.x, 12.0)
    }

    val friendTab = body + rectangle {
        color = Color(0x2A, 0x66, 0xBD, 1.0)
        size = V3(100.0, 18.0)
        offset.y = 32.0
        +text {
            content = "[ Друзья ]"
            align = CENTER
            origin = CENTER
        }
    }

    val partyTab = body + rectangle {
        color = hex("2a66bd", 0.5)
        size = V3(100.0, 18.0)
        offset.y = 32.0
        offset.x = 100.0

        +text {
            color.alpha = 0.5
            content = "[ ??? ]"
            align = CENTER
            origin = CENTER
        }
    }

    val skin = body + playerHead(UIEngine.clientApi.minecraft().player?.uniqueID ?: UUID(0, 0)) {
        scale = V3(2.5, 2.5, 2.5)
        offset = V3(6.0, 6.0)
    }

    val staffGroup = rankPrefix { }

    val donateGroup = rankPrefix { }

    val nameText = text {
        content = UIEngine.clientApi.minecraft().player?.name ?: ""
    }
    val nameContainer = body + flex {
        offset = V3(36.0, 6.0)
        flexDirection = FlexDirection.RIGHT
        flexSpacing = 4.0
        addChild(nameText, staffGroup, donateGroup)
    }

    val currentRealm = body + text {
        content = ""
        offset = V3(36.0, 16.0)
    }

    val friendList: Flex = body + flex {
        offset.y = 32.0 + 18.0 + 12.0 + 30.0 + 9.0
        flexDirection = FlexDirection.DOWN
    }

    var currentMenu: RectangleElement? = null

    init {
        onClick {
            closeMenu()
        }
    }

    fun closeMenu(): RectangleElement? {
        val menu = currentMenu
        if (menu != null) {
            menu.enabled = false
            currentMenu = null
        }
        return menu
    }


}