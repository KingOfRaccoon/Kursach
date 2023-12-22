package viewmodel

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import data.local.EmptyTime
import data.local.UserData
import data.network.*
import data.time.DataTime
import data.util.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import service.TimetableService
import kotlin.random.Random

class TimetableViewModel(private val timetableService: TimetableService) {
    private val _datesFlow = MutableStateFlow<List<DataTime>>(listOf())
    val datesFlow = _datesFlow.asStateFlow()

    private val _buildingsStateFlow =
        MutableStateFlow<Resource<PaginationPage<Building>>>(Resource.Loading())
    val buildingsStateFlow = _buildingsStateFlow.asStateFlow()

    private val _typesLessonsStateFlow =
        MutableStateFlow<Resource<PaginationPage<TypeLesson>>>(Resource.Loading())
    val typesLessonsStateFlow = _typesLessonsStateFlow.asStateFlow()

    private val _typesEventsStateFlow =
        MutableStateFlow<Resource<PaginationPage<TypeLesson>>>(Resource.Loading())
    val typesEventsStateFlow = _typesEventsStateFlow.asStateFlow()

    private val _roomsStateFlow =
        MutableStateFlow<Resource<PaginationPage<Room>>>(Resource.Loading())
    val roomsStateFlow = _roomsStateFlow.asStateFlow()

    private val _groupsStateFlow =
        MutableStateFlow<Resource<PaginationPage<Group>>>(Resource.Loading())
    val groupsStateFlow = _groupsStateFlow.asStateFlow()

    private val _teachersStateFlow =
        MutableStateFlow<Resource<PaginationPage<Teacher>>>(Resource.Loading())
    val teachersStateFlow = _teachersStateFlow.asStateFlow()

    private val _teachersImagesStateFlow = MutableStateFlow(ImagesTeachers())
    val teachersImagesStateFlow = _teachersImagesStateFlow.asStateFlow()

    private val _timetableFlow = MutableStateFlow(TimetableMap(mutableMapOf()))
    val timetableFlow = _timetableFlow.asStateFlow()

    private val _currentSelectedItemFlow = MutableStateFlow(-1)
    val currentSelectedItem = _currentSelectedItemFlow.asStateFlow()

    private val _currentSelectedItemSearchFlow = MutableStateFlow(-1)
    val currentSelectedSearchItem = _currentSelectedItemSearchFlow.asStateFlow()

    val timetableScope = CoroutineScope(Dispatchers.Default)

    val nameNewEvent = mutableStateOf("")
    val placeNewEvent = mutableStateOf("")
    val nameNewType = mutableStateOf("")
    val shortNameNewType = mutableStateOf("")

    fun combineUserTimetable(userState: StateFlow<Resource<User>>) = combine(
        userState,
        groupsStateFlow,
        teachersStateFlow
    ) { user, groups, teachers ->
        if (user is Resource.Success) {
            val (tag, id) = user.data.filter.split(" ")
                .let { it.first() to (it.last().toIntOrNull() ?: -1) }
            if (tag == "groupId:")
                Resource.Success(
                    user.data.copy(
                        groupId = id,
                        groupName = groups.data?.results?.find { group -> group.id == id }?.groupName.orEmpty()
                    )
                )
            else
                Resource.Success(
                    user.data.copy(
                        teacherId = id,
                        teacherName = teachers.data?.results?.find { group -> group.id == id }?.fullName().orEmpty()
                    )
                )

        } else
            return@combine user
    }

    fun setNameNewEvent(newName: String) {
        nameNewEvent.value = newName
    }

    fun setPlaceNewEvent(place: String) {
        placeNewEvent.value = place
    }

    fun setNameNewType(newName: String) {
        nameNewType.value = newName
    }

    fun setShortNameType(newName: String) {
        if (newName.length < 3)
            shortNameNewType.value = newName
    }

    init {
        _datesFlow.update { generateTwoWeeks() }
        loadBuildings()
        loadTypesLessons()
        loadRooms()
        loadGroups()
        loadTeachers()
    }

