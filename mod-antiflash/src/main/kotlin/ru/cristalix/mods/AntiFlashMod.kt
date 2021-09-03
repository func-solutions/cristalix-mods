package ru.cristalix.mods

import dev.xdark.clientapi.event.lifecycle.GameTickPre
import dev.xdark.clientapi.event.render.BlockLayerRender
import dev.xdark.clientapi.event.render.RenderPass
import dev.xdark.clientapi.opengl.GlStateManager
import dev.xdark.clientapi.resource.ResourceLocation
import dev.xdark.clientapi.util.BlockRenderLayer
import org.lwjgl.opengl.GL11
import ru.cristalix.clientapi.KotlinMod
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.Context3D
import ru.cristalix.uiengine.utility.*

class AntiFlashMod: KotlinMod() {


    override fun onEnable() {

        UIEngine.initialize(this)

        val box = Context3D(V3())

//        box.beforeRender = {
//            GlStateManager.disableDepth()
//            GlStateManager.disableCull()
//            GlStateManager.disableAlpha()
//            GlStateManager.disableBlend()
//        }
//        box.afterRender = {
//            GlStateManager.enableDepth()
//        }

        box.scale = V3(5.0, 5.0, 5.0)

        registerHandler<RenderPass> {

//            GlStateManager.matrixMode(GL11.GL_PROJECTION)
//            GlStateManager.popMatrix()
//            GlStateManager.loadIdentity()
//            GL11.glOrtho(-10.0, 10.0, -10.0, 10.0, 0.01, 300.0);
//            clientApi.chat().printChatMessage("renderpass $pass")
        }

        box.addChild(
            rectangle {
                offset.z = 1.0
                origin = CENTER
                size = V3(2.0, 2.0)
                color = BLACK
                textureLocation = ResourceLocation.of("minecraft", "textures/blocks/stone.png")
            },
            rectangle {
                offset.z = -1.0
                origin = CENTER
                size = V3(2.0, 2.0)
                color = BLACK
                rotation = Rotation(Math.PI, 0.0, 1.0, 0.0)
                textureLocation = ResourceLocation.of("minecraft", "textures/blocks/stone.png")
            },
            rectangle {
                offset.y = 1.0
                origin = CENTER
                size = V3(2.0, 2.0)
                color = BLACK
                rotation = Rotation(-Math.PI / 2, 1.0, 0.0, 0.0)
                textureLocation = ResourceLocation.of("minecraft", "textures/blocks/stone.png")
            },
            rectangle {
                offset.y = -1.0
                origin = CENTER
                size = V3(2.0, 2.0)
                color = BLACK
                rotation = Rotation(Math.PI / 2, 1.0, 0.0, 0.0)
                textureLocation = ResourceLocation.of("minecraft", "textures/blocks/stone.png")

            },
            rectangle {
                offset.x = -1.0
                origin = CENTER
                size = V3(2.0, 2.0)
                color = BLACK
                rotation = Rotation(-Math.PI / 2, 0.0, 1.0, 0.0)
                textureLocation = ResourceLocation.of("minecraft", "textures/blocks/stone.png")
            },
            rectangle {
                offset.x = 1.0
                origin = CENTER
                size = V3(2.0, 2.0)
                color = BLACK
                rotation = Rotation(Math.PI / 2, 0.0, 1.0, 0.0)
                textureLocation = ResourceLocation.of("minecraft", "textures/blocks/stone.png")
            }
        )

        UIEngine.worldContexts.add(box)

        registerHandler<GameTickPre> {
            val player = clientApi.minecraft().player
            box.offset = V3(player.x, player.y + player.eyeHeight, player.z)

            var blind = false
            for (potionEffect in player.activePotionEffects) {
                if (potionEffect.potion.id == 15) blind = true
            }
            box.enabled = blind
        }

        registerHandler<BlockLayerRender> {

//            clientApi.chat().printChatMessage("layer ${layer == BlockRenderLayer.SOLID} " +
//                    "${layer == BlockRenderLayer.CUTOUT} ${layer == BlockRenderLayer.CUTOUT_MIPPED} " +
//                    "${layer == BlockRenderLayer.TRANSLUCENT}")
//            GlStateManager.disableDepth()
            box.transformAndRender()
//            GlStateManager.enableDepth()

        }

    }


}