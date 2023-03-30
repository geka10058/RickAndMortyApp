package com.example.rickmortyapp.data.retrofit_controllers

import androidx.lifecycle.MutableLiveData
import com.example.rickmortyapp.Utils
import com.example.rickmortyapp.api.ApiRickMorty
import com.example.rickmortyapp.data.models.characters_data_classes.CharacterResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CharacterResponseRC: Callback<CharacterResponse> {

    val characterResponseLiveData = MutableLiveData<CharacterResponse>()

    fun getCharacterResponse(pageNumber: Int){
        val retrofit = Utils.retrofit
        val api: ApiRickMorty = retrofit.create(ApiRickMorty::class.java)
        val call: Call<CharacterResponse> = api.getCharacterResponse(pageNumber)
        call.enqueue(this)
    }

    override fun onResponse(call: Call<CharacterResponse>, response: Response<CharacterResponse>) {

        if (response.isSuccessful){
            characterResponseLiveData.postValue(response.body())
        } else println(response.errorBody())
    }

    override fun onFailure(call: Call<CharacterResponse>, t: Throwable) { t.printStackTrace() }
}