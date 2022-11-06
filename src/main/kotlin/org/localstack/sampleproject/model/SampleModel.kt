package org.localstack.sampleproject.model

import com.fasterxml.jackson.annotation.JsonIgnore

data class SampleModel(
    val id: Int,
    val name: String,

    @JsonIgnore
    val jsonIgnoredProperty: String? = null,
)