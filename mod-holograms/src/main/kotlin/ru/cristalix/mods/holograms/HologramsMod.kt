package ru.cristalix.mods.holograms

import dev.xdark.clientapi.ClientApi
import dev.xdark.clientapi.entry.ModMain
import dev.xdark.clientapi.event.input.KeyPress
import dev.xdark.clientapi.event.network.PluginMessage
import dev.xdark.clientapi.event.render.RenderTickPre
import dev.xdark.clientapi.opengl.GlStateManager
import dev.xdark.feder.NetUtil
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.Context3D
import ru.cristalix.uiengine.utility.*
import java.util.*
import kotlin.collections.HashMap

class HologramsMod : ModMain {

    private var holos: MutableMap<UUID, Context3D> = HashMap()

    override fun load(clientApi: ClientApi) {
        UIEngine.initialize(clientApi)

        UIEngine.registerHandler(PluginMessage::class.java) {
            if (channel == "holo") {
                val uuid = UUID(data.readLong(), data.readLong())
                val x = data.readDouble()
                val y = data.readDouble()
                val z = data.readDouble()
                val texture = NetUtil.readUtf8(data)
                addHolo(uuid, x, y, z, texture)
            } else if (channel == "holohide") {
                val uuid = UUID(data.readLong(), data.readLong())
                val holo = holos.remove(uuid)
                if (holo != null) {
                    UIEngine.worldContexts.remove(holo)
                }
            }
        }

//        val context3D = Context3D(V3(107.0, 100.0, 100.0))
//        UIEngine.worldContexts.add(context3D)
//
//        repeat(5) {
//            context3D.addChild(rectangle {
//                size.x = it * 10.0
//                size.y = 20.0
//                offset.x = (it * it / 2.0 + 1) * 10.0
//                color = ru.cristalix.uiengine.utility.Color(0, 0, 0, 0.5)
//                beforeRender = {
//                    GL11.glStencilMask(0xFF)
//                    GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_INCR)
//                    GL11.glStencilFunc(GL11.GL_LEQUAL, 128, 0xFF)
//                }
//            })
//        }
//
//        UIEngine.registerHandler(PluginMessage::class.java) {
//            if (channel == "wecui:wecui") {
//                println("wecui: " + NetUtil.readUtf8(data))
//            }
//        }
//
//        context3D.addChild(rectangle {
//            size.x = 40.0
//            size.y = 40.0
//            offset.x = 30.0
//            origin = Relative.CENTER
//            rotation.degrees = Math.PI / 3.0
//            color = WHITE
//            beforeRender = {
//                GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP)
//                GL11.glEnable(GL11.GL_STENCIL_TEST)
//                GlStateManager.disableDepth()
////                    GlStateManager.depthFunc(GL11.GL_EQUAL)
//            }
//            afterRender = {
//                GlStateManager.enableDepth()
////                GL11.glDisable(GL11.GL_STENCIL_TEST)
//                GL11.glStencilFunc(GL11.GL_ALWAYS, 128, 0xFF)
////                    GlStateManager.depthFunc(GL11.GL_LEQUAL)
//            }
//        })

//        addHolo(UUID.randomUUID(), 0.0, 100.0, 0.0, "mcpatcher/cit/among_us/alert.png")

        val player = clientApi.minecraft().player
        UIEngine.registerHandler(RenderTickPre::class.java) {
            holos.forEach {
                val yaw = (player.rotationYaw - player.prevRotationYaw) * clientApi.minecraft().timer.renderPartialTicks + player.prevRotationYaw
                val pitch = (player.rotationPitch - player.prevRotationPitch) * clientApi.minecraft().timer.renderPartialTicks + player.prevRotationPitch
                it.value.context?.rotation = Rotation(-yaw * Math.PI / 180 + Math.PI, 0.0, 1.0, 0.0)
                it.value.children[0].rotation = Rotation(-pitch * Math.PI / 180, 1.0, 0.0, 0.0)
            }
        }

//        UIEngine.registerHandler(KeyPress::class.java) {
//            if (key == Keyboard.KEY_J) {
//                GL11.glDisable(GL11.GL_STENCIL_TEST)
//                unload()
//            }
//        }

    }

    private fun addHolo(uuid: UUID, x: Double, y: Double, z: Double, texture: String) {
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