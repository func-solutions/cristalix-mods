@file:Suppress("LocalVariableName")

package ru.cristalix.mods.balance

import ru.cristalix.clientapi.KotlinMod
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.RectangleElement
import ru.cristalix.uiengine.element.TextElement
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.utility.*
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.log10
import kotlin.math.pow

//fun main() {
//
//    val a_long: Long = 666666666666666666
//    val b_long: Long = 333333333333333333
//    val diff_long = a_long - b_long
//
//    val a_double: Double = a_long.toDouble()
//    val b_double: Double = b_long.toDouble()
//    val diff_double = (a_double - b_double).toLong()
//
//    println(diff_long)
//    println(diff_double)
//}
//fun main() {
//    fun a(d: Double) = println("$d : " + commas(d))
//    a(0.0) // 0.0 = 0
//    a(0.00001) // 0.00001 = 0
//    a(0.000001) // 0.000001 = 0
//    a(0.1) // 0.1 = 0.1
//    a(0.01) // 0.01 = 0.01
//    a(1.0) // 1.0 = 1
//    a(1.01) // 1.01 = 1.01
//    a(1.000001) // 1.000001 = 1
//    a(100.000001) // 100.000001 = 100
//    a(100.0) // 100.0 = 100
//    a(1000.0) // 1000.0 = 1,000
//    a(1000.01) // 1000.01 = 1,000.01
//    a(-1000.01) // -1000.01 = -1,000.01
//    a(-10000.01) // -10000.01 = -10,000.01
//    a(-5234234.985) // -5234234.985 = -5,234,234.985
//    a(-2423434234.985) // -2423434234.985 = -2,423,434,234.985
//    a(-24234342545345.985) // -24234342545345.985 = -24,234,342,545,345.9843
//    a(24_234_342_545_345.985) // 24234342545345.985 = 24,234,342,545,345.9843
//}

fun commas(n: Double): String {
    if (n == 0.0) return "0"
    var whole = abs(n)
    val wholeDigitCount = log10(whole).toInt() + 1
    var str = if (n < 0) "-" else ""
    if (wholeDigitCount > 0) {
        repeat(wholeDigitCount) {
            val digitShifted = 10.0.pow(wholeDigitCount - it - 1)
            val digit = (whole / digitShifted).toInt()
            val newWhole = whole % digitShifted
            str += digit
            if ((wholeDigitCount - it) % 3 == 1 && wholeDigitCount != it + 1) str += ","
            whole = newWhole
        }
    } else {
        str += "0"
    }

    whole = (whole + 0.000005) * 10_000
    if (whole != 0.0 && n < 100000) {

        val fraction = (10_000 + whole).toInt()
        var fractionStr = fraction.toString()
        while (fractionStr.endsWith("0")) fractionStr = fractionStr.substring(0, fractionStr.length - 1)
        fractionStr = fractionStr.substring(1)
        if (fractionStr.isNotEmpty()) str += ".$fractionStr"
    }

    return str
}

//fun commas(n: Double): String {
//    var newStr = ""
//    var digits = -1
////    val ss = n.toInt().toString().split('.')
//    val str = n.toInt().toString()
//    val d1 = (n - n.toInt()).toFloat()
//
//    var d = "$d1"
//    if (d.indexOf('.') >= 0) d = d.substring(d.indexOf('.') + 1)
//
//    str.reversed().forEach {
//        if (it == '-') newStr = "-$newStr"
//        else {
//            newStr = it + if (digits++ == 2) ",$newStr" else "" + newStr
//            if (digits == 3) digits = 0
//        }
//    }
//
//    if (newStr.isEmpty()) newStr = "0"
//
//
//    return if (d1 < 0.0001) newStr else "$newStr.$d"
//}


class BalanceMod : KotlinMod() {
    private var balance = 0.0

    private lateinit var balanceText: TextElement

    private lateinit var box: RectangleElement

    override fun onEnable() {
        UIEngine.initialize(this)

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

        registerChannel("balance") {
            val newBalance = readDouble()
            addBalance(newBalance - balance)
            balance = newBalance
            balanceText.content = pretty()
        }
//        registerHandler<KeyPress> {
//            if (key == KEY_J) {
//                val change = (Math.random() * 80374587 - 4000000).toInt()
//                addBalance(change.toDouble())
//                balance += change
//                balanceText.content = pretty()
//            }
//        }


    }


    private fun pretty(balance: Double = this.balance) = "Баланс §8» §6${commas(balance)}"

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

        UIEngine.schedule(0.1) {
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

        UIEngine.schedule(0.5) {
            box.removeChild(text)
        }

    }

}