    fun createEvent(
        dateTimeStart: String,
        dateTimeEnd: String,
        typesIds: List<Int>,
        date: String,
        ownerId: Int,
        groupId: Int? = null,
        teacherId: Int? = null
    ) {
        timetableScope.launch {
            timetableService.createEvent(
                Event(
                    nameNewEvent.value,
                    dateTimeStart,
                    dateTimeEnd,
                    placeNewEvent.value,
                    typesIds
                )
            ).also {
                setNameNewEvent("")
                setPlaceNewEvent("")

                if (it is Resource.Success) {
                    loadDayTimetable(
                        date, groupId, teacherId, ownerId = ownerId, isNeedUpdate = true
                    )
                }
            }
        }
    }

    fun createType(ownerId: Int) {
        timetableScope.launch {
            timetableService.createType(Type(ownerId, nameNewType.value, shortNameNewType.value, "", "")).also {
                setNameNewType("")
                setShortNameType("")

                if (it is Resource.Success) {
                    loadTypesEvents(ownerId)
                }
            }
        }
    }

    fun getTeachers() =
        combine(teachersStateFlow, teachersImagesStateFlow) { teachers, teachersImages ->
            when (teachers) {
                is Resource.Error -> Resource.Error(teachers.message)
                is Resource.Loading -> Resource.Loading(teachers.data?.results?.map {
                    ItemTeacherFilter(
                        (it.lastname + " " + it.firstname + " " + it.secondName).trim(),
                        it.fullName(),
                        it.id,
                        teachersImages.imagesTeachers[it.tid]?.thumb_file ?: "",
                        teachersImages.imagesTeachers[it.tid]?.site_avatar_url ?: "",
                        it.tid
                    )
                }?.sortedBy { it.name })

                is Resource.Success -> Resource.Success(teachers.data.results.map {
                    ItemTeacherFilter(
                        (it.lastname + " " + it.firstname + " " + it.secondName).trim(),
                        it.fullName(),
                        it.id,
                        teachersImages.imagesTeachers[it.tid]?.thumb_file ?: "",
                        teachersImages.imagesTeachers[it.tid]?.site_avatar_url ?: "",
                        it.tid
                    )
                }.sortedBy { it.name })
            }
        }

    fun getFiltersTimetable() = combine(getTeachers(), groupsStateFlow) { teachers, groups ->
        teachers to when (groups) {
            is Resource.Error -> Resource.Error(groups.message)
            is Resource.Loading -> Resource.Loading(groups.data?.results?.map {
                ItemGroupFilter(
                    it.groupName,
                    it.id,
                    it.groupId
                )
            })

            is Resource.Success -> Resource.Success(groups.data.results.map {
                ItemGroupFilter(
                    it.groupName,
                    it.id,
                    it.groupId
                )
            })
        }
    }

    fun getTimetable(
        groupId: Int? = null,
        teacherId: Int? = null,
        roomId: Int? = null,
        isFalseIdTeacher: Boolean = false,
        isFalseIdGroup: Boolean = false
    ): Flow<Map<String, Resource<Timetable>>> {
        return combine(
            timetableFlow,
            teachersStateFlow,
            groupsStateFlow,
            typesLessonsStateFlow,
            typesEventsStateFlow,
            buildingsStateFlow
        ) { timetable, teachers, groups, typesLessons, typesEvents, buildings ->
            val searchName =
                generateTag(
                    if (groupId != null) (if (isFalseIdGroup) groups.data?.results?.find { it.groupId == groupId }?.id else groupId) else groupId,
                    if (teacherId != null) (if (isFalseIdTeacher) teachers.data?.results?.find { it.tid == teacherId }?.id else teacherId) else teacherId,
                    roomId
                )
            timetable.timetable[searchName].orEmpty().map {
                it.key to it.value.let {
                    when (it) {
                        is Resource.Error -> it
                        is Resource.Loading -> it
                        is Resource.Success -> Resource.Success(Timetable(it.data.events.map {
                            it.copy(
                                types = it.typeIds.mapNotNull { typesEvents.data?.results?.find { type -> type.id == it } }
                            )
                        }, it.data.lessons.map {
                            it.copy(
                                groups = it.groupsIds.mapNotNull { group -> groups.data?.results?.find { it.id == group } },
                                teachers = it.teachersIds.mapNotNull { teacher -> teachers.data?.results?.find { it.id == teacher } },
                                type = typesLessons.data?.results?.find { type -> type.id == it.typeId }
                                    ?: TypeLesson(),
                                rooms = it.rooms.map { room -> room.copy(buildingName = buildings.data?.results?.find { it.id == room.buildingId }?.name.orEmpty()) }
                            )
                        }.toMutableList()))
                    }
                }
            }.toMap()
        }
    }


