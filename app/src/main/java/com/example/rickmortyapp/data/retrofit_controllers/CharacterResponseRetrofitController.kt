package com.example.rickmortyapp.data.retrofit_controllers

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.rickmortyapp.api.ApiRickMorty
import com.example.rickmortyapp.data.models.characters_data_classes.CharacterResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class CharacterResponseRetrofitController: Callback<CharacterResponse> {

    val characterResponseLiveData = MutableLiveData<CharacterResponse>()

    fun getCharacterResponse(pageNumber: Int){

        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

        val retrofit = Retrofit.Builder()
            .baseUrl(ApiRickMorty.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(client)
            .build()

        val api: ApiRickMorty = retrofit.create(ApiRickMorty::class.java)
        val call: Call<CharacterResponse> = api.getCharacterResponse(pageNumber)
        call.enqueue(this)
    }

    override fun onResponse(call: Call<CharacterResponse>, response: Response<CharacterResponse>) {

        if (response.isSuccessful){
            characterResponseLiveData.postValue(response.body())
            Log.d("TAG", response.body().toString())
        } else println(response.errorBody())
    }

    override fun onFailure(call: Call<CharacterResponse>, t: Throwable) { t.printStackTrace() }
}