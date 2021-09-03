package ru.cristalix.mods

import dev.xdark.clientapi.ClientApi
import dev.xdark.clientapi.entry.ModMain
import dev.xdark.clientapi.event.chat.ChatReceive
import dev.xdark.clientapi.event.lifecycle.GameLoop
import dev.xdark.clientapi.text.Text
import ru.cristalix.clientapi.KotlinMod

class NiceChatMod: ModMain {

    override fun load(api: ClientApi) {

        val listener = ChatReceive.BUS.createListener()
        ChatReceive.BUS.register(listener, {
            it.isCancelled = true
            api.chat().printChatMessage(replace(it.text))
        }, 1)

    }

    override fun unload() {
    }

    fun replace(text: Text): Text {
        println("\"" + text.unformattedPart + "\" \"" + text.unformattedText + "\" \"" + text.formattedText + "\"")
//        text.parts.forEach {
//            if (it !== text)
//                println("\"" + it.unformattedPart + "\" \"" + it.unformattedText + "\" \"" + it.formattedText + "\"")
//        }
        val clean = text.unformattedPart
            .replace(" ┃ ", " ")
            .replace("┃ ", "")
            .replace(" ┃", "")
            .replace("┃", "")
        val cleanText = Text.of(clean)
        cleanText.style = text.style
        text.parts.forEach {
            if (text !== it)
                cleanText.append(replace(it))
        }
        return cleanText
    }

}