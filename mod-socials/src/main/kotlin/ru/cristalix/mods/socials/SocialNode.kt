package ru.cristalix.mods.socials

import dev.xdark.clientapi.opengl.GlStateManager
import dev.xdark.clientapi.text.ClickEvent.Action.SUGGEST_COMMAND
import dev.xdark.clientapi.text.Style
import dev.xdark.clientapi.text.Text
import implario.humanize.TimeFormatter
import org.lwjgl.input.Keyboard
import ru.cristalix.socials.data.friendsCommand
import ru.cristalix.socials.data.getPrefix
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.RectangleElement
import ru.cristalix.uiengine.element.TextElement
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.utility.*
import java.time.Duration
import java.util.*
import dev.xdark.clientapi.text.ClickEvent as ComponentClickEvent

//inline fun socialNode(builder: SocialNode.() -> Unit) = SocialNode().also(builder)

val timeFormatter: TimeFormatter = TimeFormatter.builder()
    .accuracy(0.0)
    .excludeIntervals(
        TimeFormatter.Interval.TICK,
        TimeFormatter.Interval.MILLISECOND,
        TimeFormatter.Interval.NANOSECOND
    )
    .build()

class SocialNode(
    val id: UUID,
) : RectangleElement() {

    val millisSinceOnline: Long
        get() = if (lastSeenTime == null || lastSeenTime!! <= 0) -1 else System.currentTimeMillis() - lastSeenTime!!

    val regex = Regex("§.")
    var realm: String? = null
        set(value) {
            field = value?.replace(regex, "")
            status = status
        }

    var relation: PlayerRelationType? = null
        set(value) {
            field = value
            // nothing?? todo
        }

    var name: String? = null
        set(value) {
            field = value
            nameText.content = field!!
        }
    var status: PlayerStatus? = null
        set(value) {
            field = value
            statusText.color =
                when (value) {
                    "AFK" -> Color(0xE0, 0x76, 0x14, 1.0)
                    "ONLINE" -> Color(100, 255, 100, 1.0)
                    else -> Color(100, 100, 100, 1.0)
                }
            statusDot.color = statusText.color
            statusDot.color.alpha = if (value == "OFFLINE") 0.0 else 1.0

            val sinceOnline = millisSinceOnline

            statusText.content = when {
                value == "OFFLINE" -> when {
                    sinceOnline < 0 -> "Оффлайн"
                    sinceOnline < 60000 -> "Оффлайн меньше минуты"
                    else -> "Оффлайн ${timeFormatter.format(Duration.ofMillis(sinceOnline))}"
                }
                realm != null -> "$realm"
                else -> "Онлайн"
            }


            head.head0.color = if (value == "OFFLINE") Color(150, 150, 150, 1.0) else WHITE
            head.head1.color = if (value == "OFFLINE") Color(150, 150, 150, 1.0) else WHITE

//            val staffGroupId = getPrefix(staffGroup)
            staffGroupBox.prefix = getPrefix(staffGroup)
            staffGroupBox.dark = value == "OFFLINE"
            donateGroupBox.prefix = getPrefix(donateGroup)
            donateGroupBox.dark = value == "OFFLINE"
//            if (staffGroupId != null) {
//                staffGroupBox.textureFrom = staffGroupId.getCoords()
//                staffGroupBox.textureSize.x = staffGroupId.width.toDouble() / 128.0
//                if (value == "OFFLINE") {
//                    staffGroupBox.textureFrom.x += 64.0 / 128.0
//                    staffGroupBox.color.alpha = 0.5
//                } else {
//                    staffGroupBox.color.alpha = 1.0
//                }
//                staffGroupBox.size.x = staffGroupId.width.toDouble()
//            }
//            val donateGroupId = getPrefix(donateGroup)
//            if (donateGroupId != null) {
//                donateGroupBox.textureFrom = donateGroupId.getCoords()
//                donateGroupBox.textureSize.x = donateGroupId.width.toDouble() / 128.0
//                if (value == "OFFLINE") {
//                    donateGroupBox.textureFrom.x += 64.0 / 128.0
//                    donateGroupBox.color.alpha = 0.5
//                } else {
//                    donateGroupBox.color.alpha = 1.0
//                }
//                donateGroupBox.size.x = donateGroupId.width.toDouble()
//            }
//            println("staff $staffGroupId $staffGroup donate $donateGroupId $donateGroup")

            nameText.color.alpha = if (value == "OFFLINE") 0.5 else 1.0

        }

    var lastSeenTime: Long? = null
        set(value) {
            field = value
            status = status
        }

    var partyUid: UUID? = null
    var staffGroup: String? = null
        set(value) {
            field = value
            status = status
        }

    var donateGroup: String? = null
        set(value) {
            field = value
            status = status
        }

    val head = +playerHead(id) {
        scale = V3(2.5, 2.5, 2.5)
        offset = V3(16.0, 16.0)
        origin = CENTER
    }

    val textContainer = flex {
        flexDirection = FlexDirection.DOWN
        flexSpacing = 1.0
        align = LEFT
        origin = LEFT
        offset.x = 36.0
    }

    val donateGroupBox = rankPrefix {
//        size.y = 9.0
//        textureSize.y = 9.0 / 128.0
//        color = WHITE
//        textureLocation = ResourceLocation.of("delfikpro", "prefixes")
    }

    val staffGroupBox = rankPrefix {
//        size.y = 9.0
//        textureSize.y = 9.0 / 128.0
//        color = WHITE
//        textureLocation = ResourceLocation.of("delfikpro", "prefixes")
    }

    var nameText = text {
        content = "..."
    }

    val nameContainer = flex {
        flexDirection = FlexDirection.RIGHT
        flexSpacing = 4.0
        addChild(nameText, staffGroupBox, donateGroupBox)
        textContainer.addChild(this)
    }

    val statusText = text {
        content = ""
        align = LEFT
        offset.x = 36.0
        textContainer.addChild(this)
    }

    val statusDot = rectangle {
        size.x = 7.0
        size.y = 7.0
        offset.x = 26.0
        offset.y = 24.0
        origin = CENTER
    }

//    val action = text {
//        content = "text"
//    }

    val menu = +flex {

        flexDirection = FlexDirection.DOWN

        color = Color(0x4E, 0x4e, 0x4E, 0.82)

        origin = BOTTOM_RIGHT
        align = BOTTOM_LEFT

        enabled = false

        fun addButton(text: String, action: RectangleElement.() -> Unit): RectangleElement {
            return rectangle {
                size = V3(130.0, 20.0)
                color = Color(0x4E, 0x4E, 0x4E, 0.0)
                onHover {
                    animate(0.1, Easings.QUAD_OUT) {
                        color = if (hovered) Color(255, 255, 255, 0.31) else Color(0x4E, 0x4E, 0x4E, 0.0)
                    }
                }
                addChild(text {
                    origin = CENTER
                    align = CENTER
//                    offset.x = 4.0
                    content = text
                })
                onClick {
                    if (!down && button == MouseButton.LEFT) {
                        action()
                    }
                }
            }.also { addChild(it) }
        }

        addButton("Написать в ЛС") {
            val text = Text.of("")
            text.style = Style.create().setClickEvent(
                ComponentClickEvent.of(SUGGEST_COMMAND, "/msg $name ")
            )
            UIEngine.clientApi.minecraft().currentScreen().handleTextClick(text)
            this@flex.enabled = false
            if (socialsScreen.currentMenu == this@flex) socialsScreen.currentMenu = null
        }

        addButton("Пригласить в пати") {
            UIEngine.clientApi.chat().sendChatMessage("/party invite $name")
            this@flex.enabled = false
            if (socialsScreen.currentMenu == this@flex) socialsScreen.currentMenu = null
        }


        val copyButton = addButton("Копировать ник") {
            UIEngine.clientApi.clipboard().setContent(
                if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) id.toString() else "$name"
            )
//            children[0].color.alpha = 0.5
//            animate(0.5, Easings.CUBIC_OUT) {
//                children[0].color.alpha = 1.0
//            }
            this@flex.enabled = false
            if (socialsScreen.currentMenu == this@flex) socialsScreen.currentMenu = null
        }

        beforeRender {
            (copyButton.children[0] as TextElement).content =
                if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) "Копировать UUID" else "Копировать ник"
        }

        var confirm = false
        addButton("Удалить из друзей") {

            val text = children[0] as TextElement
            onHover {
                if (!hovered) {
                    confirm = false
                    text.content = "Удалить из друзей"
                }
            }
            if (confirm) {
                UIEngine.clientApi.chat().sendChatMessage("$friendsCommand remove $name")
                this@flex.enabled = false
                if (socialsScreen.currentMenu == this@flex) socialsScreen.currentMenu = null
            } else {
                animate(0.7, Easings.ELASTIC_OUT) {
                    color = Color(0xA0, 0x1D, 0x28, 1.0)
                }
                text.content = "Точно-точно удалить?"
                confirm = true
            }
        }

//        onHover {
//            if (!hovered) {
//                val c = context.menuOverlay.children
//                if (c.size > 0 && c[0] == this@flex) {
//
//                    context.menuOverlay.enabled = false
//                }
//            }
//        }
    }

    init {

        addChild(textContainer, statusDot)
        size = V3(200.0, 30.0)
        color.alpha = 0.0
        onHover {

            animate(0.15) {
                color = if (this@onHover.hovered) Color(0x2A, 0x66, 0xBD, 0.28) else Color(0, 0, 0, 0.0)
            }
        }

        beforeRender {
            GlStateManager.enableDepth()
            GlStateManager.depthMask(true)
        }

        afterRender {
            GlStateManager.disableDepth()
        }

        onClick {
            if (!down) {
                socialsScreen.currentMenu?.let { it.enabled = false }
                if (socialsScreen.currentMenu == menu) {
                    socialsScreen.currentMenu = null
                } else {
                    menu.enabled = true
                    socialsScreen.currentMenu = menu
                }
            }
        }
    }


}