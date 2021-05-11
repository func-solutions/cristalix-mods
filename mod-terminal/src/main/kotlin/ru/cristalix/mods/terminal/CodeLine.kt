package ru.cristalix.mods.terminal

import ru.cristalix.uiengine.element.RectangleElement
import ru.cristalix.uiengine.utility.*

val KEYWORD_PATTERN = Regex("\\b(as|assert|break|case|catch|class|const|continue|def|" +
        "default|do|else|enum|extends|false|finally|for|goto|if|implements|import|in|instanceof|" +
        "interface|new|null|package|return|super|switch|this|throw|throws|trait|true|try|var|while)\\b")

val STRING_PATTERN = Regex("\"(\\\\\"|[^\"]+)*\"")

val BOUNDARY = Regex("\\b[A-Za-z0-9_$]+$")

class CodeLine: RectangleElement() {

    val lineNumber = text {
        color.alpha = 0.5
        align = RIGHT
        origin = RIGHT
        offset.x = -1.0
    }

    val contents = text {
        align = TOP_RIGHT
        offset.x = 1.0
    }

    val lineNumberBox = rectangle {
        offset.x = -1.0
        size.x = 10.0
        size.y = 10.0
        addChild(lineNumber, contents)
        color = Color(30, 30, 30, 1.0)
    }

    init {
        addChild(lineNumberBox)
    }

    var code: String = ""
    set(value) {
        field = value
        relight()
    }

    fun relight() {

        val ss = code.split("//", limit = 2)
        if (ss.isEmpty()) {
            contents.content = ""
            return
        }

        var visual = STRING_PATTERN.replace(KEYWORD_PATTERN.replace(ss[0]) {
            "§6${it.value}§f"
        }) {"§a${it.value}§f"}
        if (ss.size > 1) visual += "§2§o//" + ss[1]

        contents.content = visual

    }

}