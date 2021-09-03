package dev.implario.games5e.cristalix.mod

import dev.xdark.clientapi.item.ItemTools
import ru.cristalix.clientapi.KotlinMod
import ru.cristalix.uiengine.UIEngine

class Games5eMod: KotlinMod() {

    override fun onEnable() {
        UIEngine.initialize(this)

        QueuesScreen().open()

        registerChannel("cf:schema") {
            val item = ItemTools.read(this)

            val compound = item.tagCompound.getCompound("schema")


        }

    }

}