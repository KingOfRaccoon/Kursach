package data.network

data class ItemTeacherFilter (
    override val filter: String,
    override val name: String,
    override val id: Int,
    val image: String,
    val imageSite: String,
    val tid: Int
): ItemFilter(filter, name, id)