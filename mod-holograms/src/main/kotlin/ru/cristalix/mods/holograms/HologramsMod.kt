package ru.cristalix.mods.holograms

import dev.xdark.clientapi.ClientApi
import dev.xdark.clientapi.entity.EntityLivingBase
import dev.xdark.clientapi.entry.ModMain
import dev.xdark.clientapi.event.input.KeyPress
import dev.xdark.clientapi.event.lifecycle.GameLoop
import dev.xdark.clientapi.event.network.PluginMessage
import dev.xdark.clientapi.event.render.NameTemplateRender
import dev.xdark.clientapi.event.render.RenderTickPre
import dev.xdark.clientapi.resource.ResourceLocation
import dev.xdark.feder.NetUtil
import org.lwjgl.input.Keyboard
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.Context3D
import ru.cristalix.uiengine.element.RectangleElement
import ru.cristalix.uiengine.utility.*
import java.util.*
import kotlin.collections.HashMap

class HologramsMod : ModMain {

    private var holos: MutableMap<UUID, Context3D> = HashMap()

    override fun load(clientApi: ClientApi) {
        UIEngine.initialize(clientApi)

        val listener = clientApi.messageBus().createListener()
        clientApi.messageBus().register(listener, PluginMessage::class.java, {
            if (it.channel == "holo") {
                val uuid = UUID(it.data.readLong(), it.data.readLong());
                val x = it.data.readDouble()
                val y = it.data.readDouble()
                val z = it.data.readDouble()
                val texture = NetUtil.readUtf8(it.data)
                addHolo(uuid, x, y, z, texture)
            }
        }, 1)

//        addHolo(UUID.randomUUID(), 0.0, 100.0, 0.0, "mcpatcher/cit/among_us/alert.png")

        val player = clientApi.minecraft().player
        UIEngine.registerHandler(RenderTickPre::class.java, {
            holos.forEach {
                val yaw = (player.rotationYaw - player.prevRotationYaw) * clientApi.minecraft().timer.renderPartialTicks + player.prevRotationYaw
                val pitch = (player.rotationPitch - player.prevRotationPitch) * clientApi.minecraft().timer.renderPartialTicks + player.prevRotationPitch
                it.value.context.rotation = Rotation(-yaw * Math.PI / 180 + Math.PI, 0.0, 1.0, 0.0)
                it.value.children[0].rotation = Rotation(-pitch * Math.PI / 180, 1.0, 0.0, 0.0)
            }
        })

//        UIEngine.registerHandler(KeyPress::class.java, {
//            if (key == Keyboard.KEY_J) unload()
//        })

    }

    private fun addHolo(uuid: UUID, x: Double, y: Double, z: Double, texture: String) {
        println(123)
        val rect = rectangle {
            textureLocation = UIEngine.clientApi.resourceManager().getLocation("minecraft", texture)
            size = V3(16.0, 16.0)
            origin = Relative.CENTER
            color = WHITE
        }
        val context = Context3D(V3(x, y, z))
        context.addChild(rect)
        holos[uuid] = context
        UIEngine.worldContexts.add(context)
    }

    override fun unload() {
        UIEngine.uninitialize()
    }
}