package ru.cristalix.mods.flappy

import dev.xdark.clientapi.entity.EntityLiving
import dev.xdark.clientapi.entity.EntityLivingBase
import dev.xdark.clientapi.entity.EntityProvider
import dev.xdark.clientapi.event.entity.EntityInit
import dev.xdark.clientapi.event.lifecycle.GameTickPost
import dev.xdark.clientapi.resource.ResourceLocation
import org.lwjgl.input.Keyboard
import ru.cristalix.clientapi.KotlinMod
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.ContextGui
import ru.cristalix.uiengine.element.RectangleElement
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.utility.*
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.sqrt

class FlappyMod : KotlinMod() {

    override fun onEnable() {
        UIEngine.initialize(this)

        registerHandler<EntityInit> {

            clientApi.chat().printChatMessage("init " + entity.javaClass.name)

            if (entity is EntityLiving || entity is EntityLivingBase) {
                if (entity.displayName?.unformattedText == "мыш") {
                    entity.renderingEntity = clientApi.entityProvider().newEntity(EntityProvider.PIG, entity.world)
                }
            }

        }

        val textureLoader = TextureLoader(this)
        textureLoader.loadTextures().thenAccept {

            var running = true
            val contextGui = ContextGui()

            val zabelov = rectangle {
                size = V3(32.0, 32.0)
                color = WHITE
                textureFrom = V2(8.0 / 64.0, 8.0 / 64.0)
                textureSize = V2(8.0 / 64.0, 8.0 / 64.0)
                scale.y = -1.0
                textureLocation = ResourceLocation.of("delfikpro", "308380a9-2c69-11e8-b5ea-1cb72caa35fd")

                addChild(rectangle {
                    origin = V3(0.5, -0.3)
                    align = CENTER
                    textureLocation = ResourceLocation.of("delfikpro", "308380a9-2c69-11e8-b5ea-1cb72caa35fd")
                    size = V3(32.0, 48.0)
                    textureFrom = V2(20.0/64.0, 20.0/64.0)
                    textureSize = V2(8.0/64.0, 12.0/64.0)
                    color = WHITE
                    offset.z = -1.0
                })

                addChild(rectangle {
                    origin = CENTER
                    align = CENTER
                    color = WHITE
                    size = V3(32.0, 32.0)
                    textureFrom = V2(40.0 / 64.0, 8.0 / 64.0)
                    textureSize = V2(8.0 / 64.0, 8.0 / 64.0)
                    textureLocation = ResourceLocation.of("delfikpro", "308380a9-2c69-11e8-b5ea-1cb72caa35fd")
                    scale = V3(9.0/8, 9.0/8, 1.0)
                })

            }



            class Taxi(val texture: String): RectangleElement() {
                init {
                    size = V3(64.0, 64.0)
                    textureLocation = ResourceLocation.of("delfikpro", texture)
                    color = WHITE
                    scale.y = -1.0
                    origin = CENTER
                }
            }

            val taxis = ArrayList<Taxi>()

            val world = rectangle {
                size = V3(400.0, 200.0)
                color = WHITE
                color.alpha = 0.2
                addChild(zabelov)
                scale.y = -1.0
                origin = CENTER
                align = CENTER

            }

            val score = text {
                offset = V3(2.0, 2.0)
                scale = V3(3.0, 3.0, 1.0)
                shadow = true
            }
            contextGui.addChild(score, text {

                offset = V3(2.0, 30.0)
                shadow = true
                content = "Потрачено"

            })

            contextGui.addChild(world)

            var motionX = 0.0
            var motionY = 0.0

            var points = 0

            contextGui.onKeyTyped { _, i ->
                if (i == Keyboard.KEY_W || i == Keyboard.KEY_SPACE || i == Keyboard.KEY_UP) {
                    motionY = 20.0
                }
            }
            registerHandler<GameTickPost> {

                if (!running) return@registerHandler
                val dm = 4.0
                val mm = 14.0

                if (Keyboard.isKeyDown(Keyboard.KEY_D) || Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
                    motionX += dm
                    if (motionX > mm) motionX = mm
                }
                if (Keyboard.isKeyDown(Keyboard.KEY_A) || Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
                    motionX -= dm
                    if (motionX < -mm) motionX = -mm
                }


                if (motionX != 0.0 || motionY != 0.0) {
                    zabelov.animate(0.05) {
                        val l = sqrt(motionX * motionX + motionY * motionY)
                        zabelov.children[0].rotation.degrees = if (motionY > 0) asin(motionX / l) else if (motionX > 0) acos(motionY / l) else -acos(motionY / l)
                        motionX *= 0.8
                        motionY *= 0.95
                        if (motionY < -20.0) motionY = -20.0
                        if (zabelov.offset.y < 0.0) {
                            zabelov.offset.y = 0.0
                            if (motionY < 0.0) motionY = 0.0
                        }
                        zabelov.offset.x += motionX
                        zabelov.offset.y += motionY
                        motionY -= 2.0
                    }
                }

                val toremove = ArrayList<Taxi>()

                for (taxi in taxis) {

                    animate(0.05) {
                        taxi.offset.y -= 10.0
                    }
                    if (taxi.offset.y < -150.0) {
                        toremove.add(taxi)
                        world.removeChild(taxi)
                    }

                    val dx = taxi.offset.x - zabelov.offset.x
                    val dy = taxi.offset.y - zabelov.offset.y
                    if (dx * dx + dy * dy < 40 * 40) {

                        if (taxi.texture == "econom") {
                            clientApi.chat().printChatMessage("§c")
                            clientApi.chat().printChatMessage("§c Вы проиграли.")
                            clientApi.chat().printChatMessage("§c Забелов погиб.")
                            clientApi.chat().printChatMessage("§6 Ваш счёт - $points.")
                            clientApi.chat().printChatMessage("§c")
                            taxi.color = Color(255, 0, 0, 1.0)

                            running = false

                            val defeat = text {
                                content = "§c§lПоражение"
                                align = CENTER
                                origin = CENTER
                                scale = V3(3.0, 3.0, 1.0)
                                shadow = true
                            }

                            contextGui.addChild(defeat)

                            UIEngine.schedule(1.0) {
                                zabelov.offset = V3()
                                points = 0
                                running = true
                                taxis.forEach { world.removeChild(it) }
                                taxis.clear()
                                contextGui.removeChild(defeat)
                            }

                        } else {
                            animate(0.5, Easings.QUINT_OUT) {
                                taxi.color = Color(0, 255, 0, 0.0)
                                taxi.scale = V3(1.5, -1.5, 1.0)
                            }
                            UIEngine.schedule(0.5) {
                                world.removeChild(taxi)
                            }
                            points += 7914
                            toremove.add(taxi)
                        }
                    }
                }

                taxis.removeAll(toremove)

                while (taxis.size < 7) {
                    taxis.add(Taxi(if (Math.random() > 0.5) "komfortplus" else "econom").apply {
                        offset.x = Math.random() * world.size.x
                        offset.y = world.size.y + 150.0 + 150.0 * Math.random()
                        world.addChild(this)
                    })
                }

                score.content = "§6$points руб."



            }

            contextGui.open()

        }

//        val zabelov = rectangle {
//
//            size = V3(8.0, 8.0)
//
//        }
//        contextGui.addChild(zabelov)

    }

}