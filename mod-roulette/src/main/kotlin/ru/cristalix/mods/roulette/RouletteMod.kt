package ru.cristalix.mods.roulette

import KotlinMod
import dev.xdark.clientapi.event.input.KeyPress
import dev.xdark.clientapi.resource.ResourceLocation
import org.lwjgl.input.Keyboard
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.animate
import ru.cristalix.uiengine.utility.*

class RouletteMod: KotlinMod() {

    override fun onEnable() {

        UIEngine.initialize(this)

        val roulette = rectangle {

            size = V3(200.0, 200.0)
            align = CENTER
            origin = CENTER
            color = WHITE
            textureLocation = ResourceLocation.of("minecraft", "textures/items/arrow.png")

        }

        registerHandler<KeyPress> {
            if (key == Keyboard.KEY_K) {
                roulette.rotation.degrees = 0.0
                roulette.animate(5, Easings.QUINT_OUT) {
                    roulette.rotation.degrees = 2 * Math.PI * 8.333
                }
            }
        }

        UIEngine.overlayContext.addChild(roulette)

    }

}