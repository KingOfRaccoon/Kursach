package data.network

import kotlinx.serialization.Serializable

@Serializable
data class Links(val previous: Boolean = false, val next: Boolean = false)