package com.github.kotlintelegrambot.network

import com.github.kotlintelegrambot.CompletionRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

internal interface OpenAIService {

    @POST("chat/completions")
    fun getCompletions(
        @Header("Authorization") auth: String = "Bearer ${CONFIG.OPEN_AI_TOKEN}",
        @Body completionRequest: CompletionRequest
    ): Call<CompletionResponse>

}
