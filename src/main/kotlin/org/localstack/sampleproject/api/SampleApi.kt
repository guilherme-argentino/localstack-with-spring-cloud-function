package org.localstack.sampleproject.api

import com.fasterxml.jackson.databind.ObjectMapper
import org.localstack.sampleproject.model.SampleModel
import org.localstack.sampleproject.util.Logger
import org.localstack.sampleproject.util.apiGatewayFunction
import org.localstack.sampleproject.util.buildJsonResponse
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component


private val SAMPLE_RESPONSE = mutableListOf(
    SampleModel(id = 1, name = "Sample #1"),
    SampleModel(id = 2, name = "Sample #2"),
)


@Component
class SampleApi(private val objectMapper: ObjectMapper) {

    companion object : Logger()

    @Bean("POST /v1/entities")
    fun createSampleEntity() = apiGatewayFunction<SampleModel>(objectMapper) { input, context ->
        LOGGER.info("calling POST /v1/entities")
        SAMPLE_RESPONSE.add(input.payload)
        buildJsonResponse(input.payload, code = 201)
    }

    @Bean("GET /v1/entities")
    fun listSampleEntities() = apiGatewayFunction<ByteArray>(objectMapper) { input, context ->
        LOGGER.info("calling GET /v1/entities")
        buildJsonResponse("hello world")
    }

    @Bean("GET /v1/entities/get")
    fun getSampleEntity() = apiGatewayFunction<ByteArray>(objectMapper) { input, context ->
        LOGGER.info("calling GET /v1/entities/get")
        val desiredId = context.queryStringParameters["id"]!!.toInt()
        buildJsonResponse(SAMPLE_RESPONSE.find { it.id == desiredId })
    }
}