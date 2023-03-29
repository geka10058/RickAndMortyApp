package com.example.rickmortyapp.data.models.locations_data_classes

import com.example.rickmortyapp.data.models.Info
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LocationResponse(
    @Json(name = "info") val info: Info,
    @Json(name = "results") val locationResults: List<LocationResult>
)