package ru.cristalix.mods.cosmetics

import dev.xdark.clientapi.item.Item
import dev.xdark.clientapi.item.ItemStack
import dev.xdark.clientapi.nbt.NBTBase
import dev.xdark.clientapi.nbt.NBTTagCompound
import dev.xdark.clientapi.nbt.NBTTagString

inline fun item(id: String, builder: ItemStack.() -> Unit = {}): ItemStack =
    ItemStack.of(Item.of("minecraft:$id"), 1, 0).also(builder)

inline var ItemStack.nbt: Pair<String, Any>?
set(value) {
    val obj = value!!.second
    @Suppress("UNCHECKED_CAST")
    tagCompound = NBTTagCompound.of(mapOf(value.first to when (obj) {
        is String -> NBTTagString.of(obj)
        is Map<*, *> -> NBTTagCompound.of(obj as MutableMap<String, NBTBase>?)
        else -> null
    }
    ))
}
get() = null