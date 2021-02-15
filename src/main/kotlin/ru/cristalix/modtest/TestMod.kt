package ru.cristalix.modtest

import dev.xdark.clientapi.ClientApi
import dev.xdark.clientapi.entry.ModMain
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.Rectangle
import ru.cristalix.uiengine.element.Text
import ru.cristalix.uiengine.utility.V3
import ru.cristalix.uiengine.utility.WHITE

class TestMod : ModMain {

    override fun load(api: ClientApi) {
        UIEngine.initialize(api)
        UIEngine.overlayContext.addChild(
            Rectangle(
                size = V3(100.0, 100.0),
                color = WHITE,
                children = listOf(
                    Text(
                        content = "§0Hello, world.",
                        origin = V3(0.5, 0.5),
                        align = V3(0.5, 0.5)
                    )
                )
            )
        )
    }

    override fun unload() {
        UIEngine.uninitialize()
    }
}