package ru.cristalix.mods.socials.notification

import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.utility.Easings
import ru.cristalix.uiengine.utility.Flex
import ru.cristalix.uiengine.utility.FlexDirection
import ru.cristalix.uiengine.utility.TOP_RIGHT

class NotificationOverlay: Flex() {
    init {
        align = TOP_RIGHT
        origin = TOP_RIGHT
        offset.y = 52.0
        flexDirection = FlexDirection.DOWN
        flexSpacing = 4.0
    }

    fun push(notification: Notification) {

        notification.origin.x = -1.0
        animate(0.3, Easings.QUAD_OUT) {
            notification.origin.x = 0.0
        }

        val timeout = notification.data.timeout / 1000.0
        notification.progressBar.animate(timeout) {
            size.x = 0.0
        }

        UIEngine.schedule(timeout) {
            animate(0.3, Easings.QUAD_BOTH) {
                notification.origin.x = -1.0
            }
            UIEngine.schedule(0.3) {
                removeChild(notification)
            }
        }

        +notification

    }
}
