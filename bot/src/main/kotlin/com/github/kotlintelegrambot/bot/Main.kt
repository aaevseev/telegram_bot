package com.github.kotlintelegrambot.bot

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.telegramError
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.network.CONFIG
import java.io.BufferedReader
import java.io.InputStreamReader

fun main(args: Array<String>) {

    CONFIG.BOT_NAME = args[0]
    CONFIG.BOT_TOKEN = args[1]
    CONFIG.OPEN_AI_TOKEN = args[2]
    CONFIG.STABLE_DIFFUSION = args[3]

    val bot = bot {
        token = CONFIG.BOT_TOKEN

        dispatch {
            text {
                when {
                    message.replyToMessage?.from?.id == bot.getMe().get().id -> {
                        val result = bot.getCompletions(text = message.text.orEmpty())
                        bot.sendMessage(
                            chatId = ChatId.fromId(message.chat.id),
                            parseMode = ParseMode.MARKDOWN,
                            replyToMessageId = ChatId.fromId(message.messageId).id,
                            text = result?.choices?.get(0)?.message?.content.toString()
                        )
                    }
                    text == "${CONFIG.BOT_NAME}, сервер" -> {
                        bot.sendMessage(
                            chatId = ChatId.fromId(message.chat.id),
                            parseMode = ParseMode.MARKDOWN,
                            replyToMessageId = ChatId.fromId(message.messageId).id,
                            text = "Свободная память: \n```${startCommand("df -h /")}```" +
                                "\nUptime: ```${startCommand("uptime")}```" +
                                "\nЗагрузка CPU: ```${startCommand("vmstat 1 2 | awk 'FNR>3{print $(NF-3)}'")}%```"
                        )
                    }
                    message.chat.type == "private" -> {
                        val result = bot.getCompletions(text = message.text.orEmpty())
                        bot.sendMessage(
                            chatId = ChatId.fromId(message.chat.id),
                            replyToMessageId = ChatId.fromId(message.messageId).id,
                            text = result?.choices?.get(0)?.message?.content.toString()
                        )
                    }
                    text.contains("${CONFIG.BOT_NAME},") -> {
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
