package ru.cristalix.mods.terminal

import KotlinMod
import dev.xdark.clientapi.event.input.KeyPress
import dev.xdark.clientapi.gui.Screen
import dev.xdark.clientapi.opengl.GlStateManager
import dev.xdark.feder.NetUtil
import io.netty.buffer.Unpooled
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.Context2D
import ru.cristalix.uiengine.utility.*
import kotlin.math.log10

class TerminalMod : KotlinMod() {

    var actualCompletions: MutableList<String> = arrayListOf()
    var completing: String = ""
    val lines = rectangle {
        addChild(CodeLine())
    }

    var completions: List<String> = arrayListOf()

    val completionsText = text {
        offset.y = 1.0
        offset.x = 1.0
    }

    val completionsBox = rectangle {
        align = BOTTOM_RIGHT
        offset.y = 1.0
        size.x = 100.0
        size.y = 80.0
        color = Color(40, 40, 40, 0.8)
        addChild(completionsText)
    }

    val cursor = rectangle {
        size = V3(1.0, 10.0)
        color = WHITE
        color.alpha = 0.7
        addChild(completionsBox)
    }

    var lastSentSnaphot: String = ""

    var currentLineNumber = 0
    var currentPos = 0

    var sidebarWidth = 10.0

    fun updateCompletions() {
        actualCompletions = ArrayList()
        if (completing.isEmpty()) return

        completions.forEach {
            if (it.contains("$")) return@forEach
            if (it.substring(it.lastIndexOf('.') + 1).startsWith(completing))
                actualCompletions.add(it)
        }

        completionsText.content = actualCompletions.joinToString(separator = "\n") {
            "§f" + it.substring(it.lastIndexOf('.') + 1) + " §8" + it.substring(0, it.lastIndexOf('.'))
        }

        completionsBox.size.x = completionsText.size.x + 2.0
    }

    fun updateLinePositions() {
        sidebarWidth = (log10(lines.children.size + 1.0).toInt() * 7.0 + 7.0).coerceAtLeast(21.0)
        lines.children.forEachIndexed { i, e ->
            val codeLine = e as CodeLine
            codeLine.offset.y = i * 10.0
            codeLine.lineNumber.content = (i + 1).toString()
            codeLine.lineNumberBox.size.x = sidebarWidth
        }
    }

    val currentLine: CodeLine get() = lines.children[currentLineNumber] as CodeLine

    fun updateCursorPosition() {

        currentLineNumber = currentLineNumber.coerceIn(0, lines.children.size - 1)
        currentPos = currentPos.coerceIn(0, currentLine.code.length)

        val code = currentLine.code.substring(0, currentPos)
        val x = clientApi.fontRenderer().getStringWidth(code)
        cursor.offset.x = sidebarWidth + x.toDouble() + 1.0
        cursor.offset.y = currentLineNumber * 10.0
    }

    val lastKeyText = text {
        offset.x = -2.0
        offset.y = 2.0
        origin = TOP_RIGHT
        align = TOP_RIGHT
        color = Color(50, 255, 50, 1.0)
    }

    fun currentCode(formatting: Boolean): String {
        return lines.children.joinToString(separator = "\n") {
            if (formatting) (it as CodeLine).contents.content else (it as CodeLine).code
        }
    }

    override fun onEnable() {

        UIEngine.initialize(this)

        clientApi.clientConnection().sendPayload("hw:tab", Unpooled.EMPTY_BUFFER)
        registerMessage("hw:tab") {
            val amount = NetUtil.readVarInt(this)
            val l = ArrayList<String>(amount)
            completions = l
            repeat(amount) {
                l.add(NetUtil.readUtf8(this))
            }

            clientApi.chat().printChatMessage("§aReceived $amount completions.")

        }

        val terminal = rectangle {
            color = Color(20, 20, 30, 1.0)
            size = V3(600.0, 400.0)
            align = CENTER
            origin = CENTER
            addChild(lines)
        }

        var darkTheme = true

        terminal.addChild(rectangle {
            origin = BOTTOM_LEFT
            color = Color(100, 100, 100, 0.5)
            size = V3(40.0, 12.0)
            var themeText = text {
                content = "Тема"
            }
            addChild(themeText)

        })

        terminal.addChild(lastKeyText, cursor)

        val res = clientApi.resolution()
        val ctx = Context2D(V3(res.scaledWidth_double, res.scaledHeight_double))
        ctx.addChild(terminal)

        updateLinePositions()

        snapshotLoop()

        registerHandler<KeyPress> {

            if (key == Keyboard.KEY_I) {

                darkTheme = !darkTheme
                if (darkTheme) {
                    terminal.color = Color(20, 20, 30, 1.0)
                    lines.children.forEach {
                        it.color = WHITE
                    }
                } else {
                    terminal.color = Color(230, 230, 230, 1.0)
                    lines.children.forEach {
                        it.color = BLACK
                    }
                }
            }
            if (key == Keyboard.KEY_J) {
                unload()
            }

            if (key == Keyboard.KEY_Y) {
                val screen = Screen.Builder.builder()
                    .init { Keyboard.enableRepeatEvents(true) }
                    .close { Keyboard.enableRepeatEvents(false) }
                    .keyTyped { screen: Screen, char: Char, code: Int ->
                        try {

                            var input = "$char"

                            if (code == Keyboard.KEY_TAB) {
                                input = "    "
                            }

                            onKeyTyped(input, code)
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                        }


                    }
                    .draw { screen: Screen, x: Int, y: Int, partialTicks: Float ->
                        ctx.transformAndRender()
                    }.build()

                clientApi.minecraft().displayScreen(screen)
            }

        }

    }

