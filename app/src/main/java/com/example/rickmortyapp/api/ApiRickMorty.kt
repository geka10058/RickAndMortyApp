package com.example.rickmortyapp.api

import com.example.rickmortyapp.data.models.characters_data_classes.CharacterResponse
import com.example.rickmortyapp.data.models.characters_data_classes.CharacterResult
import com.example.rickmortyapp.data.models.episodes_data_classes.EpisodeResult
import com.example.rickmortyapp.data.models.episodes_data_classes.EpisodeResponse
import com.example.rickmortyapp.data.models.locations_data_classes.LocationResponse
import com.example.rickmortyapp.data.models.locations_data_classes.LocationResult
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface ApiRickMorty {

    @Headers("Content-type: application/json")
    @GET("character/?")
    fun getCharacterResponse(@Query("page") page: Int): Call<CharacterResponse>

    @Headers("Content-type: application/json")
    @GET("character/{id}")
    fun getCharacterById(@Path("id") id: Int): Call<CharacterResult>

    @Headers("Content-type: application/json")
    @GET("character/{idList}")
    fun getCharacterListForEpisode(@Path("idList") idList: String): Call<List<CharacterResult>>

    /*@Headers("Content-type: application/json")
    @GET("character/}")
    fun getCharacterListWithQuery(
    @Query("name") name: String,
    @Query("species") species: String,
    @Query("status") status: String,
    @Query("type") type: String,
    @Query("gender") gender: String,
    ): Call<CharacterResponse>*/

    @Headers("Content-type: application/json")
    @GET("character/")
    fun getCharacterListWithParameters(@QueryMap string: Map<String,String>): Call<CharacterResponse>

    @Headers("Content-type: application/json")
    @GET("location/?")
    fun getLocationsResponse(@Query("page") page: Int): Call<LocationResponse>

    @Headers("Content-type: application/json")
    @GET("location/{id}")
    fun getLocationById(@Path("id") id: Int): Call<LocationResult>

    @Headers("Content-type: application/json")
    @GET("episode/?")
    fun getEpisodesResponse(@Query("page") page: Int): Call<EpisodeResponse>

    @Headers("Content-type: application/json")
    @GET("episode/{id}")
    fun getEpisodeById(@Path("id") id: Int): Call<EpisodeResult>

    companion object{
        const val BASE_URL = "https://rickandmortyapi.com/api/"
    }
}