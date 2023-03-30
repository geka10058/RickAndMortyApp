package com.example.rickmortyapp.data.retrofit_controllers

import androidx.lifecycle.MutableLiveData
import com.example.rickmortyapp.Utils
import com.example.rickmortyapp.api.ApiRickMorty
import com.example.rickmortyapp.data.models.episodes_data_classes.EpisodeResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EpisodeResponseRC: Callback<EpisodeResponse> {

    val episodeResponseLiveData = MutableLiveData<EpisodeResponse>()

    fun getEpisodeResponse(pageNumber: Int){
        val retrofit = Utils.retrofit
        val api: ApiRickMorty = retrofit.create(ApiRickMorty::class.java)
        val call: Call<EpisodeResponse> = api.getEpisodesResponse(pageNumber)
        call.enqueue(this)
    }

    override fun onResponse(call: Call<EpisodeResponse>, response: Response<EpisodeResponse>) {
        if (response.isSuccessful){
            episodeResponseLiveData.postValue(response.body())
        } else println(response.errorBody())
    }

    override fun onFailure(call: Call<EpisodeResponse>, t: Throwable) { t.printStackTrace() }
}