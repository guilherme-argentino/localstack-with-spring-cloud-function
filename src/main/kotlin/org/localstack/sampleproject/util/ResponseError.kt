package org.localstack.sampleproject.util

import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder

data class ResponseError(
    val message: String,
)

fun <T>buildJsonResponse(data: T, code: Int = 200): Message<T> {
    return MessageBuilder
        .withPayload(data)
        .setHeader("Content-Type", "application/json")
        .setHeader("Access-Control-Allow-Origin", "*")
        .setHeader("Access-Control-Allow-Methods", "OPTIONS,POST,GET")
        .setHeader("statusCode", code)
        .build()
}

fun buildJsonErrorResponse(message: String, code: Int = 500) =
    buildJsonResponse(ResponseError(message), code)
