package ru.cristalix.mods.cosmetics

import dev.xdark.clientapi.event.input.KeyPress
import dev.xdark.clientapi.event.input.MousePress
import dev.xdark.clientapi.event.lifecycle.GameLoop
import dev.xdark.clientapi.event.render.RenderTickPre
import dev.xdark.clientapi.item.ItemStack
import dev.xdark.clientapi.item.Items
import dev.xdark.clientapi.opengl.GlStateManager
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.Display
import org.lwjgl.opengl.GL11
import ru.cristalix.clientapi.KotlinMod
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.utility.*

class CosmeticsMod : KotlinMod() {

    override fun onEnable() {

        UIEngine.initialize(this)

        UIEngine.overlayContext.apply {
            color = Color(0, 0, 0, 0.86)
            beforeRender = {
                GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT)
            }
        }


        val crateScreen = CrateScreen()
        crateScreen.prepareToOpen()
        crateScreen.shake()
        crateScreen.open()

//        perspective.addChild(text {
//            content = "Hello world"
//        })

        var ready = false
        var pressed = false

        registerHandler<GameLoop> {

            if (ready && Mouse.isButtonDown(0) && !pressed) {
                pressed = true

                clientApi.chat().printChatMessage("123")

                crateScreen.apply {
                    chest.animate(0.5, Easings.QUINT_OUT) {
                        chest.scale = V3(7.0, 7.0, 7.0)
                    }
                }

            }


            if (pressed) {
                clientApi.chat().printChatMessage("shake")

                clientApi.minecraft().setIngameNotInFocus()
                crateScreen.apply {
                    if (Mouse.isButtonDown(0)) shake()
                    else {
                        pressed = false
                        ready = false
                        open()
                    }
                }
            }

        }

        registerHandler<MousePress> {
            clientApi.chat().printChatMessage("mouse")

            isCancelled = ready
        }

        registerHandler<KeyPress> {
            clientApi.chat().printChatMessage("key")

            if (key == Keyboard.KEY_J) unload()

            if (key == Keyboard.KEY_K) {
                CosmeticsScreen()
            }
            if (key == Keyboard.KEY_I) {
                clientApi.minecraft().setIngameNotInFocus()

                when ((Math.random() * 6).toInt()) {
                    0 -> crateScreen.setup(GRAY, "Необычный предмет", ItemStack.of(Items.IRON_PICKAXE, 1, 0), "Кирка-ювелирка")
                    1 -> crateScreen.setup(GREEN, "Интересный предмет", ItemStack.of(Items.ACACIA_BOAT, 1, 0), "Лодка из\nапельсина")
                    2 -> crateScreen.setup(BLUE, "Редкий предмет", ItemStack.of(Items.ENDER_EYE, 1, 0), "Глаз эндера")
                    3 -> crateScreen.setup(ORANGE, "Невероятный предмет", ItemStack.of(Items.GOLD_INGOT, 1, 0), "Слиток\nзолота")
                    4 -> crateScreen.setup(PURPLE, "Эпический предмет", ItemStack.of(Items.TOTEM_OF_UNDYING, 1, 0), "Тотем бессмертия")
                    5 -> crateScreen.setup(RED, "ЛЕГЕНДАРНЫЙ ПРЕДМЕТ", ItemStack.of(Items.GOLDEN_APPLE, 1, 1), "Золотое\nяблоко")
                }

                crateScreen.prepareToOpen()
                ready = true
            }
            if (key == Keyboard.KEY_O) {
                crateScreen.chestLid.animate(0.5, easing = Easings.BOUNCE_OUT) {
                    rotation.degrees = 0.0
                }
            }
        }

        registerHandler<RenderTickPre> {

            crateScreen.apply {

                val intensity = rotationIntensity.color.alpha

                body1.animate(0.03) {
                    body1.rotation.degrees = (Mouse.getX() / Display.getWidth().toDouble() - 0.5) * Math.PI / 2 * intensity
                }
                body2.animate(0.03) {
                    body2.rotation.degrees = (Mouse.getY() / Display.getHeight().toDouble() - 0.5) * Math.PI / 2 * intensity
                }
                glowRect.animate(0.1) {
                    glowRect.rotation.degrees = -(Mouse.getX() / Display.getWidth().toDouble() - 0.5) * Math.PI / 2 * intensity
                }
            }


//            c.rotation.degrees = (System.currentTimeMillis() % 2000) / 2000.0 * Math.PI * 2
        }


    }


}