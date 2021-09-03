package dev.implario.games5e.cristalix.mod

import dev.xdark.clientapi.resource.ResourceLocation
import ru.cristalix.uiengine.element.RectangleElement
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.utility.*

inline fun queue(gameName: String, texture: ResourceLocation, builder: QueueElement.() -> Unit) =
    QueueElement(gameName, texture).also(builder)

class QueueElement(gameName: String, texture: ResourceLocation) : Flex() {

    val title = + text {

        origin = CENTER
        align = CENTER
        content = gameName

    }

    val icon = +rectangle {
        size = V3(96.0, 48.0)
        textureFrom.y = 0.125
        textureSize.y = 0.5
        color = WHITE
        textureLocation = texture

        val rect = this
        val overlay = +rectangle {
            size = rect.size
            color = BLACK
            color.alpha = 0.0
            +text {
                color.alpha = 0.0
                content = "Играть"
                align = CENTER
                origin = CENTER
                scale = V3(2.0, 2.0, 2.0)
            }
        }

        onHover {
            animate(0.2, Easings.CUBIC_OUT) {
                overlay.color.alpha = if (hovered) 0.5 else 0.0
                overlay.children[0].color.alpha = if (hovered) 1.0 else 0.0
            }
        }


    }

    val queued = +flex {
        +text {
            content = "В очереди: "
            color.alpha = 0.5
        }
        +text {
            content = "0"
        }
    }


    init {
//        color = Color(255, 255, 255, 0.4)
        flexDirection = FlexDirection.DOWN
        flexSpacing = 2.0
    }

}