    fun snapshotLoop() {

        val code = currentCode(true)
        if (lastSentSnaphot != code) {

            lastSentSnaphot = code
            val buffer = Unpooled.buffer()
            NetUtil.writeUtf8(code, buffer)
            clientApi.clientConnection().sendPayload("hw:code", buffer)

        }

        UIEngine.overlayContext.schedule(1) {
            snapshotLoop()
        }
    }

    private fun onKeyTyped(input: String, code: Int) {

        val line = lines.children[currentLineNumber] as CodeLine


        when (code) {

            // Backspace
            14 -> {
                if (currentPos == 0) {
                    if (currentLineNumber > 0) {
                        currentLineNumber--
                        currentPos = currentLine.code.length
                        currentLine.code += line.code
                        lines.removeChild(line)
                        updateLinePositions()
                    }
                } else {
                    line.code = line.code.substring(0, currentPos - 1) + line.code.substring(currentPos)
                    currentPos--
                }
            }

            // Escape
            1 -> {
                clientApi.minecraft().displayScreen(null)
            }

            // Enter, NumPad enter
            28, 156 -> {

                val newCode = line.code.substring(currentPos)
                line.code = line.code.substring(0, currentPos)
                val newLine = CodeLine()

                lines.addChild(newLine)
                lines.removeChild(newLine)
                lines.children.add(currentLineNumber + 1, newLine)

                newLine.code = newCode

                currentPos = 0
                currentLineNumber++
                updateLinePositions()

            }

            // Left
            203 -> {
                currentPos--
                if (currentPos < 0) {
                    if (currentLineNumber == 0) currentPos = 0
                    else {
                        currentLineNumber--
                        currentPos = currentLine.code.length
                    }
                }
            }

            // Right
            205 -> {
                currentPos++
                if (currentPos > currentLine.code.length) {
                    if (currentLineNumber < lines.children.size - 1) {
                        currentLineNumber++
                        currentPos = 0
                    } else currentPos--
                }
            }

            // Down
            208 -> {
                if (currentLineNumber < lines.children.size - 1) {
                    currentLineNumber++
                    currentPos = currentPos.coerceAtMost(currentLine.code.length)
                } else currentPos = currentLine.code.length
            }

            // Up
            200 -> {
                if (currentLineNumber > 0) {
                    currentLineNumber--
                    currentPos = currentPos.coerceAtMost(currentLine.code.length)
                } else currentPos = 0
            }

            // F5
            63 -> {
                clientApi.chat().printChatMessage("§eSending...")
                val buffer = Unpooled.buffer()
                buffer.writeBytes(currentCode(false).toByteArray())
                clientApi.clientConnection().sendPayload("hw:script", buffer)
                clientApi.chat().printChatMessage("§aSent!")
            }

            else -> {

                if (code == 57 && (Keyboard.isKeyDown(Keyboard.KEY_LMENU) || Keyboard.isKeyDown(Keyboard.KEY_LCONTROL))) {
                    completing = BOUNDARY.find(currentLine.code.substring(0, currentPos))!!.value
                    updateCompletions()
                } else if (input[0] != '\u0000' && code < 100) {
                    val after = if (currentPos == line.code.length) "" else line.code.substring(currentPos)
                    line.code = line.code.substring(0, currentPos) + input + after
                    currentPos += input.length
                }
            }
        }

        updateCursorPosition()
        lastKeyText.content = "$code $input\nline $currentLineNumber; pos $currentPos\ncompleting: $completing"

    }

}