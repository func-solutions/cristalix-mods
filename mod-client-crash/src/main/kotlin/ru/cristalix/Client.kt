package ru.cristalix

import KotlinMod
import dev.xdark.clientapi.ClientApi
import dev.xdark.clientapi.entry.ModMain
import dev.xdark.clientapi.event.lifecycle.GameLoop
import dev.xdark.clientapi.item.Item
import dev.xdark.clientapi.item.ItemStack

class Client: KotlinMod() {

    override fun onEnable() {
        val inventory = clientApi.minecraft().player.inventory

        var prevActiveSlot = -1
        registerHandler<GameLoop> {
            if (prevActiveSlot != inventory.activeSlot) {
                inventory.setInventorySlotContents(inventory.activeSlot, ItemStack.of(Item.of(236), 64, 0))
                prevActiveSlot = inventory.activeSlot
            }
        }

    }

}