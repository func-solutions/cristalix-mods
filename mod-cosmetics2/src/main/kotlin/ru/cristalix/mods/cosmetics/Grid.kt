package ru.cristalix.mods.cosmetics

import org.lwjgl.input.Mouse
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.AbstractElement
import ru.cristalix.uiengine.element.RectangleElement
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.utility.CENTER
import ru.cristalix.uiengine.utility.Color
import ru.cristalix.uiengine.utility.Easings

fun grid(vararg children: AbstractElement) = Grid().also { it.addChild(*children) }

inline fun grid(builder: Grid.() -> Unit) = Grid().also(builder)

class Grid(
    cellSizeX: Double = buttonSize,
    cellSizeY: Double = buttonSize,
    borderWidth: Double = margin,
    columns: Int = 3
): RectangleElement() {

    var cellSizeX: Double = cellSizeX
    set(value) {
        field = value
        update()
    }

    var cellSizeY: Double = cellSizeY
    set(value) {
        field = value
        update()
    }

    var borderWidth: Double = borderWidth
    set(value) {
        field = value
        update()
    }

    var columns: Int = columns
    set(value) {
        field = value
        update()
    }

    var scrollPosition = 0.0

    init {
        align = CENTER
        origin = CENTER

        beforeRender {
            val dWheel = Mouse.getDWheel()
            if (dWheel != 0 && lastParent != null) {
                animate(0.1, Easings.SINE_OUT) {
                    scrollPosition += if (dWheel > 0) 50 else -50
                    update()
                }
            }
        }
    }

    override fun addChild(vararg elements: AbstractElement) {
        super.addChild(*elements)
        update()
    }

    fun update() {

        var i = 0

        for (e in children) {
            if (e !is RectangleElement) continue
            val w = (e.size.x / buttonSize).toInt()
            e.offset.x = i % columns * (buttonSize + margin)
            e.offset.y = i / columns * (buttonSize + margin)
            i += w
        }

        size.x = buttonSize * columns + margin * (columns - 1)
        size.y = buttonSize * ((i + 2) / 3) + margin * ((i + 2) / 3 - 1)


        if (lastParent != null) {
            val o = -(lastParent!!.size.y - 40 - size.y) / 2.0
            scrollPosition = if (o <= 0) 0.0 else scrollPosition.coerceIn(-o, o)
            offset.y = scrollPosition
        }

    }


}