package ru.cristalix.mods.cosmetics

import dev.xdark.clientapi.item.Item
import dev.xdark.clientapi.item.ItemStack
import dev.xdark.clientapi.nbt.NBTTagCompound
import dev.xdark.clientapi.nbt.NBTTagString
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.utility.*

const val margin = 4.0

class CosmeticsScreen {

    val buttonCrates = button {
        size.x = buttonSize * 2 + margin
        offset.x = (buttonSize + margin) * 0
        offset.y = (buttonSize + margin) * 0
        icon.stack = ItemStack.of(Item.of("minecraft:chest"), 1, 0)
        title.content = "Кейсы"
    }

    val buttonGadgets = button {
        offset.x = (buttonSize + margin) * 0
        offset.y = (buttonSize + margin) * 1
        icon.stack = ItemStack.of(Item.of("minecraft:blaze_rod"), 1, 0)
        title.content = "Гаджеты"
    }

    val buttonPets = button {
        offset.x = (buttonSize + margin) * 1
        offset.y = (buttonSize + margin) * 1
        val item = ItemStack.of(Item.of("minecraft:clay_ball"), 1, 0)
        item.tagCompound = NBTTagCompound.of(mapOf("other" to NBTTagString.of("pets")))
        icon.stack = item
        title.content = "Питомцы"
    }

    val buttonOutfit = button {
        offset.x = (buttonSize + margin) * 0
        offset.y = (buttonSize + margin) * 2
        val item = ItemStack.of(Item.of("minecraft:clay_ball"), 1, 0)
        item.tagCompound = NBTTagCompound.of(mapOf("other" to NBTTagString.of("clothes")))
        icon.stack = item
        title.content = "Одежда"
    }

    val buttonMorphs = button {
        offset.x = (buttonSize + margin) * 1
        offset.y = (buttonSize + margin) * 2
        val item = ItemStack.of(Item.of("minecraft:spawn_egg"), 1, 0)
        item.tagCompound = NBTTagCompound.of(
            mapOf(
                "EntityTag" to NBTTagCompound.of(
                    mapOf("id" to NBTTagString.of("minecraft:spider"))
                )
            )
        )
        icon.stack = item
        title.content = "Дизгайсы"
    }

    val buttonParticles = button {
        offset.x = (buttonSize + margin) * 2
        offset.y = (buttonSize + margin) * 0
        val item = ItemStack.of(Item.of("minecraft:clay_ball"), 1, 0)
        item.tagCompound = NBTTagCompound.of(mapOf("other" to NBTTagString.of("unique")))
        icon.stack = item
        title.content = "Частицы"
    }

    val buttonSmiles = button {
        offset.x = (buttonSize + margin) * 2
        offset.y = (buttonSize + margin) * 1
        val item = ItemStack.of(Item.of("minecraft:spawn_egg"), 1, 0)
        item.tagCompound = NBTTagCompound.of(
            mapOf(
                "EntityTag" to NBTTagCompound.of(
                    mapOf("id" to NBTTagString.of("minecraft:ghast"))
                )
            )
        )
        icon.stack = item
        title.content = "Эмоции"
    }

    val contentWrapper = rectangle {
        size.y = buttonSize * 3 + margin * 2
        size.x = margin
        align = CENTER
        origin = CENTER
        addChild(
            buttonCrates,
            buttonGadgets,
            buttonPets,
            buttonOutfit,
            buttonMorphs,
            buttonParticles,
            buttonSmiles
        )
    }

    val body = rectangle {

        size.x = 200.0
        size.y = UIEngine.overlayContext.size.y
        addChild(contentWrapper)
        UIEngine.overlayContext.addChild(this)

    }

}