package com.example.rickmortyapp.api

import com.example.rickmortyapp.data.models.characters_data_classes.CharacterResponse
import com.example.rickmortyapp.data.models.characters_data_classes.CharacterResult
import com.example.rickmortyapp.data.models.locations_data_classes.LocationResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiRickMorty {

    @Headers("Content-type: application/json")
    @GET("character/?")
    fun getCharacterResponse(@Query("page") page: Int): Call<CharacterResponse>

    @Headers("Content-type: application/json")
    @GET("character/{id}")
    fun getCharacterById(@Path("id") id: Int): Call<CharacterResult>

    @Headers("Content-type: application/json")
    @GET("location/?")
    fun getLocationsResponse(@Query("page") page: Int): Call<LocationResponse>

    companion object{
        const val BASE_URL = "https://rickandmortyapi.com/api/"
    }
}