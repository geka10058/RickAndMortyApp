package com.example.rickmortyapp.data.json_models.episodes_data_classes

import com.example.rickmortyapp.data.json_models.Info
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EpisodeResponse(
    @Json(name = "info") val info: Info,
    @Json(name = "results") val episodeResults: List<EpisodeResult>?
)