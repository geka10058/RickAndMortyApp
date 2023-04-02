package com.example.rickmortyapp.data.retrofit_controllers

import androidx.lifecycle.MutableLiveData
import com.example.rickmortyapp.Utils
import com.example.rickmortyapp.api.ApiRickMorty
import com.example.rickmortyapp.data.models.characters_data_classes.CharacterResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CharacterByIdResultRC: Callback<CharacterResult> {

    val characterByIdResultLiveData = MutableLiveData<CharacterResult>()

    fun getCharacterByIdResult(id: Int){
        val retrofit = Utils.retrofit
        val api: ApiRickMorty = retrofit.create(ApiRickMorty::class.java)
        val call: Call<CharacterResult> = api.getCharacterById(id)
        call.enqueue(this)
    }

    override fun onResponse(call: Call<CharacterResult>, response: Response<CharacterResult>) {
        if (response.isSuccessful){
            characterByIdResultLiveData.postValue(response.body())
        } else println(response.errorBody())
    }

    override fun onFailure(call: Call<CharacterResult>, t: Throwable) { t.printStackTrace() }
}