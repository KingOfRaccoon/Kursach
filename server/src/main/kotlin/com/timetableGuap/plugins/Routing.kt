package com.timetableGuap.plugins

import com.timetableGuap.database.data.*
import com.timetableGuap.network.response.ResponseError
import com.timetableGuap.network.TimetableService
import com.timetableGuap.network.TimetableViewModel
import com.timetableGuap.network.response.EventLessonResponse
import com.timetableGuap.network.response.ResponseSusses
import com.timetableGuap.time.DataTime
import com.timetableGuap.util.Postman
import com.timetableGuap.util.Resource
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.flow.collectLatest
import org.apache.commons.validator.GenericValidator
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val viewModel: TimetableViewModel by inject()
    routing {
        get("/") {
            call.respondText(TimetableService(Postman()).getVersion().toString())
        }

        get("/weekTimetable/{${Tag.Year.tag}}/{${Tag.Week.tag}}") {
            if (call.parameters[Tag.Week.tag]?.toIntOrNull().let { it == null || it > 52 || it < 1 })
                call.respond(HttpStatusCode.UnprocessableEntity, ResponseError("week number is not correct"))

            if (call.parameters[Tag.Year.tag]?.toIntOrNull() == null)
                call.respond(HttpStatusCode.UnprocessableEntity, ResponseError("year number is not correct"))

            if (checkParametersQuery(call.parameters))
                call.respond(
                    HttpStatusCode.UnprocessableEntity,
                    ResponseError(
                        "At least 1 query (${Tag.GroupId.tag} or ${Tag.TeacherId.tag} " +
                                "or ${Tag.RoomId.tag}) parameter required"
                    )
                )

            viewModel.loadWeekTimetable(
                call.parameters[Tag.Year.tag].orEmpty(),
                call.parameters[Tag.Week.tag].orEmpty(),
                call.parameters[Tag.GroupId.tag]?.toInt(),
                call.parameters[Tag.TeacherId.tag]?.toInt(),
                call.parameters[Tag.RoomId.tag]?.toInt()
            )

            val startOfThisWeek = DataTime.getStartDayOfWeek(call.parameters[Tag.Week.tag].orEmpty().toInt())

            viewModel.timetableFlow.collectLatest {
                it.timetable[viewModel.generateTag(
                    call.parameters[Tag.GroupId.tag]?.toInt(),
                    call.parameters[Tag.TeacherId.tag]?.toInt(),
                    call.parameters[Tag.RoomId.tag]?.toInt()
                )]?.filterKeys {
                    it >= startOfThisWeek.getIsoFormat() && it < startOfThisWeek.goToNNextDay(7).getIsoFormat()
                }.let {
                    if (it?.all { it.value is Resource.Success } == true && it.size == 7)
                        call.respond(
                            HttpStatusCode.OK,
                            EventLessonResponse(
                                viewModel.getLessons(
                                    viewModel.generateTag(
                                        call.parameters[Tag.GroupId.tag]?.toInt(),
                                        call.parameters[Tag.TeacherId.tag]?.toInt(),
                                        call.parameters[Tag.RoomId.tag]?.toInt()
                                    ), startOfThisWeek, startOfThisWeek.goToNNextDay(7)
                                ), viewModel.getEvents(
                                    call.parameters[Tag.OwnerId.tag]?.toIntOrNull() ?: -1,
                                    startOfThisWeek, startOfThisWeek.goToNNextDay(7)
                                )
                            ),
                        )
                }
            }
        }

        get("/dayTimetable/{${Tag.Date.tag}}") {
//            if (call.parameters["date"] == null)
//                call.respond(HttpStatusCode.UnprocessableEntity, RequestError("date is not found"))

            if (!GenericValidator.isDate(call.parameters[Tag.Date.tag].orEmpty(), "yyyy-MM-dd", true))
                call.respond(HttpStatusCode.UnprocessableEntity, ResponseError("date is not correct"))

            if (checkParametersQuery(call.parameters))
                call.respond(
                    HttpStatusCode.UnprocessableEntity,
                    ResponseError(
                        "At least 1 query (${Tag.GroupId.tag} or ${Tag.TeacherId.tag} " +
                                "or ${Tag.RoomId.tag}) parameter required"
                    )
                )

            viewModel.loadDayTimetable(
                call.parameters[Tag.Date.tag].orEmpty(),
                call.parameters[Tag.GroupId.tag]?.toInt(),
                call.parameters[Tag.TeacherId.tag]?.toInt(),
                call.parameters[Tag.RoomId.tag]?.toInt()
            )

            viewModel.timetableFlow.collectLatest {
                it.timetable[viewModel.generateTag(
                    call.parameters[Tag.GroupId.tag]?.toInt(),
                    call.parameters[Tag.TeacherId.tag]?.toInt(),
                    call.parameters[Tag.RoomId.tag]?.toInt()
                )]?.get(call.parameters[Tag.Date.tag].orEmpty())?.let {
                    if (it is Resource.Success)
                        call.respond(
                            HttpStatusCode.OK,
                            EventLessonResponse(
                                viewModel.getLessons(
                                    viewModel.generateTag(
                                        call.parameters[Tag.GroupId.tag]?.toInt(),
                                        call.parameters[Tag.TeacherId.tag]?.toInt(),
                                        call.parameters[Tag.RoomId.tag]?.toInt()
                                    ), DataTime.parseFromTimeTable(call.parameters[Tag.Date.tag].orEmpty())
                                ), viewModel.getEvents(
                                    call.parameters[Tag.OwnerId.tag]?.toIntOrNull() ?: -1,
                                    DataTime.parseFromTimeTable(call.parameters[Tag.Date.tag].orEmpty())
                                )
                            ),
                        )
                }
            }
        }

        post("/createUser/") {
            val parameters = call.receiveParameters()

            getParametersForCreateUser().forEach {
                if (parameters[it.tag] == null)
                    call.respond(
                        HttpStatusCode.UnprocessableEntity,
                        ResponseError("User's ${it.tag} parameter required")
                    )
            }

            viewModel.createUser(
                parameters[Tag.Login.tag].orEmpty(),
                parameters[Tag.Password.tag].orEmpty(),
                parameters[Tag.FirstName.tag].orEmpty(),
                parameters[Tag.SecondName.tag].orEmpty()
            ).let {
                if (it is Resource.Success)
                    call.respond(HttpStatusCode.Created, ResponseSusses(it.data))
                else
                    call.respond(HttpStatusCode.Conflict, ResponseError(it.message.orEmpty()))
            }
        }

        post("/auth/") {
            val parameters = call.receiveParameters()

            getParametersForAuthUser().forEach {
                if (parameters[it.tag] == null)
                    call.respond(
                        HttpStatusCode.UnprocessableEntity,
                        ResponseError("User's ${it.tag} parameter required")
                    )
            }

            viewModel.authUser(
                parameters[Tag.Login.tag].orEmpty(),
                parameters[Tag.Password.tag].orEmpty(),
            ).let {
                if (it is Resource.Success)
                    call.respond(HttpStatusCode.OK, it.data)
                else
                    call.respond(HttpStatusCode.Conflict, ResponseError(it.message.orEmpty()))
            }
        }

        get("/createEvent/") {
            getParametersForCreateEvent().forEach {
                if (call.parameters[it.tag] == null)
                    call.respond(
                        HttpStatusCode.UnprocessableEntity,
                        ResponseError("Event's ${it.tag} parameter required")
                    )
            }

            if (call.parameters[Tag.TypeId.tag]?.split(",")?.map { it.toIntOrNull() }?.any { it == null } == true)
                call.respond(HttpStatusCode.UnprocessableEntity, ResponseError("TypeId number is not correct"))

            viewModel.createEvent(
                Event(
                    -1,
                    call.parameters[Tag.Name.tag].orEmpty(),
                    call.parameters[Tag.Place.tag].orEmpty(),
                    call.parameters[Tag.DateTimeStart.tag].orEmpty(),
                    call.parameters[Tag.DateTimeEnd.tag].orEmpty(),
                ),
                call.parameters[Tag.TypeId.tag]?.split(",").orEmpty().map { it.toIntOrNull() }.filterNotNull()
            ).let {
                if (it is Resource.Success)
                    call.respond(HttpStatusCode.OK, ResponseSusses(it.data))
                else
                    call.respond(HttpStatusCode.Conflict, ResponseError(it.message.orEmpty()))
            }
        }

        get("/createType/") {
            getParametersForCreateType().forEach {
                if (call.parameters[it.tag] == null)
                    call.respond(
                        HttpStatusCode.UnprocessableEntity,
                        ResponseError("Type's ${it.tag} parameter required")
                    )
            }

            if (call.parameters[Tag.OwnerId.tag]?.toIntOrNull() == null)
                call.respond(HttpStatusCode.UnprocessableEntity, ResponseError("OwnerId number is not correct"))

            viewModel.createType(
                Type(
                    -1,
                    call.parameters[Tag.OwnerId.tag].orEmpty().toInt(),
                    call.parameters[Tag.Name.tag].orEmpty(),
                    call.parameters[Tag.ShortName.tag].orEmpty(),
                    call.parameters[Tag.LightColor.tag].orEmpty(),
                    call.parameters[Tag.DarkColor.tag].orEmpty(),
                )
            ).let {
                if (it is Resource.Success)
                    call.respond(HttpStatusCode.OK, ResponseSusses(it.data))
                else
                    call.respond(HttpStatusCode.Conflict, ResponseError(it.message.orEmpty()))
            }
        }

        get("/groups") {
            checkPagination(call)

            call.respond(
                HttpStatusCode.OK,
                viewModel.getPagination(
                    GroupDatabase.nameTable,
                    GroupDatabase.convertToGroupDatabase,
                    call.parameters[Tag.Page.tag]?.toIntOrNull() ?: 1,
                    call.parameters[Tag.PageSize.tag]?.toIntOrNull() ?: 50
                )
            )
        }

        get("/teachers") {
            checkPagination(call)

            call.respond(
                HttpStatusCode.OK,
                viewModel.getPagination(
                    TeacherDatabase.nameTable,
                    TeacherDatabase.convertToTeacherDatabase,
                    call.parameters[Tag.Page.tag]?.toIntOrNull() ?: 1,
                    call.parameters[Tag.PageSize.tag]?.toIntOrNull() ?: 50
                )
            )
        }

        get("/rooms") {
            checkPagination(call)

            call.respond(
                HttpStatusCode.OK,
                viewModel.getPagination(
                    RoomDatabase.nameTable,
                    RoomDatabase.convertToRoomDatabase,
                    call.parameters[Tag.Page.tag]?.toIntOrNull() ?: 1,
                    call.parameters[Tag.PageSize.tag]?.toIntOrNull() ?: 50
                )
            )
        }

        get("/typesLesson") {
            checkPagination(call)

            call.respond(
                HttpStatusCode.OK,
                viewModel.getPagination(
                    Type.nameTable,
                    Type.convertToType,
                    call.parameters[Tag.Page.tag]?.toIntOrNull() ?: 1,
                    call.parameters[Tag.PageSize.tag]?.toIntOrNull() ?: 50,
                    "WHERE ownerId = 0"
                )
            )
        }

        get("/buildings") {
            checkPagination(call)

            call.respond(
                HttpStatusCode.OK,
                viewModel.getPagination(
                    Building.nameTable,
                    Building.convertToBuilding,
                    call.parameters[Tag.Page.tag]?.toIntOrNull() ?: 1,
                    call.parameters[Tag.PageSize.tag]?.toIntOrNull() ?: 50
                )
            )
        }
    }
}

