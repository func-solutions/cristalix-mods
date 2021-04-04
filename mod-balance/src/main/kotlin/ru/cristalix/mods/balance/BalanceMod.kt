package ru.cristalix.mods.balance

import dev.xdark.clientapi.ClientApi
import dev.xdark.clientapi.entry.ModMain
import dev.xdark.clientapi.event.input.KeyPress
import dev.xdark.clientapi.event.network.PluginMessage
import org.lwjgl.input.Keyboard.KEY_J
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.RectangleElement
import ru.cristalix.uiengine.element.TextElement
import ru.cristalix.uiengine.element.animate
import ru.cristalix.uiengine.utility.*
import kotlin.math.absoluteValue

class BalanceMod : ModMain {

    private var balance = 0.0
    private lateinit var balanceText: TextElement

    private lateinit var box: RectangleElement

    override fun load(clientApi: ClientApi) {
        UIEngine.initialize(clientApi)

        balanceText = text {
            content = pretty()

            origin = ru.cristalix.uiengine.utility.Relative.BOTTOM_RIGHT
            shadow = true
        }

        box = rectangle {
            align = Relative.BOTTOM_RIGHT
            offset = V3(-3.0, -14.0)
            addChild(balanceText)
        }

        UIEngine.overlayContext.addChild(box)

        clientApi.messageBus().register(clientApi.messageBus().createListener(), PluginMessage::class.java, {
            if (it.channel == "balance") {
                val newBalance = it.data.readDouble()
                addBalance(newBalance - balance)
                balance = newBalance
                balanceText.content = pretty()
            }
        }, 1)

//        UIEngine.registerHandler(KeyPress::class.java, {
//            if (key == KEY_J) {
//                val change = Math.random() * 8374587 - 4000000
//                addBalance(change)
//                balance += change
//                balanceText.content = pretty()
//            }
//        })


    }

    private fun pretty(balance: Double = this.balance) = "Баланс §8» §6${commas(balance)}"

    private fun commas(n: Double): String {
        var newStr = ""
        var digits = -1
        val ss = n.toString().split('.')
        val str = ss[0]

        str.reversed().forEach {
            newStr = it + if (digits++ == 2) ",$newStr" else "" + newStr
            if (digits == 3) digits = 0
        }
        return if (ss.size < 2) newStr else (newStr + '.' + ss[1])
    }

    private fun addBalance(balance: Double) {
        val sign = if (balance < 0) "-" else "+"
        val text = text {
            content = sign + commas(balance.absoluteValue)
            color = Color(255, 0xAA, 0, 1.0)
            origin = Relative.BOTTOM_RIGHT
            offset = V3(y = -10.0)
            shadow = true
        }
        balanceText.animate(0.1, Easings.CUBIC_OUT) {
            offset.y = -3.0
        }

        UIEngine.overlayContext.schedule(0.1) {
            balanceText.animate(0.4, Easings.BACK_OUT) {
                offset.y = 0.0
            }
        }

        box.addChild(text)

        text.animate(0.5, Easings.QUAD_OUT) {
            offset.x = -40.0
            offset.y = -20 * Math.random()
            color.alpha = 0.0
        }

        UIEngine.overlayContext.schedule(0.5) {
            box.removeChild(text)
        }

    }

    override fun unload() {
        UIEngine.uninitialize()
    }
}