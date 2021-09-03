package ru.cristalix.mods.cosmetics

import ru.cristalix.uiengine.element.ItemElement
import ru.cristalix.uiengine.element.RectangleElement
import ru.cristalix.uiengine.element.TextElement
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.utility.*

inline fun button(setup: ButtonElement.() -> Unit) = ButtonElement().also(setup)

const val buttonSize = 60.0

class ButtonElement : RectangleElement() {

    val icon: ItemElement = item {
        origin = CENTER
        align = CENTER
        offset.y = -4.0
        scale = V3(2.0, 2.0, 1.0)
    }

    val title: TextElement = text {
        origin = BOTTOM
        align = BOTTOM
        offset.y = -2.0
    }

    init {
        size.x = buttonSize
        size.y = buttonSize
        color = BUTTON_BLUE
        color.alpha = 0.28
        addChild(icon, title)
        onHover {
            animate(0.3, Easings.QUART_OUT) {
                color.alpha = if (hovered) 1.0 else 0.28
            }
        }
    }


}