    fun loadWeekTimetable(
        year: String,
        week: String,
        groupId: Int? = null,
        teacherId: Int? = null,
        roomId: Int? = null,
        ownerId: Int? = null
    ) {
        val searchName = generateTag(groupId, teacherId, roomId)
        timetableScope.launch {
            val data = convertWeekToDays(
                timetableService.getTimetableWeek(year, week, groupId, teacherId, roomId, ownerId)
            )

            _timetableFlow.update {
                _timetableFlow.value.mergeMaps(
                    searchName, data
                )
            }
        }
    }

    fun loadDayTimetable(
        day: String,
        groupId: Int? = null,
        teacherId: Int? = null,
        roomId: Int? = null,
        ownerId: Int? = null,
        isNeedUpdate: Boolean = false
    ) {
        timetableScope.launch {
            val searchName = generateTag(groupId, teacherId, roomId)
            if (isNeedUpdate || (timetableFlow.value.timetable[searchName]?.containsKey(day) != true
                        || timetableFlow.value.timetable[searchName]?.get(day) !is Resource.Success)
            ) {
                val data = timetableService.getTimetableDay(day, groupId, teacherId, roomId, ownerId)

                _timetableFlow.update {
                    _timetableFlow.value.mergeMaps(
                        searchName, mapOf(
                            day to data
                        )
                    )
                }
            }
        }
    }

    fun generateTag(
        groupId: Int? = null,
        teacherId: Int? = null,
        roomId: Int? = null
    ): String {
        var tag = ""
        if (groupId != null)
            tag += "groupId: $groupId"

        if (teacherId != null)
            tag += "teacherId: $teacherId"

        if (roomId != null)
            tag += "roomId: $roomId"

        return tag
    }

    fun setCurrentSelectedItem(index: Int) {
        _currentSelectedItemFlow.value = index
    }

    fun setCurrentSelectedSearchItem(index: Int) {
        _currentSelectedItemSearchFlow.value = index
    }

    private fun generateTwoWeeks(): List<DataTime> {
        val timeZone = TimeZone.currentSystemDefault()
        val instant = Clock.System.now().minus(28, DateTimeUnit.DAY, timeZone)
        return List(14) {
            DataTime(
                instant.plus(it, DateTimeUnit.DAY, timeZone).toLocalDateTime(timeZone)
            )
        }
    }

    private fun loadTypesLessons() {
        timetableScope.launch {
            val list = Resource.Loading(PaginationPage<TypeLesson>())
            var page = 1
            var http: Resource<PaginationPage<TypeLesson>>
            do {
                http = timetableService.getTypesLessonList(page)
                page++

                obtainHttpPagination(_typesLessonsStateFlow, list, http)
            } while (http.data?.links?.next == true)
        }
    }

    fun loadTypesEvents(ownerId: Int) {
        timetableScope.launch {
            val list = Resource.Loading(PaginationPage<TypeLesson>())
            var page = 1
            var http: Resource<PaginationPage<TypeLesson>>
            do {
                http = timetableService.getTypesEventsList(ownerId, page)
                page++

                obtainHttpPagination(_typesEventsStateFlow, list, http)
            } while (http.data?.links?.next == true)
        }
    }

