package dev.implario.games5e.cristalix.mod

import dev.xdark.clientapi.item.ItemTools
import dev.xdark.clientapi.nbt.NBTTagCompound
import dev.xdark.clientapi.resource.ResourceLocation
import ru.cristalix.uiengine.utility.*

class MapElement(mapName: String, author: String, info: String, texture: ResourceLocation): Flex() {

    init {
        flexDirection = FlexDirection.DOWN
        flexSpacing = 2.0
    }

    val picture = +rectangle {
        color = WHITE
        textureLocation = texture
        textureSize.y = 0.5
        size = V3(120.0, 60.0)
    }

    val name = +text {
        content = mapName
        align = TOP
        origin = TOP
    }

    val author = +text {
        content = "by $author"
        align = TOP
        origin = TOP
        color.alpha = 0.4
    }

    val info = +text {
        content = info
    }

}
