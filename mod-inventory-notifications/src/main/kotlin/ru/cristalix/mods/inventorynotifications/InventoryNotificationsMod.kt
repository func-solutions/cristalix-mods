package ru.cristalix.mods.inventorynotifications

import dev.xdark.clientapi.entity.EntityProvider
import dev.xdark.clientapi.event.lifecycle.GameLoop
import dev.xdark.clientapi.event.render.GuiOverlayRender
import dev.xdark.clientapi.event.render.RenderTickPost
import dev.xdark.clientapi.opengl.GlStateManager
import dev.xdark.clientapi.opengl.OpenGlHelper
import dev.xdark.clientapi.opengl.RenderHelper
import dev.xdark.clientapi.resource.ResourceLocation
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11
import ru.cristalix.clientapi.KotlinMod
import ru.cristalix.clientapi.readUtf8
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.Context2D
import ru.cristalix.uiengine.element.RectangleElement
import ru.cristalix.uiengine.utility.*
import kotlin.math.abs

class InventoryNotificationsMod : KotlinMod() {

    var elem: RectangleElement? = null

    val context = Context2D(V3())


    override fun onEnable() {
        UIEngine.initialize(this)

        val pig = UIEngine.clientApi.entityProvider().newEntity(EntityProvider.PIG, UIEngine.clientApi.minecraft().world)

        UIEngine.overlayContext.addChild(rectangle {
            size = V3(160.0, 180.0)
//            color = WHITE
            origin = RIGHT
            align = V3(0.48, 0.5)
            addChild(
                text {
                    origin = TOP
                    align = TOP
                    offset.y = 10.0
                    scale = V3(1.5, 1.5)
                    content = "Спавнер"
                },
                rectangle {
                    align = CENTER

//                    scale = V3(3.0, 3.0, 3.0)
                    color = Color(255, 255, 255, 0.5)
                    afterRender = {
                        GlStateManager.enableColorMaterial()
                        GlStateManager.pushMatrix()
                        GlStateManager.enableDepth()
                        GlStateManager.translate(0f, 0f, 128.0f)
                        val scale = 50f
                        GlStateManager.scale(-scale, scale, scale)
                        GlStateManager.rotate(180.0f, 0.0f, 0.0f, 1.0f)
                        GlStateManager.rotate(135.0f, 0.0f, 1.0f, 0.0f)
                        RenderHelper.enableStandardItemLighting()
                        GlStateManager.rotate(-135.0f, 0.0f, 1.0f, 0.0f)
                        GlStateManager.rotate((abs((System.currentTimeMillis() % 8000) / 8000f * 60f - 30f) - 15f), 1f, 0f, 0f)
                        GlStateManager.rotate(((System.currentTimeMillis() % 4000) / 4000f * 360f), 0f, 1f, 0f)
                        pig.setPitch(15f)

                        // ToDo: Uncomment
                        UIEngine.clientApi.minecraft().entityRenderManager
                            .renderEntity(pig, 0.0, 0.0, 0.0, 0f, 0f, true)

                        GlStateManager.popMatrix()
                        RenderHelper.disableStandardItemLighting()
                        GlStateManager.disableRescaleNormal()
                        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit)
                        GlStateManager.disableTexture2D()
                        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit)
                    }
                }
            )
        })

        context.beforeRender {
            clientApi.fontRenderer().drawString(" ", 0f, 0f, -1, false)
            GlStateManager.disableLighting()
            GL11.glColor4f(1f, 1f, 1f, 1f)
//            GlStateManager.disableBlend()
//            GlStateManager.disableAlpha()

        }

        registerHandler<RenderTickPost>(2) {
            GlStateManager.disableLighting()
        }
//        registerHandler<RenderTickPost>(-1) {
//            GlStateManager.disableDepth()
//            GlStateManager.color(0f, 0f, 0f, 0f)
//        }

        context.afterRender {
//            GlStateManager.enableLighting()
//            GlStateManager.disableAlpha()
//            GlStateManager.disableBlend()
        }

//        render(
//            93.0, -110.0, 120.0, 40.0, "§e§lСкидки до 70%\n" +
//                    "Проверь ежедневное\n" +
//                    "предложение"
//        )

        registerChannel("invnotif") {
            val x = readDouble()
            val y = readDouble()
            val width = readDouble()
            val height = readDouble()
            val text = readUtf8()
            render(x, y, width, height, text)
        }

        registerChannel("invnotif:del") {
            context.children.clear()
        }

        registerHandler<RenderTickPost> {
            context.transformAndRender()
        }

    }

    fun render(x: Double, y: Double, width: Double, height: Double, text: String) {

        val lines = text.split("\n")

        context.addChild(rectangle {
            align = Relative.CENTER
            offset = V3(x, y)
            size = V3(width - 3, height - 3)
            color = WHITE
            val textureLoc = ResourceLocation.of("minecraft", "textures/gui/demo_background.png")
            textureLocation = textureLoc
            textureSize = V2((width - 3) / 256.0, (height - 3) / 256.0)
            addChild(rectangle {
                align = Relative.BOTTOM_LEFT
                size = V3(width - 3, 3.0)
                textureLocation = textureLoc
                textureFrom = V2(0.0, 163.0 / 256.0)
                textureSize = V2((width - 3) / 256.0, 3.0 / 256.0)
                color = WHITE
            })
            addChild(rectangle {
                align = Relative.TOP_RIGHT
                size = V3(3.0, height - 3.0)
                textureLocation = textureLoc
                textureFrom = V2(245.0 / 256.0, 0.0)
                textureSize = V2(3.0 / 256.0, (height - 3.0) / 256.0)
                color = WHITE
            })
            addChild(rectangle {
                align = Relative.BOTTOM_RIGHT
                size = V3(3.0, 3.0)
                textureLocation = textureLoc
                textureFrom = V2(245.0 / 256.0, 163.0 / 256.0)
                textureSize = V2(3.0 / 256.0, 3.0 / 256.0)
                color = WHITE
            })

            lines.forEachIndexed { i, text ->
                addChild(text {
                    offset.x = 5.0
                    offset.y = 5.0 + i * 10.0
                    color = BLACK
                    content = text
                })
            }

        })
    }

}