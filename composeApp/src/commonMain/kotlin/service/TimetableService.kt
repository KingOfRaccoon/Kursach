package service

import androidx.compose.foundation.pager.PageSize
import data.network.*
import data.util.Postman
import data.util.Resource
import io.ktor.http.*

class TimetableService(private val postman: Postman) {
    private val baseUrl = "https://hot-up-skylark.ngrok-free.app/"
    private val dayRoute = "dayTimetable/"
    private val weekRoute = "weekTimetable/"
    private val createEventRoute = "createEvent/"
    private val createTypeRoute = "createType/"
    private val groupsRoute = "groups/"
    private val teachersRoute = "teachers/"
    private val roomsRoute = "rooms/"
    private val typesLessonsRoute = "typesLesson/"
    private val typesEventsRoute = "types/"
    private val buildingsRoute = "buildings/"

    suspend fun getTimetableDay(
        day: String,
        groupId: Int? = null,
        teacherId: Int? = null,
        roomId: Int? = null,
        ownerId: Int? = null
    ): Resource<Timetable> {
        return postman.submitForm(
            baseUrl,
            dayRoute + day,
            parameters {
                mapOf("groupId" to groupId, "teacherId" to teacherId, "roomId" to roomId, "ownerId" to ownerId).filter {
                    it.value != null
                }.forEach {
                    append(it.key, it.value.toString())
                }
            }
        )
    }

    suspend fun getTimetableWeek(
        year: String,
        weekNumber: String,
        groupId: Int? = null,
        teacherId: Int? = null,
        roomId: Int? = null,
        ownerId: Int? = null
    ): Resource<Timetable> {
        return postman.submitForm(
            baseUrl,
            "$weekRoute$year/$weekNumber",
            parameters {
                mapOf("groupId" to groupId, "teacherId" to teacherId, "roomId" to roomId, "ownerId" to ownerId).filter {
                    it.value != null
                }.forEach {
                    append(it.key, it.value.toString())
                }
            }
        )
    }

    suspend fun createEvent(event: Event): Resource<ResponseSusses> {
        return postman.submitForm(
            baseUrl,
            createEventRoute,
            parameters {
                event.getMap().forEach {
                    append(it.key, it.value)
                }
            }
        )
    }

    suspend fun createType(type: Type): Resource<ResponseSusses> {
        return postman.submitForm(
            baseUrl,
            createTypeRoute,
            parameters {
                type.getMap().forEach {
                    append(it.key, it.value)
                }
            }
        )
    }

    suspend fun getGroupsList(page: Int, pageSize: Int = 50): Resource<PaginationPage<Group>> {
        return postman.submitForm(
            baseUrl,
            groupsRoute,
            parameters {
                append("page", page.toString())
                append("pageSize", pageSize.toString())
            }
        )
    }

    suspend fun getTeachersList(page: Int, pageSize: Int = 50): Resource<PaginationPage<Teacher>> {
        return postman.submitForm(
            baseUrl,
            teachersRoute,
            parameters {
                append("page", page.toString())
                append("pageSize", pageSize.toString())
            }
        )
    }

    suspend fun getRoomsList(page: Int, pageSize: Int = 50): Resource<PaginationPage<Room>> {
        return postman.submitForm(
            baseUrl,
            roomsRoute,
            parameters {
                append("page", page.toString())
                append("pageSize", pageSize.toString())
            }
        )
    }

    suspend fun getTypesLessonList(page: Int, pageSize: Int = 50): Resource<PaginationPage<TypeLesson>> {
        return postman.submitForm(
            baseUrl,
            typesLessonsRoute,
            parameters {
                append("page", page.toString())
                append("pageSize", pageSize.toString())
            }
        )
    }

    suspend fun getTypesEventsList(ownerId: Int, page: Int, pageSize: Int = 50): Resource<PaginationPage<TypeLesson>> {
        return postman.submitForm(
            baseUrl,
            typesEventsRoute,
            parameters {
                append("ownerId", ownerId.toString())
                append("page", page.toString())
                append("pageSize", pageSize.toString())
            }
        )
    }

    suspend fun getBuildingsList(page: Int, pageSize: Int = 50): Resource<PaginationPage<Building>> {
        return postman.submitForm(
            baseUrl,
            buildingsRoute,
            parameters {
                append("page", page.toString())
                append("pageSize", pageSize.toString())
            }
        )
    }
}