    private fun loadBuildings() {
        timetableScope.launch {
            val list = Resource.Loading(PaginationPage<Building>())
            var page = 1
            var http: Resource<PaginationPage<Building>>
            do {
                http = timetableService.getBuildingsList(page)
                page++

                obtainHttpPagination(_buildingsStateFlow, list, http)
            } while (http.data?.links?.next == true)
        }
    }

    private fun loadRooms() {
        timetableScope.launch {
            val list = Resource.Loading(PaginationPage<Room>())
            var page = 1
            var http: Resource<PaginationPage<Room>>
            do {
                http = timetableService.getRoomsList(page)
                page++

                obtainHttpPagination(_roomsStateFlow, list, http)
            } while (http.data?.links?.next == true)
        }
    }

    private fun loadTeachers() {
        timetableScope.launch {
            val list = Resource.Loading(PaginationPage<Teacher>())
            var page = 1
            var http: Resource<PaginationPage<Teacher>>
            do {
                http = timetableService.getTeachersList(page)
                page++

                obtainHttpPagination(_teachersStateFlow, list, http)
            } while (http.data?.links?.next == true)
        }
    }

    private fun loadGroups() {
        timetableScope.launch {
            val list = Resource.Loading(PaginationPage<Group>())
            var page = 1
            var http: Resource<PaginationPage<Group>>
            do {
                http = timetableService.getGroupsList(page)
                page++

                obtainHttpPagination(_groupsStateFlow, list, http)
            } while (http.data?.links?.next == true)
        }
    }

    private fun <T> obtainHttpPagination(
        flow: MutableStateFlow<Resource<PaginationPage<T>>>,
        currentData: Resource.Loading<PaginationPage<T>>,
        newData: Resource<PaginationPage<T>>
    ) {
        if (newData is Resource.Success)
            currentData.data = currentData.data?.plus((newData.data))
        flow.update {
            if (currentData.data?.links?.next == true)
                currentData.copy(currentData.data?.copy(id = Random.nextInt()))
            else
                Resource.Success(currentData.data?.copy(id = Random.nextInt()) ?: PaginationPage())
        }
    }

    private fun convertWeekToDays(
        week: Resource<Timetable>,
        dataTime: DataTime = DataTime.getStartThisWeek()
    ): MutableMap<String, Resource<Timetable>> {
        val mapDays = mutableMapOf<String, Resource<Timetable>>()
        when (week) {
            is Resource.Error -> mapDays.putAll(
                List(7) { dataTime.goToNNextDay(it).getIsoFormat() to week }
            )

            is Resource.Loading -> mapDays.putAll(
                List(7) { dataTime.goToNNextDay(it).getIsoFormat() to week }
            )

            is Resource.Success -> mapDays.putAll(
                week.data.events.groupBy { it.dateTimeStart.split("T").first() }
                    .mapValues { Resource.Success(Timetable(it.value)) }.toList()
                    .toMutableList()
                    .sortedBy { it.first } + week.data.lessons.groupBy { it.dateTimeStart.split("T").first() }
                    .mapValues { Resource.Success(Timetable(lessons = it.value)) }.toList()
                    .toMutableList()
                    .sortedBy { it.first }
            )
        }

        return addDaysWithoutLessons(mapDays)
    }

