package io.github.criwen.todo.benchmark

import io.github.criwen.todo.tests.TodoApiClient
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import java.time.Instant
import java.util.concurrent.atomic.AtomicInteger

@State(Scope.Benchmark)
open class Client {

    val client = TodoApiClient()

    private var id = AtomicInteger(Instant.now().nano)

    val nextId: Int
        get() = id.getAndIncrement()
}