package ru.cristalix.mods.socials

import dev.xdark.clientapi.resource.ResourceLocation
import ru.cristalix.socials.data.Prefix
import ru.cristalix.uiengine.element.RectangleElement
import ru.cristalix.uiengine.utility.WHITE

inline fun rankPrefix(prefix: Prefix? = null, dark: Boolean = false, builder: RankComponent.() -> Unit): RankComponent =
    RankComponent().also(builder).also {
        if (prefix != null) it.prefix = prefix
        if (dark) it.dark = true
    }


class RankComponent : RectangleElement() {

    var prefix: Prefix? = null
        set(value) {
            field = value
            if (value != null) {
                textureFrom = value.getCoords()
                textureSize.x = value.width.toDouble() / 128.0
                if (dark) {
                    textureFrom.x += 64.0 / 128.0
                    color.alpha = 0.5
                } else {
                    color.alpha = 1.0
                }
                size.x = value.width.toDouble()
            } else {
                size.x = 0.0
            }
        }

    var dark: Boolean = false
        set(value) {
            field = value
            prefix = prefix
        }

    init {
        size.y = 9.0
        textureSize.y = 9.0 / 128.0
        color = WHITE
        textureLocation = ResourceLocation.of("delfikpro", "prefixes")
    }

}