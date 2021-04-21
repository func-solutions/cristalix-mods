package ru.cristalix.mods.holograms

import KotlinMod
import dev.xdark.clientapi.ClientApi
import dev.xdark.clientapi.entry.ModMain
import dev.xdark.clientapi.event.input.KeyPress
import dev.xdark.clientapi.event.network.PluginMessage
import dev.xdark.clientapi.event.render.RenderTickPre
import dev.xdark.clientapi.opengl.GlStateManager
import dev.xdark.clientapi.render.DefaultVertexFormats
import dev.xdark.clientapi.resource.ResourceLocation
import dev.xdark.feder.NetUtil
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.Context3D
import ru.cristalix.uiengine.utility.*
import java.util.*
import kotlin.collections.HashMap

class HologramsMod : KotlinMod() {

    private var holos: MutableMap<UUID, Context3D> = HashMap()

    override fun onEnable() {
        UIEngine.initialize(this)

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
//
//        val context3D = Context3D(V3(100.0, 106.0, 100.0))
//        context3D.addChild(cube {
//            size.x = 10.0
//            size.y = 10.0
//            size.z = 10.0
//            color = WHITE
//            textureSize = V2(1.0, 1.0)
//            textureLocation = ResourceLocation.of("minecraft", "textures/entity/chest/normal_double.png")
//        })
//
//        UIEngine.worldContexts.add(context3D)

//        clientApi.chat().printChatMessage("123")

//        UIEngine.overlayContext.addChild(rectangle {
//            color = WHITE
//            align = V3(0.4, 0.4)
//            beforeRender = {
//
//                val width = 100.0
//                val height = 100.0
//
//                val part = (System.currentTimeMillis() % 4000) / 4000.0
//
//                GlStateManager.enableBlend()
//                GlStateManager.disableTexture2D()
//                GlStateManager.color(1f, 1f, 1f, 1f)
//                GlStateManager.disableCull()
//
//                val tessellator = clientApi.tessellator()
//
//                val worldRenderer = tessellator.bufferBuilder
//                worldRenderer.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION)
//
//
//                worldRenderer.pos(width / 2, height / 2, 0.0).endVertex()
//                worldRenderer.pos(width / 2, 0.0, 0.0).endVertex()
//                if (part > 0.125) worldRenderer.pos(width, 0.0, 0.0).endVertex()
//                if (part > 0.375) worldRenderer.pos(width, height, 0.0).endVertex()
//                if (part > 0.625) worldRenderer.pos(0.0, height, 0.0).endVertex()
//                if (part > 0.875) worldRenderer.pos(0.0, 0.0, 0.0).endVertex()
//
//                val step = ((part + 0.125) * 4).toInt()
//
//                val d = part * 4 - step + 0.5
//
//                worldRenderer.pos(
//                    width * if (step == 0 || step == 4) d else if (step == 1) 1.0 else if (step == 2) 1 - d else 0.0,
//                    height * if (step == 0 || step == 4) 0.0 else if (step == 1) d else if (step == 2) 1.0 else 1 - d,
//                    0.0
//                ).endVertex()
//
//                tessellator.draw()
//
//                GlStateManager.enableTexture2D()
//                clientApi.fontRenderer().drawString("$step ${(d * 100).toInt() / 100.0}", 10f, 10f, -1, true)
//
//            }
//        })

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
////                GL11.glDisable(GL11.GL_STENCIL_TEST)
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
            beforeRender = {
                GlStateManager.disableDepth()
            }
            afterRender = {
                GlStateManager.enableDepth()
            }
        }
        val context = Context3D(V3(x, y, z))
        context.addChild(rect)
        holos[uuid] = context
        UIEngine.worldContexts.add(context)
    }

}