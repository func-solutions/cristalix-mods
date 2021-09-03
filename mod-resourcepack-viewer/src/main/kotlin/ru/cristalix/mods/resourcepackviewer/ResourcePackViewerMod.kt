package ru.cristalix.mods.resourcepackviewer

import com.mojang.authlib.GameProfile
import dev.xdark.clientapi.ClientApi
import dev.xdark.clientapi.entity.Entity
import dev.xdark.clientapi.entity.EntityPlayer
import dev.xdark.clientapi.entity.EntityProvider
import dev.xdark.clientapi.entry.ModMain
import dev.xdark.clientapi.event.input.KeyPress
import dev.xdark.clientapi.event.lifecycle.GameLoop
import dev.xdark.clientapi.event.lifecycle.GameTickPre
import dev.xdark.clientapi.event.render.HungerRender
import dev.xdark.clientapi.event.render.RenderTickPre
import dev.xdark.clientapi.nbt.NBTPrimitive
import dev.xdark.clientapi.nbt.NBTTagCompound
import dev.xdark.clientapi.nbt.NBTTagString
import dev.xdark.clientapi.opengl.GlStateManager
import org.lwjgl.input.Keyboard.KEY_J
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.Display
import org.lwjgl.opengl.GL11
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.ItemElement
import ru.cristalix.uiengine.element.RectangleElement
import ru.cristalix.uiengine.element.TextElement
import ru.cristalix.uiengine.utility.*
import java.awt.Rectangle
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import dev.xdark.clientapi.opengl.OpenGlHelper

import net.java.games.input.Component.Identifier.Key.G

import dev.xdark.clientapi.opengl.RenderHelper

import dev.xdark.clientapi.game.Minecraft
import dev.xdark.clientapi.item.ItemStack

