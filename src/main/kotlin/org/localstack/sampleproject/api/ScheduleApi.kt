package org.localstack.sampleproject.api

import com.fasterxml.jackson.databind.ObjectMapper
import org.localstack.sampleproject.util.apiGatewayFunction
import org.localstack.sampleproject.util.buildJsonResponse
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component


@Component
class ScheduleApi(private val objectMapper: ObjectMapper) {

    @Bean("SCHEDULE warmup")
    fun warmup() = apiGatewayFunction<ByteArray>(objectMapper) { input, context ->
        // execute scheduled events
        buildJsonResponse("OK")
    }
}