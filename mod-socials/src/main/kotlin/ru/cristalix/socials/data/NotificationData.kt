package ru.cristalix.socials.data

import ru.cristalix.uiengine.utility.Color
import java.util.*

fun intToColor(i: Int, alpha: Double = 1.0): Color =
    Color(i shr 16 and 255, i shr 8 and 255, i and 255, alpha)

data class NotificationData(
    val source: UUID?,
    val type: String?,
    val text: String?,
    val timeoutBarColor: Int,
    val backgroundColor: Int,
    val timeout: Long,
    val buttons: List<NotificationButton>?,
)

data class NotificationButton(
    val text: String? = null,
    val color: Int,
    val command: String? = null,
    val removeButton: Boolean,
    val removeNotification: Boolean,
)
