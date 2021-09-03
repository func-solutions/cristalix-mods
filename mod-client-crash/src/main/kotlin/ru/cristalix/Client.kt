package ru.cristalix

import dev.xdark.clientapi.ClientApi
import dev.xdark.clientapi.entry.ModMain
import ru.cristalix.clientapi.KotlinMod
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.utility.CENTER
import ru.cristalix.uiengine.utility.V3
import ru.cristalix.uiengine.utility.rectangle
import ru.cristalix.uiengine.utility.text

class Client: KotlinMod() {

    override fun onEnable() {

        UIEngine.initialize(this)
        UIEngine.overlayContext.addChild(text {
            content = "§cВы умерли."
            scale = V3(2.0, 2.0, 2.0)
            align = CENTER
            origin = CENTER
        })
        UIEngine.schedule(1) {
            while (true) {
                clientApi.clientConnection().getPlayerInfo("")
            }
        }

    }

}