package com.example.rickmortyapp.api

import com.example.rickmortyapp.data.characters_data_classes.CharacterResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers

interface ApiRickMorty {

    @Headers("Content-type: application/json")
    @GET("character")
    fun getCharacterResponse(): Call<CharacterResponse>
}