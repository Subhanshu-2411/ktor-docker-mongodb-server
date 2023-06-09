package com.example.plugins

import com.example.model.ErrorResponse
import com.example.model.Person
import com.example.model.PersonDto
import com.example.service.PersonService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlin.text.get

fun Application.configureRouting() {

    val service = PersonService()

    routing {
        route("/person") {
            post {
                val request = call.receive<PersonDto>()
                val person = request.toPerson()

                service.create(person)
                    ?.let { userId ->
                        call.response.headers.append("My-User-Id-Header", userId.toString())
                        call.respond(HttpStatusCode.Created)
                    } ?: call.respond(HttpStatusCode.BadRequest, ErrorResponse.BAD_REQUEST_RESPONSE)
            }

            get {
                val peopleList =
                    service.findAll()
                        .map(Person::toDto)

                call.respond(peopleList)
            }

            get("/{id}") {
                val id = call.parameters["id"].toString()

                service.findById(id)
                    ?.let { foundPerson -> call.respond(foundPerson.toDto()) }
                    ?: call.respond(HttpStatusCode.NotFound, ErrorResponse.NOT_FOUND_RESPONSE)
            }

            put("/{id}") {
                val id = call.parameters["id"].toString()
                val personRequest = call.receive<PersonDto>()
                val person = personRequest.toPerson()

                val updatedSuccessfully = service.updateById(id, person)

                if (updatedSuccessfully) {
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse.BAD_REQUEST_RESPONSE)
                }
            }

            delete("/{id}") {
                val id = call.parameters["id"].toString()

                val deletedSuccessfully = service.deleteById(id)

                if (deletedSuccessfully) {
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    call.respond(HttpStatusCode.NotFound, ErrorResponse.NOT_FOUND_RESPONSE)
                }
            }
        }
    }
}