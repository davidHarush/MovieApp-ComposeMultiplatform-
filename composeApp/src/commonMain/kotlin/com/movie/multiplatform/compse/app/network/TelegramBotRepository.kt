package com.movie.multiplatform.compse.app.network


import com.movie.multiplatform.compse.app.getPlatform
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json


//@Composable
//fun testTelegramBot() {
//    rememberCoroutineScope().launch {
//        TelegramBotRepository().sendMessageToChat("Hello from Compose  -> device: ${getPlatform().name}")
////            val stickerFileId =
////                "CAACAgQAAxkBAAEuxWJnI0kknwek2BEc-_ihqOvdJorztwACAQcAApWtnAForTr_KwnG6zYE"
////            sendStickerToChat(stickerFileId)
////            sendWelcomeMessageWithInlineKeyboard()
////            startListeningForCallbackQueries(this@launch)
//
//    }
//
//
//}

class TelegramBotRepository {

    private val botToken = "7589068254:AAFSAmR3Bumywa6L9EnW_twwVxSIBsAAaJU"
    private val groupId: String = "-1002391522958"
    private var lastUpdateId: Long = 0


    val client = HttpClient(getPlatform().httpClientEngine) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        install(Logging) {
            level = LogLevel.ALL
            logger = Logger.DEFAULT
        }
    }

    suspend fun sendMessageToChat(message: String) {
        val url = "https://api.telegram.org/bot$botToken/sendMessage"
        val response: HttpResponse = client.post(url) {
            contentType(ContentType.Application.Json)
            setBody(TelegramMessageRequest(chat_id = groupId, text = message))
        }
        println("sendMessageToChat response: ${response.status}, body: ${response.bodyAsText()}")
    }

    suspend fun getChatAdministrators(): HttpResponse {
        val url = "https://api.telegram.org/bot$botToken/getChatAdministrators"
        val response: HttpResponse = client.get(url) {
            contentType(ContentType.Application.Json)
            parameter("chat_id", groupId)
        }
        println("getChatAdministrators response: ${response.status}, body: ${response.bodyAsText()}")
        return response
    }

    suspend fun sendPhotoToChat(photoUrl: String, caption: String = "") {
        val url = "https://api.telegram.org/bot$botToken/sendPhoto"
        val response: HttpResponse = client.post(url) {
            contentType(ContentType.Application.Json)
            setBody(TelegramPhotoRequest(chat_id = groupId, photo = photoUrl, caption = caption))
        }
        println("sendPhotoToChat response: ${response.status}, body: ${response.bodyAsText()}")
    }

    suspend fun sendStickerToChat(stickerUrl: String) {
        val url = "https://api.telegram.org/bot$botToken/sendSticker"
        val response: HttpResponse = client.post(url) {
            contentType(ContentType.Application.Json)
            setBody(TelegramStickerRequest(chat_id = groupId, sticker = stickerUrl))
        }
        println("sendStickerToChat response: ${response.status}, body: ${response.bodyAsText()}")
    }

    suspend fun editMessage(chatId: String, messageId: Int, newText: String) {
        val url = "https://api.telegram.org/bot$botToken/editMessageText"
        val response: HttpResponse = client.post(url) {
            contentType(ContentType.Application.Json)
            setBody(
                TelegramEditMessageRequest(
                    chat_id = chatId,
                    message_id = messageId,
                    text = newText
                )
            )
        }
        println("editMessage response: ${response.status}, body: ${response.bodyAsText()}")
    }

    suspend fun getChatDetails(): HttpResponse {
        val url = "https://api.telegram.org/bot$botToken/getChat"
        val response: HttpResponse = client.get(url) {
            contentType(ContentType.Application.Json)
            parameter("chat_id", groupId)
        }
        println("getChatDetails response: ${response.status}, body: ${response.bodyAsText()}")
        return response
    }

    suspend fun kickChatMember(userId: Int) {
        val url = "https://api.telegram.org/bot$botToken/kickChatMember"
        val response: HttpResponse = client.post(url) {
            contentType(ContentType.Application.Json)
            parameter("chat_id", groupId)
            parameter("user_id", userId)
        }
        println("kickChatMember response: ${response.status}, body: ${response.bodyAsText()}")
    }

    suspend fun sendLocation(latitude: Double, longitude: Double) {
        val url = "https://api.telegram.org/bot$botToken/sendLocation"
        val response: HttpResponse = client.post(url) {
            contentType(ContentType.Application.Json)
            setBody(
                TelegramLocationRequest(
                    chat_id = groupId,
                    latitude = latitude,
                    longitude = longitude
                )
            )
        }
        println("sendLocation response: ${response.status}, body: ${response.bodyAsText()}")
    }

    suspend fun sendMessageWithInlineKeyboard(message: String) {
        val url = "https://api.telegram.org/bot$botToken/sendMessage"
        val inlineKeyboardMarkup = InlineKeyboardMarkup(
            inline_keyboard = listOf(
                listOf(InlineKeyboardButton(text = "Button 1", callback_data = "data1")),
                listOf(InlineKeyboardButton(text = "Button 2", callback_data = "data2"))
            )
        )
        val response: HttpResponse = client.post(url) {
            contentType(ContentType.Application.Json)
            setBody(
                TelegramMessageWithKeyboardRequest(
                    chat_id = groupId,
                    text = message,
                    reply_markup = inlineKeyboardMarkup
                )
            )
        }
        println("sendMessageWithInlineKeyboard response: ${response.status}, body: ${response.bodyAsText()}")
    }

    suspend fun sendMessageWithReplyKeyboard(message: String) {
        val url = "https://api.telegram.org/bot$botToken/sendMessage"
        val replyKeyboardMarkup = ReplyKeyboardMarkup(
            keyboard = listOf(
                listOf(ReplyKeyboardButton(text = "Reply Button 1")),
                listOf(ReplyKeyboardButton(text = "Reply Button 2"))
            )
        )
        val response: HttpResponse = client.post(url) {
            contentType(ContentType.Application.Json)
            setBody(
                TelegramMessageWithReplyKeyboardRequest(
                    chat_id = groupId,
                    text = message,
                    reply_markup = replyKeyboardMarkup
                )
            )
        }
        println("sendMessageWithReplyKeyboard response: ${response.status}, body: ${response.bodyAsText()}")
    }


    suspend fun sendWelcomeMessageWithInlineKeyboard() {
        val url = "https://api.telegram.org/bot$botToken/sendMessage"
        val inlineKeyboardMarkup = InlineKeyboardMarkup(
            inline_keyboard = listOf(
                listOf(InlineKeyboardButton(text = "Contact us", callback_data = "contact_us")),
                listOf(
                    InlineKeyboardButton(
                        text = "Additional links",
                        callback_data = "additional_links"
                    )
                )
            )
        )
        val response: HttpResponse = client.post(url) {
            contentType(ContentType.Application.Json)
            setBody(
                TelegramMessageWithKeyboardRequest(
                    chat_id = groupId,
                    text = "Welcome to our bot",
                    reply_markup = inlineKeyboardMarkup
                )
            )
        }
        println("sendWelcomeMessageWithInlineKeyboard response: ${response.status}, body: ${response.bodyAsText()}")
    }


    fun startListeningForCallbackQueries(scope: CoroutineScope) {
        scope.launch(Dispatchers.IO) {
            while (isActive) {
                handleCallbackQueries()
                delay(2000)
            }
        }
    }

    suspend fun handleCallbackQueries() {
        val url = "https://api.telegram.org/bot$botToken/getUpdates"
        val response: HttpResponse = client.get(url) {
            contentType(ContentType.Application.Json)
            parameter("offset", lastUpdateId + 1)

        }

        val json = Json { ignoreUnknownKeys = true }
        val updates = json.decodeFromString<GetUpdatesResponse>(response.bodyAsText())


        updates.result.forEach { update ->
            update.callback_query?.let { callbackQuery ->
                val data = callbackQuery.data
                val chatId = callbackQuery.message?.chat?.id

                if (chatId != null) {
                    when (data) {
                        "contact_us" -> sendMessageToChatWithId(chatId, "did you click contact us ?")
                        "additional_links" -> sendMessageToChatWithId(
                            chatId,
                            " did you click additional links ?"
                        )

                        else -> sendMessageToChatWithId(chatId, "I don't know what you want")
                    }
                }
            }
            lastUpdateId = update.update_id
        }
    }

    suspend fun sendMessageToChatWithId(chatId: Long, message: String) {
        val url = "https://api.telegram.org/bot$botToken/sendMessage"
        val response: HttpResponse = client.post(url) {
            contentType(ContentType.Application.Json)
            setBody(TelegramMessageRequest(chat_id = chatId.toString(), text = message))
        }
        println("sendMessageToChatWithId response: ${response.status}, body: ${response.bodyAsText()}")
    }

    @Serializable
    data class GetUpdatesResponse(val ok: Boolean, val result: List<Update>)

    @Serializable
    data class Update(
        val update_id: Long,
        val callback_query: CallbackQuery? = null
    )

    @Serializable
    data class CallbackQuery(
        val id: String,
        val from: User,
        val message: Message? = null,
        val chat_instance: String,
        val data: String
    )

    @Serializable
    data class User(
        val id: Long,
        val is_bot: Boolean,
        val first_name: String,
        val username: String? = null
    )

    @Serializable
    data class Message(
        val message_id: Long,
        val chat: Chat,
        val text: String? = null
    )

    @Serializable
    data class Chat(val id: Long, val type: String)


    @Serializable
    data class TelegramMessageRequest(val chat_id: String, val text: String)

    @Serializable
    data class TelegramPhotoRequest(
        val chat_id: String,
        val photo: String,
        val caption: String = ""
    )

    @Serializable
    data class TelegramStickerRequest(val chat_id: String, val sticker: String)

    @Serializable
    data class TelegramEditMessageRequest(
        val chat_id: String,
        val message_id: Int,
        val text: String
    )

    @Serializable
    data class TelegramLocationRequest(
        val chat_id: String,
        val latitude: Double,
        val longitude: Double
    )

    @Serializable
    data class InlineKeyboardButton(val text: String, val callback_data: String)

    @Serializable
    data class InlineKeyboardMarkup(val inline_keyboard: List<List<InlineKeyboardButton>>)

    @Serializable
    data class TelegramMessageWithKeyboardRequest(
        val chat_id: String,
        val text: String,
        val reply_markup: InlineKeyboardMarkup
    )

    @Serializable
    data class ReplyKeyboardButton(val text: String)

    @Serializable
    data class ReplyKeyboardMarkup(val keyboard: List<List<ReplyKeyboardButton>>)

    @Serializable
    data class TelegramMessageWithReplyKeyboardRequest(
        val chat_id: String,
        val text: String,
        val reply_markup: ReplyKeyboardMarkup
    )
}