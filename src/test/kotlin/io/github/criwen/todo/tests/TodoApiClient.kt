package io.github.criwen.todo.tests

import io.github.criwen.todo.model.ToDo
import io.kotest.common.runBlocking
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*

class TodoApiClient {

    private val client = HttpClient(Apache) {
        install(Logging) {
            logger = Logger.SIMPLE
            level = LogLevel.ALL
        }

        install(ContentNegotiation) {
            json()
        }

        install(Auth) {
            basic {
                credentials {
                    BasicAuthCredentials("admin", "admin")
                }
            }
        }
    }

    fun getTodos(limit: Int? = null, offset: Int? = null): HttpResponse {
        return runBlocking {
            client.get("http://localhost:8080/todos") {
                limit?.let {
                    parameter("limit", limit)
                }

                offset?.let {
                    parameter("offset", offset)
                }
            }
        }
    }

    fun postTodos(todo: ToDo): HttpResponse {
        return runBlocking {
            client.post("http://localhost:8080/todos") {
                setBody(todo)
                contentType(ContentType.Application.Json)
            }
        }
    }

    fun putTodosById(id: Int, todo: ToDo): HttpResponse {
        return runBlocking {
            client.put("http://localhost:8080/todos/$id") {
                setBody(todo)
                contentType(ContentType.Application.Json)
            }
        }
    }

    fun deleteTodosById(id: Int): HttpResponse {
        return runBlocking {
            client.delete("http://localhost:8080/todos/$id") // TODO: as segment
        }
    }
}