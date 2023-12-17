package data.network

import kotlinx.serialization.Serializable
import kotlin.random.Random

@Serializable
data class PaginationPage<T>(
    var links: Links = Links(),
    var count: Int = 0,
    var results: List<T> = listOf(),
    private var id: Int = Random.nextInt()
) {
    operator fun plus(responseData: PaginationPage<T>?): PaginationPage<T> {
        return if (responseData != null && responseData.count == count)
            this.apply {
                links = responseData.links
                results = results.plus(responseData.results).distinct()
                count = responseData.count
                id = Random.nextInt()
            }
        else {
            if (responseData != null) {
                this.apply {
                    links = responseData.links
                    results = responseData.results
                    count = responseData.count
                    id = Random.nextInt()
                }
                responseData.apply {
                    id = Random.nextInt()
                }
            } else
                this.apply {
                    id = Random.nextInt()
                }
        }
    }

    override fun equals(other: Any?): Boolean {
        return if (other is PaginationPage<*>) {
            id == other.id && results == other.results && count == other.count && links == other.links
        } else
            false
    }
}