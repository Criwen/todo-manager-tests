package io.github.criwen.todo.benchmark

import io.github.criwen.todo.model.ToDo
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.infra.Blackhole

open class CreateTodoBenchmark {

    @Benchmark
    fun createNewTodo(client: Client, blackhole: Blackhole) {
        val testData = ToDo(
            id = client.nextId,
            text = "Hello",
            completed = false
        )

        blackhole.consume(
            client.client.postTodos(testData)
        )
    }
}