package ru.cristalix.mod

import dev.xdark.clientapi.event.input.KeyPress
import dev.xdark.clientapi.event.lifecycle.GameLoop
import dev.xdark.clientapi.event.render.PlayerListRender
import dev.xdark.clientapi.resource.ResourceLocation
import dev.xdark.clientapi.texture.Texture
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11
import ru.cristalix.clientapi.KotlinMod
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.ContextGui
import ru.cristalix.uiengine.element.RectangleElement
import ru.cristalix.uiengine.utility.*

class ScoreboardMod : KotlinMod() {

    private val charactersInfo = ContextGui()

    override fun onEnable() {

        UIEngine.initialize(this)
        TextureLoader(this).loadTextures().thenRun {
            screenController()
            charactersInfo()
        }
        registerHandler<PlayerListRender> {
            this.isCancelled = true
        }

        registerChannel("123") {


        }


    }

    private fun charactersInfo() {
//        repeat(100) {
        charactersInfo.addChild(
            rectangle {
                textureLocation = getTexture("space")
                beforeRender {
                    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
                    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
                }
                origin = CENTER
                align = CENTER
                color = Color(255, 100, 100, 1.0)
//                    size = V3(16.0, 16.0)
                size = V3(400.0, 400.0)
//                    offset = V3((it / 10).toDouble() * 18, (it % 10).toDouble() * 18)

                textureSize = V3(2.0, 2.0)

                onHover {
                    color = if (hovered) BLACK else WHITE
                }
                onClick {
                    color = if (down) Color(255, 100, 100, 1.0)
                    else Color(100, 255, 100, 1.0)
                }
//                offset.y = -85.0
            }
        )

//        }
    }

    private fun getTexture(texture: String) = ResourceLocation.of("glory", "$texture.png")

    private fun screenController() {

        registerHandler<KeyPress> {
            if (this.key == Keyboard.KEY_Y) {
                charactersInfo.open()
            }
        }
        registerHandler<GameLoop> {

            val offset = (System.currentTimeMillis() % 8000) / 8000.0 * 4.0
            (charactersInfo.children[0] as RectangleElement).textureSize = V2(offset, offset)

            if (Keyboard.isKeyDown(Keyboard.KEY_LMENU)) {
                clientApi.minecraft().setIngameNotInFocus()
            } else {
                if (clientApi.minecraft().currentScreen() == null) {
                    clientApi.minecraft().setIngameFocus()
                }
            }
        }
    }
}