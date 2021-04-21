package ru.cristalix.mods.cosmetics

import KotlinMod
import dev.xdark.clientapi.event.input.KeyPress
import dev.xdark.clientapi.event.lifecycle.GameLoop
import dev.xdark.clientapi.event.render.RenderTickPre
import dev.xdark.clientapi.item.Item
import dev.xdark.clientapi.item.ItemStack
import dev.xdark.clientapi.opengl.GlStateManager
import dev.xdark.clientapi.opengl.RenderHelper
import dev.xdark.clientapi.resource.ResourceLocation
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.Display
import org.lwjgl.opengl.GL11
import org.lwjgl.util.glu.GLU
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.animate
import ru.cristalix.uiengine.utility.*

class CosmeticsMod : KotlinMod() {

    override fun onEnable() {

        UIEngine.initialize(this)

        UIEngine.overlayContext.color = Color(0, 0, 0, 0.86)

        val perspective = rectangle {
            enabled = false
            beforeRender = {
                GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT)
                GlStateManager.matrixMode(GL11.GL_PROJECTION)
                GlStateManager.pushMatrix()
                GlStateManager.loadIdentity()
                val screen = UIEngine.overlayContext.size
                GLU.gluPerspective(90f, (screen.x / screen.y).toFloat(), 0.01f, 500f)
                GlStateManager.matrixMode(GL11.GL_MODELVIEW)
                GlStateManager.loadIdentity()
                GlStateManager.translate(0f, 0f, -300f)
                GlStateManager.scale(1f, -1f, 1f)
                GlStateManager.disableCull()


                GlStateManager.disableCull()
                GlStateManager.enableDepth()
                RenderHelper.enableGUIStandardItemLighting()
                GlStateManager.enableRescaleNormal()

            }
            afterRender = {

                RenderHelper.disableStandardItemLighting()

                GlStateManager.matrixMode(GL11.GL_PROJECTION)
                GlStateManager.popMatrix()
                GlStateManager.matrixMode(GL11.GL_MODELVIEW)
            }
            align = CENTER
        }

        val body1 = rectangle {
            rotation = Rotation(y = 1.0, z = 0.0)
        }

        val body2 = rectangle {
            rotation = Rotation(x = 1.0, z = 0.0)
            align = CENTER
            addChild(body1)
        }

        perspective.addChild(body2)

        UIEngine.overlayContext.addChild(perspective)

        val chestLid = cube {

            rotation.x = 1.0
            rotation.z = 0.0

            offset.y = 1.0
            align = V3(0.5, 0.0, 1.0)
            origin = V3(0.5, 1.0, 1.0)

            color = WHITE
            size = V3(14.0, 5.0, 14.0)
            textureLocation =
                clientApi.resourceManager().getLocation("minecraft", "textures/entity/chest/normal.png")
            textureSize = V3(64.0, 64.0)

            addChild(cube {

                align = V3(0.5, 0.0, 0.0)
                origin = V3(0.5, 0.0, 1.0)
                offset.y = 3.0

                color = WHITE
                size = V3(2.0, 4.0, 1.0)

                textureLocation =
                    clientApi.resourceManager().getLocation("minecraft", "textures/entity/chest/normal.png")
                textureSize = V3(64.0, 64.0)

            })

        }
        val chest = cube {

            offset.y = 50.0
            align = V3(0.5, 0.5, 0.5)

            rotation = Rotation(0.3, 1.0, 0.0, 0.0)
            origin = V3(0.5, 0.5, 0.5)

            scale = V3(6.0, 6.0, 6.0)

            afterRender = {
                RenderHelper.disableStandardItemLighting()
            }

            color = WHITE
            size = V3(14.0, 10.0, 14.0)
            textureLocation = clientApi.resourceManager().getLocation("minecraft", "textures/entity/chest/normal.png")
            textureSize = V3(64.0, 64.0)
            textureFrom = V2(0.0, 19.0)
            addChild(chestLid)
        }
        val wrapper = rectangle {
//            offset.z = 60.0
            addChild(chest)
            align = CENTER
            rotation = Rotation(0.0, 0.0, 1.0, 0.0)

            beforeRender = {
//                GlStateManager.disableCull()
//                GlStateManager.enableDepth()
//                RenderHelper.enableGUIStandardItemLighting()
//                GlStateManager.enableRescaleNormal()
            }

            afterRender = {
//                RenderHelper.disableStandardItemLighting()
            }

        }

        body1.addChild(wrapper)

