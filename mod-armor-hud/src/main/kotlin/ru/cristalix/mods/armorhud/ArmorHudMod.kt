package ru.cristalix.mods.armorhud

import com.google.gson.Gson
import dev.xdark.clientapi.event.gui.ScreenDisplay
import dev.xdark.clientapi.event.input.KeyPress
import dev.xdark.clientapi.event.lifecycle.GameLoop
import dev.xdark.clientapi.opengl.GlStateManager
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse
import ru.cristalix.clientapi.KotlinMod
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.*
import ru.cristalix.uiengine.utility.*
import java.lang.Exception
import java.nio.file.Files
import java.nio.file.Paths

class ArmorHudMod : KotlinMod() {

    var dragging: Boolean = false
    var draggingPosition = V2()
    val gson = Gson()

    override fun onEnable() {
        UIEngine.initialize(this)

        registerHandler<KeyPress> {
            if (key == Keyboard.KEY_J) UIEngine.uninitialize()
        }

        val armorIndicators = rectangle {
            size = V3(50.0, 50.0)
            onClick {
                if (Mouse.isGrabbed()) return@onClick
                dragging = down
                if (down) {
                    draggingPosition = position
                } else {
                    saveSettings(Settings(align.x, align.y, offset.x, offset.y, 1.0, "normal", true))
                }
            }
            beforeRender {
                if (dragging) {
                    val resolution = clientApi.resolution()
                    val factor = resolution.scaleFactor
                    val draggable = this

                    val lastParent = draggable.lastParent ?: return@beforeRender

                    val px = (lastParent.hoverPosition.x - draggingPosition.x) / (lastParent.size.x - draggable.size.x)
                    val py = (lastParent.hoverPosition.y - draggingPosition.y) / (lastParent.size.y - draggable.size.y)

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

                    draggable.align = V3(alignX, alignY)
                    draggable.origin = V3(alignX, alignY)
                    draggable.offset.x =
                        ((lastParent.hoverPosition.x - draggingPosition.x + (draggable.size.x - lastParent.size.x) * alignX)
                            .coerceIn(-alignX * lastParent.size.x, (-alignX + 1) * lastParent.size.x) * factor).toInt().toDouble() /
                                factor
                    draggable.offset.y =
                        ((lastParent.hoverPosition.y - draggingPosition.y + (draggable.size.y - lastParent.size.y) * alignY)
                            .coerceIn(-alignY * lastParent.size.y, (-alignY + 1) * lastParent.size.y) * factor).toInt().toDouble() /
                                factor


                    if (!Mouse.isButtonDown(0)) dragging = false
                }

            }
        }

        registerHandler<GameLoop> {
        }


        UIEngine.overlayContext.addChild(armorIndicators)

        repeat(4) {
            armorIndicators.children.add(
                rectangle {
                    size = V3(16.0, 16.0)
                    color = TRANSPARENT

                    addChild(
                        item {
                            stack = clientApi.itemRegistry().getItem(1).newStack(1, 1)
                        },
                        text {
                            beforeRender = {
                                GlStateManager.disableDepth()
                            }
                            afterRender = {
                                GlStateManager.enableDepth()
                            }
                            offset = V3(19.0, 3.0, -1.0)
                            shadow = true
                        }
                    )
                }
            )
        }

        val slotSize = 17.0

        fun reload(settings: Settings) {

            armorIndicators.align.x = settings.alignX
            armorIndicators.align.y = settings.alignY
            armorIndicators.origin.x = settings.alignX
            armorIndicators.origin.y = settings.alignY
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


        registerHandler<GameLoop> {
            val inventory = clientApi.minecraft().player.inventory
            var i = 0
            IntRange(36, 39).forEach {
                val container = armorIndicators.children[3 - i] as RectangleElement
                val stack = inventory.getStackInSlot(it)
                (container.children[0] as ItemElement).stack =
                    stack
                val percentage = 1.0 - stack.itemDamage.toDouble() / stack.maxDamage
                val textElement = container.children[1] as TextElement
                textElement.content = if (stack.isDamageable) (percentage * 100).toInt().toString() + "%" else ""
                textElement.color.green = (percentage * 2 * 255).toInt().coerceIn(150..255)
                textElement.color.red = ((1 - percentage) * 2 * 255).toInt().coerceIn(150..255)
                textElement.color.blue = 150
                i++
            }
        }

        reload(
            readSettings() ?: Settings(
                1.0, 0.0, -60.0, 0.0, 1.0, "", true
            )
        )
    }

    private fun saveSettings(settings: Settings) {
        try {
            Files.write(Paths.get("armorhud.json"), gson.toJson(settings).toByteArray());
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun readSettings(): Settings? {
        try {
            val readAllLines = Files.readAllLines(Paths.get("armorhud.json"))
            if (readAllLines == null || readAllLines.isEmpty()) return null;
            return gson.fromJson(readAllLines.get(0), Settings::class.java)
        } catch (exception: Exception) {
            return null
        }
    }

}