package ru.cristalix.mods.prison

import dev.xdark.clientapi.item.ItemStack
import dev.xdark.clientapi.item.ItemTool
import dev.xdark.clientapi.item.ItemTools
import ru.cristalix.clientapi.KotlinMod
import ru.cristalix.clientapi.readArray
import ru.cristalix.clientapi.readUtf8
import ru.cristalix.clientapi.readVarInt
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.utility.*

class PrisonMod: KotlinMod() {

    override fun onEnable() {

        UIEngine.initialize(this)


        val fadeItems = rectangle {

        }

        val frontItems = rectangle {

        }

        val background = rectangle {
            color.alpha = 0.65
            size = V3(100.0, 200.0)
        }

        val levelText = text {
            offset.y = 2.0
            align = TOP
            origin = TOP
        }

        val body = rectangle {
            addChild(fadeItems, background, levelText, frontItems)
            align = LEFT
            offset.x = 50.0
        }


        data class ItemData(
            val itemStack: ItemStack,
            val title: String,
            val current: Int,
            val required: Int
        )

        registerChannel("prison:addinfos") {
            val level = readInt()
            val percentage = readInt()
            val itemsArray = readArray {
                ItemData(
                    ItemTools.read(this),
                    readUtf8(),
                    readVarInt(),
                    readVarInt()
                )
            }

            val levelUpdates = readArray { readUtf8() }


            val items = itemsArray.toList().sortedBy { it.current == it.required }

            var x = 0
            var y = 0
            val columns = 2

            var background = false

            for (item in items) {

                if (item.required == item.current && !background) {
                    background = true
                    x = 0
                    if (x != 0) {
                        y++
                    }
                }

                val container = if (background) fadeItems else frontItems

                container.addChild(rectangle {
                    offset = V3(x * 120.0, y * 50.0)
                    addChild(item {
                        stack = item.itemStack
                        scale = V3(2.0, 2.0, 2.0)
                    })
                    addChild(text {
                        content = item.title
                        offset.x = 34.0
                    })
                })

                if (++x == columns) {
                    x = 0
                    y++
                }

            }

        }


        UIEngine.overlayContext.addChild(body)

    }

}