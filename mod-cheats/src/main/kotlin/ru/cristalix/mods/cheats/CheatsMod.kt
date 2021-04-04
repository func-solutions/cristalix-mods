package ru.cristalix.mods.cheats

import com.sun.java.accessibility.util.GUIInitializedListener
import dev.xdark.clientapi.ClientApi
import dev.xdark.clientapi.entity.EntityPlayer
import dev.xdark.clientapi.entry.ModMain
import dev.xdark.clientapi.event.chat.ChatSend
import dev.xdark.clientapi.event.entity.PlayerJump
import dev.xdark.clientapi.event.input.KeyPress
import dev.xdark.clientapi.event.input.MousePress
import dev.xdark.clientapi.event.lifecycle.GameLoop
import dev.xdark.clientapi.event.render.GuiOverlayRender
import dev.xdark.clientapi.event.render.HotbarRender
import dev.xdark.clientapi.event.render.NameTemplateRender
import dev.xdark.clientapi.event.render.RenderTickPre
import dev.xdark.clientapi.math.MathHelper
import dev.xdark.clientapi.opengl.GlStateManager
import dev.xdark.feder.NetUtil
import io.netty.buffer.Unpooled
import org.lwjgl.input.Keyboard
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.AbstractElement
import ru.cristalix.uiengine.element.AnimationContext
import ru.cristalix.uiengine.element.RectangleElement
import ru.cristalix.uiengine.element.TextElement
import ru.cristalix.uiengine.utility.Color
import ru.cristalix.uiengine.utility.Relative
import ru.cristalix.uiengine.utility.rectangle
import ru.cristalix.uiengine.utility.text
import kotlin.math.absoluteValue
import kotlin.math.acos
import kotlin.math.asin

class CheatsMod: ModMain {

    lateinit var ui: RectangleElement


    val functions = mapOf(
//        "akb" to "AntiKnockBack",
//        "killaura" to "KillAura",
        "fly" to "FlyHack",
        "longjump" to "LongJump",
        "nofall" to "NoFall",
//        "scaff" to "ScaffoldWalk",
        "nogravity" to "AntiGravity",
        "aim" to "PlayerAimbot"
    )


    val using = ArrayList<String>()
    val rgb = ArrayList<AbstractElement>()

    override fun load(clientApi: ClientApi) {
        UIEngine.initialize(clientApi)

        UIEngine.overlayContext.addChild(text {
            origin = Relative.TOP
            align = Relative.TOP
            offset.y = 3.0
            content = "Меню чита включается на ё"
            shadow = true
            rgb.add(this)
        })

        ui = rectangle {
            enabled = false
            functions.entries.forEachIndexed { i, e ->
                addChild(rectangle {
                    color = Color(40, 40, 40, 0.5)
                    size.y = 13.0
                    offset.x = 3.0
                    offset.y = 3.0 + 14.0 * i
                    addChild(text {
                        content = e.value
                        origin = Relative.CENTER
                        align = Relative.CENTER
                        shadow = true
                        rgb.add(this)
                    })
                    size.x = children[0].size.x + 6.0
                    onClick = { el, b, d ->

                        if (b) {
                            val buffer = Unpooled.buffer()
                            NetUtil.writeUtf8(e.key, buffer)
                            color = if (using.remove(e.key)) {
                                buffer.writeBoolean(false)
                                Color(40, 40, 40, 0.5)
                            } else {
                                using.add(e.key)
                                buffer.writeBoolean(true)
                                Color(230, 230, 230, 0.5)
                            }
                            clientApi.clientConnection().sendPayload("cheats", buffer)
                        }

                    }
                })

            }
        }

        UIEngine.overlayContext.addChild(ui)

        UIEngine.registerHandler(PlayerJump::class.java) {
            if (using.contains("longjump")) {
                val yaw = player.rotationYaw / 180f * Math.PI.toFloat()
                val cos = MathHelper.cos(yaw).toDouble()
                val sin = MathHelper.sin(yaw).toDouble()
                isCancelled = true
//                player.setMotion(0.0, 0.5, 0.0)
                player.setMotion(-sin * 1.5, 0.5, cos * 1.5)
//                UIEngine.overlayContext.schedule(0.05) {
//                }
            }
        }

        UIEngine.registerHandler(ChatSend::class.java) {
            message = message.replace("/ban", "/fakeban").replace("/mute", "/fakemute")
                .replace("/galert", "/fakegalert")
        }

        UIEngine.registerHandler(GameLoop::class.java) {
            clientApi.minecraft().player.setNoGravity(using.contains("nogravity"))
        }

        val entities = ArrayList<EntityPlayer>()

        UIEngine.registerHandler(RenderTickPre::class.java) {

            val percent = (System.currentTimeMillis() % 10000) / 10000.0
            val step = (percent * 6).toInt()
            val part = percent * 6.0 - step


            val r = if (step == 0 || step == 5) 1.0 else if (step == 1) 1.0 - part else if (step == 4) part else 0.0
            val g = if (step == 2 || step == 1) 1.0 else if (step == 3) 1.0 - part else if (step == 0) part else 0.0
            val b = if (step == 4 || step == 3) 1.0 else if (step == 5) 1.0 - part else if (step == 2) part else 0.0

            val color = Color((r * 255).toInt(), (g * 255).toInt(), (b * 255).toInt(), 1.0)
            rgb.forEach {
                it.color = color
            }

            entities.clear()
        }

        UIEngine.registerHandler(NameTemplateRender::class.java) {
            if (entity is EntityPlayer) entities.add(entity as EntityPlayer)
        }

        val player = clientApi.minecraft().player

        fun distSq(other: EntityPlayer) =
            (other.x - player.x) * (other.x - player.x) +
            (other.y - player.y) * (other.y - player.y) +
            (other.z - player.z) * (other.z - player.z)

        UIEngine.registerHandler(GuiOverlayRender::class.java) {

            if (!using.contains("aim")) return@registerHandler
            val nearest = entities.minByOrNull { distSq(it) }
            if (nearest == null || distSq(nearest) > 25.0) return@registerHandler
            var dx = player.x - nearest.x
            var dy = player.y - nearest.y
            var dz = player.z - nearest.z

            val l1 = MathHelper.fastInvSqrt(dx * dx + dy * dy + dz * dz)
            val l2 = MathHelper.fastInvSqrt(dx * dx + dz * dz)
            dx *= l2
            dy *= l1
            dz *= l2

            var asin = asin(dx)
            val acos = acos(dz)

            if (acos < Math.PI / 2.0) asin = Math.PI - asin

            player.setYaw((asin * 180.0f / Math.PI).toFloat())
            player.setPitch(((asin(dy) * 180f / Math.PI).toFloat()))

//            clientApi.fontRenderer().drawString("${(asin(dx) * 180.0 / Math.PI).toInt()} ${(-acos(dz) * 180.0 / Math.PI).toInt()}", 100.0f, 2.0f, -1, true)
        }

        UIEngine.registerHandler(KeyPress::class.java) {
            if (key == Keyboard.KEY_GRAVE) {
                ui.enabled = !ui.enabled
                if (ui.enabled) {
                    clientApi.minecraft().setIngameNotInFocus()
                } else {
                    clientApi.minecraft().setIngameFocus()
                }
            }
        }

    }

    override fun unload() {
        UIEngine.uninitialize()
    }


}