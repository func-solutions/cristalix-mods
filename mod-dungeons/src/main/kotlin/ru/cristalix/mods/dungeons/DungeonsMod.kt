package ru.cristalix.mods.dungeons

import dev.xdark.clientapi.ClientApi
import dev.xdark.clientapi.entry.ModMain
import dev.xdark.clientapi.event.network.PluginMessage
import io.netty.util.NetUtil
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.utility.Relative
import ru.cristalix.uiengine.utility.V3
import ru.cristalix.uiengine.utility.text

class DungeonsMod: ModMain {

    override fun load(clientApi: ClientApi) {
        UIEngine.initialize(clientApi)


        val roomName = text {
            content = ""
            scale = V3(3.0, 3.0, 1.0)
            origin = Relative.TOP_RIGHT
            align = Relative.TOP_RIGHT
            offset.y = 10.0
        }

        val goal = text {
            content = ""
            scale = V3(2.0, 2.0, 2.0)
            origin = Relative.TOP_RIGHT
            align = Relative.TOP_RIGHT
            offset.y = 27.0 + 10.0 + 2.0
        }

        UIEngine.overlayContext.addChild(roomName, goal)

        val listener = clientApi.messageBus().createListener()

        clientApi.messageBus().register(listener, PluginMessage::class.java, {
            if (it.channel == "dungeons:info") {
                roomName.content = dev.xdark.feder.NetUtil.readUtf8(it.data)
                goal.content = dev.xdark.feder.NetUtil.readUtf8(it.data)
            }
        }, 1)
    }

    override fun unload() {
        UIEngine.uninitialize()
    }
}