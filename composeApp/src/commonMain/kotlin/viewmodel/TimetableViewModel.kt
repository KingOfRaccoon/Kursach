package viewmodel

import data.network.*
import data.time.DataTime
import data.util.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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

    private val _timetableFlow = MutableStateFlow(TimetableMap(mutableMapOf()))
    val timetableFlow = _timetableFlow.asStateFlow()

    private val _currentSelectedItemFlow = MutableStateFlow(-1)
    val currentSelectedItem = _currentSelectedItemFlow.asStateFlow()

    val timetableScope = CoroutineScope(Dispatchers.Default)

    init {
        _datesFlow.update { generateTwoWeeks() }
        loadBuildings()
        loadTypesLessons()
        loadRooms()
        loadGroups()
        loadTeachers()
    }

    fun setCurrentSelectedItem(index: Int) {
        _currentSelectedItemFlow.value = index
    }

    private fun generateTwoWeeks(): List<DataTime> {
        val timeZone = TimeZone.currentSystemDefault()
        val instant = Clock.System.now()
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
}