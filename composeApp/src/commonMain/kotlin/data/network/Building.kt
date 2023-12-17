package data.network

import kotlinx.serialization.Serializable

@Serializable
data class Building(
    val id: Int,
    val name: String,
    val shortName: String
)