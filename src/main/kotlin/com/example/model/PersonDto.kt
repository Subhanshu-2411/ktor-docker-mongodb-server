package com.example.model

import kotlinx.serialization.Serializable

@Serializable
data class PersonDto(
    val id: String? = null,
    val name: String,
    val age: Int
)