    private fun addDaysWithoutLessons(dayLessons: MutableMap<String, Resource<Timetable>>): MutableMap<String, Resource<Timetable>> {
        val lessons = dayLessons.toList().toMutableList()
        var sizeNeed = 7 - lessons.size
        var i = 0
        if (lessons.isNotEmpty()) {
            if (DataTime.parseFromTimeTable(lessons.minByOrNull { it.first }!!.first).dayOfWeek != 1) {
                var startWeek =
                    DataTime.parseFromTimeTable(lessons.minByOrNull { it.first }!!.first)
                startWeek = startWeek.goToNNextDay(-(startWeek.dayOfWeek - 1))
                repeat(DataTime.parseFromTimeTable(lessons.minByOrNull { it.first }!!.first).dayOfWeek - 1) {
                    lessons.add(
                        it, startWeek.goToNNextDay(it)
                            .getIsoFormat() to Resource.Success(Timetable())
                    )
                    sizeNeed--
                }
            }
        } else {
            val startWeek = DataTime.getStartThisWeek()
            return List(7) { startWeek.goToNNextDay(it) }.associate {
                it.getIsoFormat() to Resource.Success(Timetable())
            }.toMutableMap()
        }

        while (sizeNeed > 0 && i < lessons.lastIndex) {
            if (DataTime.parseFromTimeTable(lessons[i].first).tomorrow()
                    .getIsoFormat() != lessons[i + 1].first
            ) {
                lessons.add(
                    i + 1, DataTime.parseFromTimeTable(lessons[i].first).tomorrow()
                        .getIsoFormat() to Resource.Success(Timetable())
                )
                sizeNeed--
            }

            i++
        }
        if (lessons.isNotEmpty()) {
            for (k in 0 until sizeNeed)
                lessons.add(
                    DataTime.parseFromTimeTable(lessons.last().first).tomorrow()
                        .getIsoFormat() to Resource.Success(Timetable())
                )
        }
        return lessons.toMap().toMutableMap()
    }

    fun createItems(events: List<TimetableItem>): MutableList<TimetableItem> { // here we need to get the data of time of subject into mutable list
        val items = mutableListOf<TimetableItem>()
        val eventsSorted = events.sortedBy { it._dateTimeStart }.toMutableList()
        val times = eventsSorted.map {
            it.getDataTimeStart().getTime() //time of beginning of lesson in format h:mm or hh:mm
        }.toMutableList()
        val timesEnd = eventsSorted.map {
            it.getDataTimeEnd().getTime()
        }.toMutableList()
        for (i in startTime..endTime) {// from 8 to 20
            val currTime = getHour(i)
            if (times.isNotEmpty()) {
                val times0 = if (times[0].length < 5) "0${times[0]}" else times[0]
                // if times mutable list isn't contains time from 8 to 20 we add an empty event
                if (currTime < times0) {
                    items.add(EmptyTime(i)) // create instance of EmptyTime with startTime
                } else if (getHour(i + 1) > timesEnd[0]) {
                    times.removeFirst()
                    timesEnd.removeFirst()
                    items.add(eventsSorted.removeFirst())
                }
            } else
                items.add(EmptyTime(i))
        }

        // calculate coefficient equals difference between start time current item and end time previous item
        for (i in 0 until items.size - 1) {
            items[i].let { it as? EmptyTime }?.apply {
                paddingCoefficient =
                    if (items[i + 1] is EmptyTime)
                        ((items[i + 1] as EmptyTime).startTime - startTime).toDouble()
                    else
                        (items[i + 1].getDataTimeStart().getTime().split(":").let {
                            it[0].toInt() + it[1].toInt() / 60.0
                        } - startTime)
            }
        }

        return items
    }

    private fun getHour(i: Int) = if (i > 9) "$i:00" else "0$i:00"

    inline fun <T1, T2, T3, T4, T5, T6, R> combine(
        flow: Flow<T1>,
        flow2: Flow<T2>,
        flow3: Flow<T3>,
        flow4: Flow<T4>,
        flow5: Flow<T5>,
        flow6: Flow<T6>,
        crossinline transform: suspend (T1, T2, T3, T4, T5, T6) -> R,
    ): Flow<R> = combine(flow, flow2, flow3, flow4, flow5, flow6) { args: Array<*> ->
        @Suppress("UNCHECKED_CAST")
        transform(
            args[0] as T1,
            args[1] as T2,
            args[2] as T3,
            args[3] as T4,
            args[4] as T5,
            args[5] as T6,
        )
    }

    companion object {
        private const val startTime = 8
        private const val endTime = 22
    }
}