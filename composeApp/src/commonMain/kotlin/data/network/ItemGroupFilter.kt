package data.network

data class ItemGroupFilter (
    override val name: String,
    override val id: Int,
    val searchId: Int
): ItemFilter(name, name, id)