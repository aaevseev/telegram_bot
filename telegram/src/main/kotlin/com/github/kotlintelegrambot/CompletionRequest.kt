package com.github.kotlintelegrambot

import com.google.gson.annotations.SerializedName as Name

data class CompletionRequest(
    @Name("model")
    val model: String = "gpt-4",
    @Name("messages")
    val messages: List<Messages>
)

data class Messages(
    @Name("role")
    val role: String = "user",
    @Name("content")
    val content: String,
)
