package org.localstack.sampleproject.util

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.messaging.Message
import java.util.function.Function

fun <T>apiGatewayFunction(
    objectMapper: ObjectMapper,
    callable: (message: Message<T>, context: APIGatewayProxyRequestEvent) -> Message<*>
): Function<Message<T>, Message<*>> = Function { input ->
    try {
        val context = objectMapper.readValue(
            objectMapper.writeValueAsString(input.headers),
            APIGatewayProxyRequestEvent::class.java
        )

        return@Function callable(input, context)
    } catch (e: Throwable) {
        val message = e.message?.replace("\n", "")?.replace("\"", "'")
        return@Function buildJsonErrorResponse(message ?: "", 500)
    }
}