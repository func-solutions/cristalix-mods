package ru.cristalix.mods.socials

import com.mojang.authlib.minecraft.MinecraftProfileTexture
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.Parent
import ru.cristalix.uiengine.element.RectangleElement
import ru.cristalix.uiengine.utility.*
import java.util.*

inline fun playerHead(uuid: UUID, builder: PlayerHead.() -> Unit) = PlayerHead(uuid).also(builder)

class PlayerHead(uuid: UUID) : RectangleElement() {

    var uuid: UUID = uuid
    set(value) {
        field = value
        reload()
    }

    val head0 = rectangle {
        size = V3(8.0, 8.0)
        origin = CENTER
        align = CENTER
        textureFrom = V2(8.0 / 64.0, 8.0 / 64.0)
        textureSize = V2(8.0 / 64.0, 8.0 / 64.0)
        color = Color(255, 255, 255, 1.0)
        this@PlayerHead.addChild(this)
    }

    val head1 = rectangle {
        size = V3(8.0, 8.0)
        scale = V3(1.125, 1.125, 1.0)
        textureFrom = V2(40.0 / 64.0, 8.0 / 64.0)
        textureSize = V2(8.0 / 64.0, 8.0 / 64.0)
        origin = CENTER
        align = CENTER
        color = Color(255, 255, 255, 1.0)
        this@PlayerHead.addChild(this)
    }

    init {

        size = V3(8.0, 8.0)
        this.uuid = uuid

    }

    fun reload() {

        head0.textureLocation?.let {
            UIEngine.clientApi.renderEngine().deleteTexture(it)
        }
        head1.textureLocation?.let {
            UIEngine.clientApi.renderEngine().deleteTexture(it)
        }

        head0.textureLocation = null
        head1.textureLocation = null

        UIEngine.clientApi.skinManager().loadSkin(
            MinecraftProfileTexture(
                "https://webdata.c7x.dev/textures/skin/$uuid",
                hashMapOf()
            ), MinecraftProfileTexture.Type.SKIN
        ) { _, location, _ ->
            head0.textureLocation = location
            head1.textureLocation = location
        }
    }

}