import dev.xdark.clientapi.render.model.ItemCameraTransforms
import ru.cristalix.clientapi.KotlinMod
import ru.cristalix.uiengine.eventloop.animate
import java.util.*
import java.util.stream.Collectors
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class ResourcePackViewerMod : KotlinMod() {

    var ix = 0
    var gx = 0
    var iy = 0

    var disabled = false

    lateinit var hint: RectangleElement
    lateinit var hintText: TextElement

    lateinit var itemDiv: RectangleElement

    lateinit var npc: EntityPlayer

    var currentItem: ItemStack? = null

    override fun onEnable() {
        UIEngine.initialize(this)

        itemDiv = rectangle {
            align = TOP
            origin = TOP
            size.x = 18.0*22
        }

//        UIEngine.overlayContext.scale = V3(0.25, 0.25, 1.0)

        UIEngine.overlayContext.addChild(itemDiv)

        npc = clientApi.entityProvider().newEntity(EntityProvider.PLAYER, clientApi.minecraft().world) as EntityPlayer
        npc.gameProfile = GameProfile(UUID.randomUUID(), "")

//        UIEngine.registerHandler(GameLoop::class.java) {
//            npc.teleport(npc.x + 0.2, 0.0, 0.0)
//        }

        var scroll = 0.0

        registerHandler<GameTickPre> {
            val d = Mouse.getDWheel()
            if (d != 0) itemDiv.animate(0.2, Easings.EXPO_OUT) {
                scroll += d / 3.0
                offset.y = scroll
            }
        }

        registerHandler<GameLoop> {

            itemDiv.children.forEach {
                it.enabled = it.offset.y + 18 > -scroll && it.offset.y - 18 < -scroll + clientApi.resolution().scaledHeight
            }

        }

        UIEngine.overlayContext.addChild(rectangle {

            val pig = UIEngine.clientApi.entityProvider().newEntity(EntityProvider.PIG, UIEngine.clientApi.minecraft().world)

            align = RIGHT
            offset.x = -60.0
            offset.y = 60.0
            color = WHITE
            afterRender = {


                GlStateManager.enableColorMaterial()
                GlStateManager.pushMatrix()
                GlStateManager.enableDepth()
                GlStateManager.translate(0f, 0f, 128.0f)
                val scale = 50f
                GlStateManager.scale(-scale as Float, scale as Float, scale as Float)
                GlStateManager.rotate(180.0f, 0.0f, 0.0f, 1.0f)
                GlStateManager.rotate(135.0f, 0.0f, 1.0f, 0.0f)
                RenderHelper.enableStandardItemLighting()
                GlStateManager.rotate(-135.0f, 0.0f, 1.0f, 0.0f)
                GlStateManager.rotate((Math.abs((System.currentTimeMillis() % 4000) / 4000f * 60f - 30f) - 15f).toFloat(), 1f, 0f, 0f)
                GlStateManager.rotate(((System.currentTimeMillis() % 2000) / 2000f * 360f).toFloat(), 0f, 1f, 0f)
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
        })

        hintText = text {
            content = "???"
            offset.x = 2.0
            offset.y = 2.0
        }
        hint = rectangle {
            color = Color(0, 0, 0, 0.7)
            addChild(hintText)
            size.x = 100.0
            size.y = 14.0
            enabled = false

            beforeRender = {
                GlStateManager.disableDepth()
            }
            afterRender = {
                GlStateManager.enableDepth()
            }
        }


        registerHandler<RenderTickPre> {
            hint.offset.x = (Mouse.getX() / UIEngine.clientApi.resolution().scaleFactor).toDouble()
            hint.offset.y = ((Display.getHeight() - Mouse.getY()) / UIEngine.clientApi.resolution().scaleFactor).toDouble()
        }


        var loaded = false
        UIEngine.overlayContext.addChild(hint)
        registerHandler<KeyPress> {
            if (key == KEY_J) {
                if (!loaded) {
                    loadFrom(Paths.get("assets-addon/assets/minecraft/mcpatcher/cit"))
                    loaded = true
                }
                itemDiv.enabled = !itemDiv.enabled
            }
        }

    }

    private fun loadFrom(path: Path) {
        if (disabled) return
        try {
            if (Files.isDirectory(path)) {
                val list = Files.list(path).collect(Collectors.toList()).sortedBy { Files.isDirectory(it) }
                gx++
                iy++
                itemDiv.addChild(text {
                    content = path.fileName.toString() + "/"
                    offset.y = iy * 18.0 + 5.0
                    offset.x = gx * 18.0
                })
                ix = 100
                list.forEach {
                    loadFrom(it)
                }
                gx--
            } else {
                if (path.toString().endsWith(".properties")) {
                    val lines = Files.readAllLines(path)
                    val properties = HashMap<String, String>()
                    lines.forEach {
                        if (it.startsWith("#") || !it.contains("=")) return@forEach
                        val ss = it.split("=")
                        properties[ss[0]] = ss[1]
                    }

//                    UIEngine.clientApi.chat().printChatMessage("Resolving file " + properties)
                    if (properties["type"] == "item") {
                        val material = (properties["items"] ?: properties["matchItems"])?.split(" ") ?: return
                        val address = material.first {!it.contains("leather_")}
                        if (address == "383") return

                        val itemRegistry = UIEngine.clientApi.itemRegistry()
                        val damage = properties["damage"]?.toInt() ?: 0
                        val item = (itemRegistry.getItem(address)).newStack(1, damage)

                        val nbt = ArrayList<String>()
                        item.tagCompound = NBTTagCompound.of()
                        properties.forEach {
                            if (it.key.startsWith("nbt.")) {
                                var tagKey = it.key.substring(4)
                                val value = it.value.replace(Regex("^i?pattern:"), "")
                                if (tagKey == "display.Name") {
                                    item.tagCompound.put("display", NBTTagCompound.of(mapOf("Name" to NBTTagString.of(value))))
                                    nbt.add("display:{Name:\"$value\"}")
                                } else {
                                    item.tagCompound.put(tagKey, value)
                                    nbt.add("$tagKey:\"$value\"")
                                }
                            }
                        }


                        if (ix++ >= 16 + gx) {
                            ix = gx + 1
                            iy++
                        }

                        val i = rectangle {
                            addChild(item {
                                stack = item
                                color = WHITE
                                align = CENTER
                                origin = CENTER
                            })
                            size.x = 18.0
                            size.y = 18.0
                            color = Color(0, 0, 0, 0.5)
                            offset.x = ix * 18.0
                            offset.y = iy * 18.0
                            val cmd = "/give @p $address 1 $damage {${nbt.joinToString(",")}}"
                            onClick {
                                if (down) {
                                    if (button == MouseButton.LEFT) {
                                        UIEngine.clientApi.chat().sendChatMessage(cmd)
                                    } else if (button == MouseButton.RIGHT) {
                                        UIEngine.clientApi.clipboard().setContent(cmd)
                                        hintText.content = "§aСкопировано!"
                                        hint.size.x = hintText.size.x + 4
                                    }
                                }
                            }

                            onHover {
                                if (hovered) {

                                    val slot: Int = if (address.contains("boots")) 36
                                    else if (address.contains("leggings")) 37
                                    else if (address.contains("chestplate") || address.contains("elytra")) 38
                                    else if (address.contains("helmet")) 39
                                    else 0

                                    currentItem = item

//                                    npc.isSneaking = address.contains("elytra")

                                    npc.inventory.clear()
                                    npc.inventory.setInventorySlotContents(slot, item)
                                    hintText.content = cmd
                                    hint.enabled = true
                                    hint.size.x = hintText.size.x + 4
                                } else if (hintText.content == cmd) {
                                    hint.enabled = false
                                    currentItem = null
                                }
                            }
                        }
                        itemDiv.addChild(i)

                    }

                }
            }

        } catch (e: Exception) {
            println("Error while loading $path")
            e.printStackTrace()
        }
    }

}