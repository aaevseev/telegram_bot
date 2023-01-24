package com.github.kotlintelegrambot.bot

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.telegramError
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.network.CONFIG.BOT_NAME
import com.github.kotlintelegrambot.network.CONFIG.BOT_TOKEN
import com.github.kotlintelegrambot.network.CONFIG.OPEN_AI_TOKEN
import java.io.BufferedReader
import java.io.InputStreamReader

fun main(args: Array<String>) {

    BOT_NAME = args[0]
    BOT_TOKEN = args[1]
    OPEN_AI_TOKEN = args[2]

    val bot = bot {
        token = BOT_TOKEN

        dispatch {
            text {
                when {
                    message.chat.type == "private" -> {
                        val result = bot.getCompletions(text = message.text.orEmpty())
                        bot.sendMessage(
                            chatId = ChatId.fromId(message.chat.id),
                            replyToMessageId = ChatId.fromId(message.messageId).id,
                            text = result?.choices?.get(0)?.message?.content.toString()
                        )
                    }
                    text == "$BOT_NAME, сервер" -> {
                        bot.sendMessage(
                            chatId = ChatId.fromId(message.chat.id),
                            parseMode = ParseMode.MARKDOWN,
                            replyToMessageId = ChatId.fromId(message.messageId).id,
                            text = "Свободная память: \n```${startCommand("df -h /")}```" +
                                "\nUptime: ```${startCommand("uptime")}```" +
                                "\nЗагрузка CPU: ```${startCommand("vmstat 1 2 | awk 'FNR>3{print $(NF-3)}'")}%```"
                        )
                    }
                    text.contains("$BOT_NAME,") -> {
                        val result = bot.getCompletions(text = message.text.orEmpty())
                        bot.sendMessage(
                            chatId = ChatId.fromId(message.chat.id),
                            parseMode = ParseMode.MARKDOWN,
                            replyToMessageId = ChatId.fromId(message.messageId).id,
                            text = result?.choices?.get(0)?.message?.content.toString()
                        )
                    }
                }
            }
            telegramError {
                println(error.getErrorMessage())
            }
        }
    }

    bot.startPolling()
}

fun startCommand(command: String): String {
    val process = Runtime.getRuntime().exec(command)
    return try {
        if (process.waitFor() == 0) {
            val inputStream = process.inputStream
            val inputStreamReader = InputStreamReader(inputStream)
            BufferedReader(inputStreamReader).readLines().joinToString("\n")
        } else {
            process.waitFor().toString()
        }
    } catch (e: InterruptedException) {
        e.printStackTrace()
        e.toString()
    }
}
