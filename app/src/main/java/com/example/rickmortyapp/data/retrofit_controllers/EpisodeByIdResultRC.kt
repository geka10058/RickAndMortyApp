package com.example.rickmortyapp.data.retrofit_controllers

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.rickmortyapp.Utils
import com.example.rickmortyapp.api.ApiRickMorty
import com.example.rickmortyapp.data.models.episodes_data_classes.EpisodeResult
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class EpisodeByIdResultRC: Callback<EpisodeResult> {

    val episodeByIdResultLiveData = MutableLiveData<EpisodeResult>()

    fun getEpisodeByIdResult(id: Int){
        val retrofit = Utils.retrofit
        val api: ApiRickMorty = retrofit.create(ApiRickMorty::class.java)
        val call: Call<EpisodeResult> = api.getEpisodeById(id)
        call.enqueue(this)
    }

    override fun onResponse(call: Call<EpisodeResult>, response: Response<EpisodeResult>) {
        if (response.isSuccessful){
            episodeByIdResultLiveData.postValue(response.body())
            Log.d("TAG", response.body().toString())
        } else println(response.errorBody())
    }

    override fun onFailure(call: Call<EpisodeResult>, t: Throwable) { t.printStackTrace() }
}