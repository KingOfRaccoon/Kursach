package data.network

import kotlinx.serialization.Serializable

@Serializable
data class Room(
    val buildingId: Int,
    val name: String
)