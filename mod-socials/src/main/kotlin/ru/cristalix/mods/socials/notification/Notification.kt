package ru.cristalix.mods.socials.notification

import ru.cristalix.socials.data.NotificationData
import ru.cristalix.socials.data.intToColor
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.Parent
import ru.cristalix.uiengine.element.RectangleElement
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.utility.*
import java.util.*
import kotlin.collections.ArrayList

const val notificationWidth = 210.0

class Notification(
    val data: NotificationData,
) : Flex() {

    val progressBar = +rectangle {
        color = intToColor(data.timeoutBarColor)
        size.x = 210.0
        size.y = 2.0
    }

    init {
        size.x = notificationWidth

        +rectangle { size.y = 5.0 }

        val text = data.text
        if (text != null) {
            val lines = ArrayList(text.split("\n", limit = 2))

            if (lines.isNotEmpty()) +text {
                offset.x = 5.0
                align.x = 5.0 / notificationWidth
                content = lines.removeAt(0)
            }
            if (lines.isNotEmpty()) {
                +rectangle { size.y = 4.0 }
                +text {
                    offset.x = 5.0
                    align.x = 5.0 / notificationWidth
                    color.alpha = 0.62
                    content = lines[0]
                }
            }

        }

        +rectangle { size.y = 7.0 }

        +flex {
            flexSpacing = 6.0
            flexDirection = FlexDirection.RIGHT
            align.x = 5.0 / notificationWidth

            data.buttons?.forEach { button ->

                +rectangle {
                    color = intToColor(button.color)
                    val caption = +text {
                        content = button.text ?: ""
                        origin = CENTER
                        align = CENTER
                    }
                    size.x = caption.size.x + 18.0
                    size.y = 14.0
                    onClick {
                        if (down) {
                            if (button.command != null) {
                                UIEngine.clientApi.chat().sendChatMessage(button.command)
                            }
                            this@Notification.lastParent?.let {
                                animate(0.3, Easings.QUAD_BOTH) {
                                    this@Notification.origin.x = -1.0
                                }
                                UIEngine.schedule(0.3) {
                                    (it as Parent).removeChild(this@Notification)
                                }
                            }
                        }
                    }
                }

            }


        }

        +rectangle {
            size.y = 5.0
            size.x = notificationWidth
        }

        color = intToColor(data.backgroundColor)
        color.alpha = 1.0
        flexDirection = FlexDirection.DOWN
    }

}