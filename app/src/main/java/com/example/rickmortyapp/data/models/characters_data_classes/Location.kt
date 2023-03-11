package com.example.rickmortyapp.data.models.characters_data_classes

import com.squareup.moshi.Json

data class Location(
    @Json(name = "name") val name: String?,
    @Json(name = "url") val url: String?
)