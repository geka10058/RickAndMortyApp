package com.example.rickmortyapp.data.json_models.episodes_data_classes

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EpisodeResult(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String?,
    @Json(name = "air_date") val airDate: String?,
    @Json(name = "episode") val episode: String?,
    @Json(name = "characters") val characters: List<String>?,
)
