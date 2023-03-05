package io.github.criwen.todo.model

@kotlinx.serialization.Serializable
data class ToDo(
    val id: Int?,
    val text: String?,
    val completed: Boolean?
)