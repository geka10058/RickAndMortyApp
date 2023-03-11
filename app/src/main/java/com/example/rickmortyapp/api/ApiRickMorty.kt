package com.example.rickmortyapp.api

import com.example.rickmortyapp.data.models.characters_data_classes.CharacterResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface ApiRickMorty {

    @Headers("Content-type: application/json")
    @GET("character/?")
    fun getCharacterResponse(@Query("page") page: Int): Call<CharacterResponse>

    companion object{
        const val BASE_URL = "https://rickandmortyapi.com/api/"
    }
}