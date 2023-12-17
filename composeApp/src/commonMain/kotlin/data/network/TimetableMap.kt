package data.network

import data.util.Resource
import kotlin.random.Random

data class TimetableMap(
    val timetable: MutableMap<String, Map<String, Resource<Timetable>>> = mutableMapOf(),
    private val id: Int = Random.nextInt()
) {
    fun mergeMaps(
        key: String,
        miniMap: Map<String, Resource<Timetable>>
    ): TimetableMap {
        return TimetableMap(timetable = timetable.apply {
            timetable.put(key, (timetable[key].orEmpty().keys + miniMap.keys).associateWith {
                val bidData = timetable[key]?.get(it)
                val miniData = miniMap[it]
                if (bidData != null && miniData != null) {
                    if (bidData is Resource.Success && miniData is Resource.Success) {
                        miniData.apply {
                            this.data.lessons = (this.data.lessons + bidData.data.lessons)
                                .distinctBy { it.dateTimeStart }
                                .toMutableList()
                            this.data.events = (this.data.events + bidData.data.events)
                                .distinctBy { it.dateTimeStart }
                                .toMutableList()
                        }.also { println("update data: ${it.data.lessons.size} ${it.data.events.size}") }
                    } else if (bidData is Resource.Success && miniData is Resource.Error) {
                        bidData
                    } else if (bidData is Resource.Error && miniData is Resource.Success) {
                        miniData
                    } else {
                        Resource.Loading()
                    }
                } else if (bidData != null && miniData == null) {
                    bidData
                } else if (bidData == null && miniData != null) {
                    miniData
                } else
                    Resource.Loading()
            })?.toMutableMap()
        })
    }
}