package com.example.rickmortyapp.data.characters_data_classes

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CharacterResponse(
    @Json(name = "info") val info: Info,
    @Json(name = "results") val characterResults: List<CharacterResult>
)