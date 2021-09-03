package ru.cristalix.socials.data

import java.util.*

/**
 * Basic player info like name, name-with-rank, and online status
 * is used in every sphere.
 */
data class PlayerInfo(
    val uuid: UUID,

    /**
     * Simple name without any formatting
     */
    val name: String?,

    /**
     * Color code with code character
     */
    val nameColor: String?,

    val donateGroup: String?,

    val staffGroup: String?,

    /**
     * Human readable location, e. g. "Playing BedWars"
     */
    val status: String?,

    /**
     * Party id, is used to group friends / guild members by party
     * and to distinguish between players, that are in party, and players,
     * that haven't accept the invite yet.
     */
    val partyId: UUID?
)

/**
 * Player can request player info for any player uuid at any time.
 */
data class RequestPlayerInfoPacket(
    val ids: List<UUID>
)

/**
 * If API usage limits are not exceeded, the server will respond with corresponding player info.
 */
data class PlayerInfoPacket(
    val info: List<PlayerInfo>
)


/**
 * Any changes in friend list will cause the service to send the entire friend list.
 */
data class UpdateFriendsPacket(
    val ids: List<UUID>
)

/**
 * Any changes in party will cause the service to send data of the entire party.
 */
data class UpdatePartyPacket(

    /**
     * Null if player doesn't have an active party.
     */
    val party: Party?

)

data class Party(

    /**
     * Players list contains all players that have access to this party,
     * including ones who haven't yet accept the invite,
     * and including ones who went offline, but will remain in the party once reconnected.
     */
    val players: List<UUID>,

    /**
     * The list with players should also include the uuid of the leader.
     */
    val leader: UUID,

    /**
     * If a player has party-type relationships with the party leader,
     * then they will be able to join without an invite.
     */
    val type: PartyType,

)

/**
 * Default party type if PRIVATE.
 */
enum class PartyType {

    /**
     * Nobody can join the party without an invite
     */
    PRIVATE,

    /**
     * Only friends of party leader can join without an invite
     */
    FRIENDS,

    /**
     * Only party leader's guild members can join without an invite
     */
    GUILD,

    /**
     * You should be either a friend or be in the same guild
     * to join the party without an invite
     */
    FRIENDS_OR_GUILD,

    /**
     * Anyone can join the party without an invite
     */
    PUBLIC,

}
