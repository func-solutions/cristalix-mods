package dev.implario.games5e.cristalix.mod

import dev.xdark.clientapi.resource.ResourceLocation
import ru.cristalix.uiengine.element.ContextGui
import ru.cristalix.uiengine.utility.*

class QueuesScreen: ContextGui() {

    val queuesContainer = +flex {
        flexSpacing = 8.0

        align = CENTER
        origin = CENTER

        +queue("Столбы ¨22ae49Classic", ResourceLocation.of("minecraft", "textures/blocks/glazed_terracotta_lime.png")) {
            onClick {
                if (button == MouseButton.RIGHT) {
                    MapsScreen(this).open()
                }
            }
        }
        +QueueElement("Столбы ¨e07614PvP", ResourceLocation.of("minecraft", "textures/blocks/glazed_terracotta_orange.png"))
        +QueueElement("CustomSteveChaos", ResourceLocation.of("minecraft", "textures/blocks/gold_block.png"))
    }

    val topHud = +rectangle {
        size = V3(250.0, 50.0)
        color = hex("3d3d3d", 0.6)
        align = TOP
        origin = TOP
    }

    init {
        color.alpha = 0.82
    }

}
