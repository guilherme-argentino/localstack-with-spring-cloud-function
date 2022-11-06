package org.localstack.sampleproject.config

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import java.text.DateFormat

@Configuration
class JacksonConfiguration {

    @Bean
    fun jacksonBuilder() = Jackson2ObjectMapperBuilder()
        .dateFormat(DateFormat.getDateInstance(DateFormat.FULL))

    @Bean
    @Primary
    fun objectMapper(): ObjectMapper = ObjectMapper().apply {
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true)
        configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true)
        configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true)
        setSerializationInclusion(JsonInclude.Include.NON_NULL)
        findAndRegisterModules()
    }
}