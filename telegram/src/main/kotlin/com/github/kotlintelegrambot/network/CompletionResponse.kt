package com.github.kotlintelegrambot.network

import com.google.gson.annotations.SerializedName


data class CompletionResponse(
    val id: String,
    @SerializedName("object")
    val object_field: String,
    val created: Long,
    val model: String,
    val choices: List<Choice>,
    val usage: Usage,
)

data class Choice(
   val message: Message
)

data class Message(
    @SerializedName("role")
    val role: String = "user",
    @SerializedName("content")
    val content: String,
)

data class Usage(
    @SerializedName("prompt_tokens")
    val promptTokens: Long,
    @SerializedName("completion_tokens")
    val completionTokens: Long,
    @SerializedName("total_tokens")
    val totalTokens: Long,
)
