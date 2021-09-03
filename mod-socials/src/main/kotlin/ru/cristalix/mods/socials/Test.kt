package ru.cristalix.mods.socials

import java.nio.ByteBuffer

@ExperimentalUnsignedTypes
fun main() {

    val a1 = 45123
    val a2 = -76433

    val b1 = a1.toUInt() xor 0x00808080u
    val b2 = a2.toUInt() xor 0x00808080u

    val bb = ByteBuffer.allocate(4)

    bb.putInt(b1.toInt())


}