        val glowRect = rectangle {

            offset.z = 90.0

            rotation.y = 1.0
            rotation.z = 0.0


            align = CENTER
            origin = CENTER
            size = V3(200.0, 200.0)
//            offset.y = -50.0
            color = Color(0x80, 0x3C, 0xEE, 0.85)
            textureLocation = ResourceLocation.of("minecraft", "textures/glow.png")
            beforeRender = {
                GlStateManager.disableDepth()
                GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE)
            }

            afterRender = {
                GlStateManager.enableDepth()
                GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
            }
        }
        body1.addChild(glowRect)
        val itemRect = rectangle {
            offset.z = 90.0
            offset.y = 20.0
            align = CENTER
            origin = CENTER
            color = Color(0x80, 0x3C, 0xEE, 0.28)
            size = V3(23.0, 23.0)
            scale = V3(2.0, 2.0, 2.0)
//            offset.y = -50.0
            addChild(rectangle {
                origin = BOTTOM
                align = BOTTOM
                size = V3(23.0, 1.0)
                color = Color(0x80, 0x3C, 0xEE, 1.0)
            })
            addChild(item {
                offset.z = -150.0
                stack = ItemStack.of(Item.of(449), 1, 0)
                align = CENTER
                origin = CENTER
            })
        }
        body1.addChild(itemRect)

//        perspective.addChild(text {
//            content = "Hello world"
//        })

        var ready = false
        var pressed = false

        registerHandler<GameLoop> {

            if (ready && Mouse.isButtonDown(0) && !pressed) {
                pressed = true

                chest.animate(0.5, Easings.QUINT_OUT) {
                    chest.scale = V3(5.0, 5.0, 5.0)
                }

            }

            if (pressed) {
                chest.animate(0.05) {
                    chest.offset.x = (Math.random() - 0.5) * 32.0
                    chest.offset.y = (Math.random() - 0.5) * 32.0 + 50.0
                    chest.offset.z = (Math.random() - 0.5) * 32.0
                }
            }

        }

        registerHandler<KeyPress> {
            if (key == Keyboard.KEY_J) unload()

            if (key == Keyboard.KEY_K) {
                perspective.enabled = false
            }
            if (key == Keyboard.KEY_I) {
                perspective.enabled = true
                wrapper.align.y = -0.5
                chest.scale = V3(3.0, 3.0, 3.0)
                chest.animate(0.8) {
                    chest.scale = V3(6.0, 6.0, 6.0)
                }

                wrapper.rotation.degrees = -1.0
                wrapper.animate(0.8) {
                    wrapper.rotation.degrees = Math.PI + 0.4
                }

                UIEngine.overlayContext.schedule(0.8) {
                    wrapper.animate(0.4, Easings.BACK_OUT) {
                        wrapper.rotation.degrees = Math.PI
                    }
                }
                itemRect.enabled = false

                wrapper.animate(1.1, Easings.BACK_OUT) {
                    wrapper.align.y = 0.5
                }
                chest.rotation.degrees = 0.1

                chestLid.rotation.degrees = 0.0

                ready = true

                glowRect.scale = V3()
                glowRect.color.alpha = 0.0

                UIEngine.overlayContext.schedule(1.2) {
                    itemRect.enabled = true
                    itemRect.offset.y = 50.0
                }
                UIEngine.overlayContext.schedule(1.4) {
                    itemRect.animate(0.3, Easings.BACK_OUT) {
                        offset.y = 0.0
                    }
                    glowRect.animate(0.2) {
                        scale = V3(1.0, 1.0, 1.0)
                        color.alpha = 0.8
                    }
                }
                UIEngine.overlayContext.schedule(1.4) {
                    chestLid.animate(0.2, Easings.BACK_OUT) {
                        rotation.degrees = -Math.PI * 0.5
                    }
                    chest.animate(0.2, Easings.BACK_OUT) {
                        rotation.degrees = 0.35
                    }
                }
            }
            if (key == Keyboard.KEY_O) {
                chestLid.animate(0.5, easing = Easings.BOUNCE_OUT) {
                    rotation.degrees = 0.0
                }
            }
        }

        registerHandler<RenderTickPre> {

            body1.rotation.degrees = (Mouse.getX() / Display.getWidth().toDouble() - 0.5) * Math.PI / 2
            body2.rotation.degrees = (Mouse.getY() / Display.getHeight().toDouble() - 0.5) * Math.PI / 2
            glowRect.rotation.degrees = -(Mouse.getX() / Display.getWidth().toDouble() - 0.5) * Math.PI / 2


//            c.rotation.degrees = (System.currentTimeMillis() % 2000) / 2000.0 * Math.PI * 2
        }


    }


}