suspend fun checkPagination(call: ApplicationCall) {
    getParametersForPagination().forEach {
        if (call.parameters[it.tag] == null)
            call.respond(
                HttpStatusCode.UnprocessableEntity,
                ResponseError("""Parameter "${it.tag}" required""")
            )

        if (call.parameters[it.tag]?.toIntOrNull() == null)
            call.respond(HttpStatusCode.UnprocessableEntity, ResponseError("${it.tag} number is not correct"))
    }
}

fun checkParametersQuery(parameters: Parameters) =
    parameters[Tag.GroupId.tag] == null && parameters[Tag.TeacherId.tag] == null
            && parameters[Tag.RoomId.tag] == null

enum class Tag(val tag: String) {
    Year("year"),
    Week("week"),
    Date("date"),
    GroupId("groupId"),
    TeacherId("teacherId"),
    RoomId("roomId"),
    Login("login"),
    Password("password"),
    FirstName("firstName"),
    SecondName("secondName"),
    TypeId("typeId"),
    Name("name"),
    Place("place"),
    DateTimeStart("dateTimeStart"),
    DateTimeEnd("dateTimeEnd"),
    OwnerId("ownerId"),
    ShortName("shortName"),
    LightColor("lightColor"),
    DarkColor("darkColor"),
    Page("page"),
    PageSize("pageSize")
}

fun getParametersForPagination() = listOf(Tag.Page, Tag.PageSize)
fun getParametersForCreateType() = listOf(Tag.OwnerId, Tag.Name, Tag.ShortName, Tag.LightColor, Tag.DarkColor)
fun getParametersForCreateEvent() = listOf(Tag.TypeId, Tag.Name, Tag.Place, Tag.DateTimeStart, Tag.DateTimeEnd)
fun getParametersForAuthUser() = listOf(Tag.Login, Tag.Password)
fun getParametersForCreateUser() = getParametersForAuthUser() + listOf(Tag.FirstName, Tag.SecondName)