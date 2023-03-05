package com.example.rickmortyapp.data.characters_data_classes

import com.squareup.moshi.Json

data class Origin(
    @Json(name = "name") val name: String?,
    @Json(name = "url") val url: String?
)