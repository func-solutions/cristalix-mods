package ru.cristalix.mods.terminal

import KotlinMod
import dev.xdark.feder.NetUtil
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.Context3D
import ru.cristalix.uiengine.utility.*
import java.lang.Double.min

class TerminalViewMod: KotlinMod() {

    override fun onEnable() {

        UIEngine.initialize(this)

        val ctx = Context3D(V3(-91.0, 105.3, -53.95))

        ctx.size = V3(16.0 * 5, 16.0 * 3)
        ctx.color = Color(20, 20, 30, 1.0)

        val code = text {

            offset.x = 1.0
            offset.y = 1.0
            offset.z = -0.01
            scale = V3(0.25, 0.25, 1.0)

        }

        registerMessage("hw:code") {
            code.content = NetUtil.readUtf8(this)

            var scale = 0.25
            if (code.size.x > ctx.size.x * 4) scale = ctx.size.x / (code.size.x + 3.0)
            if (code.size.y > ctx.size.y * 4) scale = min(scale, ctx.size.y / (code.size.y + 3.0))
            code.scale = V3(scale, scale, 1.0)
        }

        ctx.addChild(code)

        UIEngine.worldContexts.add(ctx)

    }


}