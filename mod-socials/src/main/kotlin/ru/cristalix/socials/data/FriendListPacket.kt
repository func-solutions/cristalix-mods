package ru.cristalix.mods.socials

import java.util.*


class FriendListPacket {



}


data class SocialInfo(

    val uuid: UUID,
    val partyUid: UUID?,
    val name: String?,
    val donateGroup: String?,
    val staffGroup: String?,
    val status: PlayerStatus?,
    val relation: PlayerRelationType?,
    val realm: String?,
    val lastSeenOnline: Long?
)

{
    override fun toString(): String {
        return "SocialInfo(uuid=$uuid, partyUid=$partyUid, name=$name, donateGroup=$donateGroup, staffGroup=$staffGroup, status=$status, relation=$relation, realm=$realm, lastSeenOnline=$lastSeenOnline)"
    }
}

typealias PlayerStatus = String
typealias PlayerRelationType = String

//enum class PlayerStatus {
//    OFFLINE, ONLINE, AFK
//}

enum class PlayerRelationType2 {
    /**
     * No relation link. Will be returned instead of a null and prefferably pushed into database instead of deleting the record.
     */
    NONE,

    /**
     * One player is subscribed to another, replacement for friendlist invite. If both players are subscribed to each other it means they're friends.
     */
    SUBSCRIBER,

    /**
     * Both players are subscribed to each other.
     */
    FRIEND
}