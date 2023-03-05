package io.github.criwen.todo.tests

import io.github.criwen.todo.model.ToDo
import io.kotest.assertions.asClue
import io.kotest.assertions.withClue
import io.kotest.common.runBlocking
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.ktor.client.call.*
import io.ktor.http.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.concurrent.atomic.AtomicInteger

class TodoManagerBlackboxTesting {

    val client = TodoApiClient()

    private var id = AtomicInteger(Instant.now().nano)

    private val nextId: Int
        get() = id.getAndIncrement()

    @Nested
    inner class CreateCases {

        @Test
        fun `create one todo, method should return 201 Created`() {
            val id = nextId

            val testData = ToDo(
                id = id,
                text = "Hello!",
                completed = false
            )

            withClue("Create new todo") {
                val createResponse = client.postTodos(testData)

                createResponse.status shouldBe HttpStatusCode.Created
            }
        }

        @Test
        fun `create one todo without mandatory field, method should return 400 Bad Request`() {
            val testData = ToDo(
                id = null,
                text = "Hello!",
                completed = false
            )

            withClue("Create new todo") {
                val createResponse = client.postTodos(testData)

                createResponse.status shouldBe HttpStatusCode.BadRequest
            }
        }

        @Test
        fun `create two todo with same id, method should return 400 Bad Request`() {
            val id = nextId

            val testData = ToDo(
                id = id,
                text = "Hello!",
                completed = false
            )

            withClue("Create first todo") {
                val createResponse = client.postTodos(testData)

                createResponse.status shouldBe HttpStatusCode.Created
            }

            withClue("Create second todo") {
                val createResponse = client.postTodos(testData)

                createResponse.status shouldBe HttpStatusCode.BadRequest
            }
        }
    }

    @Nested
    inner class GetCases {

        @Test
        fun `create one todo, method should return 200 OK with created todo`() {
            val id = nextId

            val testData = ToDo(
                id = id,
                text = "Hello!",
                completed = false
            )

            withClue("Create new todo") {
                val createResponse = client.postTodos(testData)

                createResponse.status shouldBe HttpStatusCode.Created
            }

            withClue("Get created todo") {
                val getResponse = client.getTodos()

                val body = runBlocking { getResponse.body<List<ToDo>>() }

                body shouldContain testData
            }
        }
    }

    @Nested
    inner class GetWithPaginationCases {

        @Test
        fun `create four todos, positive pagination cases`() {
            val testDataSet = listOf(
                ToDo(
                    id = nextId,
                    text = "Hello",
                    completed = false
                ),
                ToDo(
                    id = nextId,
                    text = "World",
                    completed = false
                ),
                ToDo(
                    id = nextId,
                    text = "Good",
                    completed = false
                ),
                ToDo(
                    id = nextId,
                    text = "Day",
                    completed = false
                )
            )

            testDataSet.forEach { testData ->
                testData.asClue {
                    val createResponse = client.postTodos(testData)

                    createResponse.status shouldBe HttpStatusCode.Created
                }
            }

            withClue("Pagination cases") {
                withClue("limit = 1, offset = 0") {
                    val response = runBlocking { client.getTodos(limit = 1, offset = 0).body<List<ToDo>>() }

                    response.shouldHaveSize(1)
                }

                withClue("limit = 1, offset = 1") {
                    val response = runBlocking { client.getTodos(limit = 1, offset = 1).body<List<ToDo>>() }

                    response.shouldHaveSize(1)
                }

                withClue("limit = 1, offset = 2") {
                    val response = runBlocking { client.getTodos(limit = 1, offset = 2).body<List<ToDo>>() }

                    response.shouldHaveSize(1)
                }

                withClue("limit = 2, offset = 0") {
                    val response = runBlocking { client.getTodos(limit = 2, offset = 0).body<List<ToDo>>() }

                    response.shouldHaveSize(2)
                }

                withClue("limit = 2, offset = 1") {
                    val response = runBlocking { client.getTodos(limit = 2, offset = 1).body<List<ToDo>>() }

                    response.shouldHaveSize(2)
                }

                withClue("limit = 2, offset = 99999") {
                    val response = runBlocking { client.getTodos(limit = 2, offset = 99999).body<List<ToDo>>() }

                    response.shouldBeEmpty()
                }
            }
        }
    }

    @Nested
    inner class UpdateCases {

        @Test
        fun `create one todo and update it, should return 200 OK`() {
            val id = nextId

            val testData = ToDo(
                id = id,
                text = "Hello!",
                completed = false
            )

            withClue("Create new todo") {
                val createResponse = client.postTodos(testData)

                createResponse.status shouldBe HttpStatusCode.Created
            }

            val updatedTestData = ToDo(
                id = id,
                text = "World",
                completed = true
            )

            withClue("Update todo") {
                val updateResponse = client.putTodosById(id, updatedTestData)

                updateResponse.status shouldBe HttpStatusCode.OK
            }

            withClue("Get updated todo") {
                val getResponse = client.getTodos()

                val body = runBlocking { getResponse.body<List<ToDo>>() }

                body shouldContain updatedTestData
            }
        }

        @Test
        fun `update non-existing todo, should return 404 Not Found`() {
            val id = nextId

            val updatedTestData = ToDo(
                id = id,
                text = "World",
                completed = true
            )

            withClue("Update todo") {
                val updateResponse = client.putTodosById(999999999, updatedTestData)

                updateResponse.status shouldBe HttpStatusCode.NotFound
            }
        }
    }

    @Nested
    inner class DeleteCases {

        @Test
        fun `create one todo and delete it, should return 204 No Content`() {
            val id = nextId

            val testData = ToDo(
                id = id,
                text = "Hello!",
                completed = false
            )

            withClue("Create new todo") {
                val createResponse = client.postTodos(testData)

                createResponse.status shouldBe HttpStatusCode.Created
            }

            withClue("Delete todo") {
                val updateResponse = client.deleteTodosById(id)

                updateResponse.status shouldBe HttpStatusCode.NoContent
            }
        }

        @Test
        fun `delete non-existing todo, should return 404 Not Found`() {
            withClue("Delete todo") {
                val updateResponse = client.deleteTodosById(999999999)

                updateResponse.status shouldBe HttpStatusCode.NotFound
            }
        }
    }
}