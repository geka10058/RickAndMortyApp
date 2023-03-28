package com.example.rickmortyapp.data.json_models.characters_data_classes

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CharacterResult(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String?,
    @Json(name = "status") val status: String?,
    @Json(name = "species") val species: String?,
    @Json(name = "gender") val gender: String?,
    @Json(name = "origin") val origin: Origin?,
    @Json(name = "location") val location: Location?,
    @Json(name = "image") val image: String?,
)