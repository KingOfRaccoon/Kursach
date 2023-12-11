package com.timetableGuap.plugins

import com.timetableGuap.database.data.*
import com.timetableGuap.network.response.serializer.GroupSerializer
import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        serialization(ContentType.Application.Json, Json {
            this.serializersModule = SerializersModule {
                this.polymorphic(DatabaseItem::class) {
                    subclass(GroupDatabase::class, GroupDatabase.serializer())
                    subclass(TeacherDatabase::class, TeacherDatabase.serializer())
                    subclass(RoomDatabase::class, RoomDatabase.serializer())
                    subclass(Type::class, Type.serializer())
                    subclass(Building::class, Building.serializer())
                }
            }
        })
    }
    routing {
        get("/json/kotlinx-serialization") {
            call.respond(mapOf("hello" to "world"))
        }
    }
}
