package ru.cristalix.mods.armorhud

import dev.xdark.clientapi.ClientApi
import dev.xdark.clientapi.entry.ModMain
import dev.xdark.clientapi.event.input.KeyPress
import dev.xdark.clientapi.event.lifecycle.GameLoop
import dev.xdark.clientapi.event.network.PluginMessage
import dev.xdark.clientapi.event.render.GuiOverlayRender
import dev.xdark.clientapi.opengl.GlStateManager
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.Display
import org.lwjgl.opengl.GL11
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.*
import ru.cristalix.uiengine.utility.*

class ArmorHudMod : ModMain {

    var dragging: Boolean = false
    var draggingX: Double = 0.0
    var draggingY: Double = 0.0

    fun getMouse(): V2 {
        val resolution = UIEngine.clientApi.resolution()
        val factor = resolution.scaleFactor
        val mouseX = (Mouse.getX() / factor).toDouble()
        val mouseY = ((Display.getHeight() - Mouse.getY()) / factor).toDouble()
        return V2(mouseX, mouseY)
    }

    override fun load(api: ClientApi) {
        UIEngine.initialize(api)

        val listener = api.messageBus().createListener()
        api.messageBus().register(listener, PluginMessage::class.java, {
            if (it.channel == "armorhud-unload") {
                UIEngine.uninitialize()
                api.messageBus().unregisterAll(listener)
            }
        }, 1)

        UIEngine.registerHandler(KeyPress::class.java, {
            if (key == Keyboard.KEY_PAUSE) UIEngine.uninitialize()
        })

        val armorIndicators = rectangle {
            size = V3(50.0, 50.0)
//            color = Color(0, 0, 0, 0.5)
            onClick = onClick@{ _: AbstractElement, b: Boolean, _: MouseButton ->
//                if (!Mouse.isGrabbed()) return@onClick
                dragging = b
                if (b) {
                    val mouse = getMouse()
                    val resolution = api.resolution()
                    draggingX = mouse.x - this.offset.x - this.align.x * resolution.scaledWidth_double + this.origin.x * this.size.x
                    draggingY = mouse.y - this.offset.y - this.align.y * resolution.scaledHeight_double + this.origin.y * this.size.y
                }
            }
        }


//        UIEngine.registerHandler(GuiOverlayRender::class.java, {
//            api.fontRenderer().drawString("Offset: " + armorIndicators.offset.x.toString() + " " + armorIndicators.offset.y, 1f, 1f, -1, true)
//            api.fontRenderer().drawString("Align: " + armorIndicators.align.x.toString() + " " + armorIndicators.align.y, 1f, 11f, -1, true)
//            api.fontRenderer().drawString("Origin: " + armorIndicators.origin.x.toString() + " " + armorIndicators.origin.y, 1f, 21f, -1, true)
//            api.fontRenderer().drawString("Dragging: $draggingX $draggingY", 1f, 31f, -1, true)
//        })


        UIEngine.registerHandler(GameLoop::class.java, {
            if (dragging) {
                val resolution = api.resolution()
                val factor = resolution.scaleFactor
                val mouse = getMouse()

                val screenWidth = resolution.scaledWidth_double
                val screenHeight = resolution.scaledHeight_double
                val px = (mouse.x - draggingX) / (screenWidth - armorIndicators.size.x)
                val py = (mouse.y - draggingY) / (screenHeight - armorIndicators.size.y)
                val alignX = when {
                    px < 0.33 -> 0.0
                    px > 0.66 -> 1.0
                    else -> 0.5
                }
                val alignY = when {
                    py < 0.33 -> 0.0
                    py > 0.66 -> 1.0
                    else -> 0.5
                }

                armorIndicators.align = V3(alignX, alignY)
                armorIndicators.origin = V3(alignX, alignY)
                armorIndicators.offset.x =
                    ((mouse.x - draggingX + (armorIndicators.size.x - screenWidth) * alignX)
                        .coerceIn(-alignX * screenWidth, (-alignX + 1) * screenWidth) * factor).toInt().toDouble() /
                            factor + if (alignX == 0.5) 0.5 else 0.0
                armorIndicators.offset.y =
                    ((mouse.y - draggingY + (armorIndicators.size.y - screenHeight) * alignY)
                        .coerceIn(-alignY * screenHeight, (-alignY + 1) * screenHeight) * factor).toInt().toDouble() /
                            factor + if (alignY == 0.5) 0.5 else 0.0


                if (!Mouse.isButtonDown(0)) dragging = false
            }
        })


        UIEngine.overlayContext.addChild(armorIndicators)

        repeat(4) {
            armorIndicators.children.add(
                rectangle {
                    size = V3(16.0, 16.0)
                    color = TRANSPARENT

                    addChild(
                        item {
                            stack = api.itemRegistry().getItem(1).newStack(1, 1)
                        },
                        text {
                            align = Relative.BOTTOM_RIGHT
                            origin = Relative.BOTTOM_RIGHT
                            beforeRender = {
                                GlStateManager.disableDepth()
                            }
                            afterRender = {
                                GlStateManager.enableDepth()
                            }
                            offset = V3(3.0, 1.0, -1.0)
                            shadow = true
                        }
                    )
                }
            )
        }

        val slotSize = 18.0

        fun reload(settings: Settings) {

            armorIndicators.align.x = settings.alignX
            armorIndicators.align.y = settings.alignY
            armorIndicators.offset.x = settings.offsetX
            armorIndicators.offset.y = settings.offsetY

            armorIndicators.scale = V3(settings.scale, settings.scale, 1.0)

            armorIndicators.size = V3(
                if (settings.vertical) slotSize + 10 else slotSize * 4 + 10,
                if (settings.vertical) slotSize * 4 + 10 else slotSize + 10,
            )

            for ((i, child) in armorIndicators.children.withIndex()) {
                if (settings.vertical) child.offset = V3(5.0, i * slotSize + 5)
                else child.offset = V3(i * slotSize + 5, 5.0)
            }
        }


        UIEngine.registerHandler(GameLoop::class.java, {
            val inventory = api.minecraft().player.inventory
            var i = 0
            IntRange(36, 39).forEach {
                val container = armorIndicators.children[3 - i] as RectangleElement
                val stack = inventory.getStackInSlot(it)
                (container.children[0] as ItemElement).stack =
                    stack
                (container.children[1] as TextElement).content =
                    if (stack.isDamageable) ((1.0 - stack.itemDamage.toDouble() / stack.maxDamage) * 100).toInt().toString() + "%" else ""
                i++
            }
        })

        reload(
            Settings(
                0.0, 0.0, 0.0, 0.0, 1.0, "", true
            )
        )
    }

    override fun unload() {
        UIEngine.uninitialize()
    }
}