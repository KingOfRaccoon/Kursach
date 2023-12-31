package com.timetableGuap

import com.timetableGuap.database.DatabaseFactory
import com.timetableGuap.database.data.*
import com.timetableGuap.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        application {
            configureRouting()
        }
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Hello World!", bodyAsText())
        }
    }

    private val database = DatabaseFactory("test")
    private val list: List<DatabaseItem> = listOf(
        Building(0, "test", "t"),
        Type(2, "fdf", "f", "", ""),
        Event(1, 2, "test", "test", "2023-11-15T09:30:00Z", "2023-11-15T11:30:00Z"),
        GroupDatabase(3, "test", 0),
        SubjectDatabase(6, "test", 90, 2),
        LessonDatabase(4, 1, "2023-11-15T09:30:00Z", "groupId: 0", 6),
        TeacherDatabase(5, "te", "tet", "tewt", -1, "", ""),
        User(8, "ere", "dfs"),
        EventType(1, 2),
        RoomDatabase(0, "11-11"),
        LessonGroup(3, 4),
        LessonRoom(0, "11-11", 4),
        LessonTeacher(5, 4),
        TypeUser(8, 2),
        MarkerDatabase("dfkfjdkfjflskflsdtest11dsfddfdffdfd2321")
    )

    @Test
    fun testDatabase(){
        database.addItemsInDatabase(list)
    }
}
