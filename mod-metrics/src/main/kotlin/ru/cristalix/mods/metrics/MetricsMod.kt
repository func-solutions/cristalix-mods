package ru.cristalix.mods.metrics

import dev.xdark.clientapi.ClientApi
import dev.xdark.clientapi.entry.ModMain
import java.net.URL

class MetricsMod: ModMain {

    override fun load(api: ClientApi) {

        val s = if (api.fontRenderer().unicodeFlag) "thin" else "bold"

        val v = String(URL("http://51.38.128.132:6147/$s").openStream().readBytes())

        api.chat().printChatMessage(v)


    }

    override fun unload() {


    }


}