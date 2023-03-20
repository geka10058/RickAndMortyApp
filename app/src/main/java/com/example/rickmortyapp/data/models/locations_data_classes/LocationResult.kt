package com.example.rickmortyapp.data.models.locations_data_classes

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LocationResult(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String?,
    @Json(name = "type") val type: String?,
    @Json(name = "dimension") val dimension: String?
)