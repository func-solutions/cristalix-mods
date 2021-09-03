package ru.cristalix.socials.data

import ru.cristalix.uiengine.utility.V2
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException

const val friendsCommand = "/friend"

//fun getPrefix(groupId: String): String {
//
//    return when (groupId) {
//        "NO", "PLAYER" -> ""
//        "YOUTUBE" -> "§cY§fT"
//        "TESTER" -> "Tester"
//        "BUILDER" -> "§3B"
//        "SR_BUILDER" -> "§3Sr.B"
//        "CUR_BUILDER" -> "§3Cur.B"
//        "HELPER" -> "§9H"
//        "MODERATOR" -> "§dM"
//        "GRAND_MODERATOR" -> "§5Sr.M"
//        "CURATOR" -> "§cCur"
//        "ADMIN" -> "§cADM"
//        "DEVELOPER" -> "§9Dev"
//        "OWNER" -> "§cOWN"
//        "LOCAL_STAFF" -> "§4LS"
//        else -> ""
//    }
//
//}

enum class Prefix(
    val width: Int
) {

    ADMIN(28),
    BUILDER(31),
    CURATOR(28),
    CUR_BUILDER(31),
    DIAMOND(16),
    DEVELOPER(28),
    EMERALD(16),
    GOLD(16),
    GOD(28),
    HE_TOPT(28),
    HELPER(28),
    YOUTUBE(16),
    VIP_PLUS(16),
    VIP(16),
    IRON(16),
    MODERATOR(28),
    MVP_PLUS(28),
    MVP(28),
    OWNER(28),
    PREMIUM_PLUS(16),
    PREMIUM(16),
    SPONSOR(16),
    GRAND_MODERATOR(28);

    fun getCoords() = V2(ordinal / 14 * 32.0 / 128.0, ordinal % 14 * 9.0 / 128.0)

}

fun getPrefix(groupId: String?): Prefix? {
    if (groupId == null) return null
    try {
//        println(groupId)
        return Prefix.valueOf(groupId.toUpperCase())
    } catch (ex: IllegalArgumentException) {
        return null
    }
}



