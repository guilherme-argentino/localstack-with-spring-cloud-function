package org.localstack.sampleproject.api

import com.amazonaws.services.lambda.runtime.events.DynamodbEvent
import org.localstack.sampleproject.model.SampleModel
import org.localstack.sampleproject.util.Logger
import org.springframework.cloud.function.adapter.aws.SpringBootStreamHandler
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import java.util.function.Function

@Component
class LambdaApi : SpringBootStreamHandler() {

    companion object : Logger()

    @Bean
    fun functionOne(): Function<Any, String> {
        return Function {
            LOGGER.info("calling function one")
            return@Function "ONE";
        }
    }

    @Bean
    fun functionTwo(): Function<SampleModel, SampleModel> {
        return Function {
            LOGGER.info("calling function two")
            return@Function it;
        }
    }

    @Bean
    fun dynamoDbStreamHandlerExample(): Function<DynamodbEvent, Unit> {
        return Function {
            LOGGER.info("handling DynamoDB stream event")
        }
    }
}