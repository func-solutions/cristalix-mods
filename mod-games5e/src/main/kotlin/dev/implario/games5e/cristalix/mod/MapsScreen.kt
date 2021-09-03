package dev.implario.games5e.cristalix.mod

import dev.xdark.clientapi.resource.ResourceLocation
import org.lwjgl.input.Keyboard
import ru.cristalix.uiengine.element.ContextGui
import ru.cristalix.uiengine.utility.CENTER
import ru.cristalix.uiengine.utility.flex
import java.lang.Double.min

class MapsScreen(val previousScreen: ContextGui) : ContextGui() {

    init {
        keyTypedHandlers.clear()
        onKeyTyped { _, k -> if (k == Keyboard.KEY_ESCAPE) previousScreen.open() }
    }

    val mapsContainer = +flex {

        overflowWrap = true
        beforeTransform {
            size.x = min(this@MapsScreen.size.x, 120.0 * 4 + 8.0 * 4)
        }

        align = CENTER
        origin = CENTER

        flexSpacing = 8.0
        +MapElement(
            "Каллисто", "Faelan_", "Игроков в команде: 4-5",
            ResourceLocation.of("minecraft", "textures/blocks/red_wool.png")
        )
        +MapElement(
            "Европа", "Faelan_", "Игроков в команде: 3-4",
            ResourceLocation.of("minecraft", "textures/blocks/lime_wool.png")
        )
        +MapElement(
            "Ганимед", "Faelan_", "Игроков в команде: 4",
            ResourceLocation.of("minecraft", "textures/blocks/yellow_wool.png")
        )
        +MapElement(
            "Ио", "Faelan_", "Игроков в команде: 2",
            ResourceLocation.of("minecraft", "textures/blocks/black_wool.png")
        )


    }

    init {
        color.alpha = 0.82
    }


}