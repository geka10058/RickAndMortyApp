package com.example.rickmortyapp.data.retrofit_controllers

import androidx.lifecycle.MutableLiveData
import com.example.rickmortyapp.Utils
import com.example.rickmortyapp.api.ApiRickMorty
import com.example.rickmortyapp.data.models.locations_data_classes.LocationResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LocationResponseRC: Callback<LocationResponse> {

    val locationResponseLiveData = MutableLiveData<LocationResponse>()

    fun getLocationResponse(pageNumber: Int){
        val retrofit = Utils.retrofit
        val api: ApiRickMorty = retrofit.create(ApiRickMorty::class.java)
        val call: Call<LocationResponse> = api.getLocationsResponse(pageNumber)
        call.enqueue(this)
    }

    override fun onResponse(call: Call<LocationResponse>, response: Response<LocationResponse>) {
        if (response.isSuccessful){
            locationResponseLiveData.postValue(response.body())
        } else println(response.errorBody())
    }

    override fun onFailure(call: Call<LocationResponse>, t: Throwable) { t.printStackTrace() }
}