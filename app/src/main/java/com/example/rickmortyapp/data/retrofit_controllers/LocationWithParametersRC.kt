package com.example.rickmortyapp.data.retrofit_controllers

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.rickmortyapp.Utils
import com.example.rickmortyapp.api.ApiRickMorty
import com.example.rickmortyapp.data.models.Info
import com.example.rickmortyapp.data.models.locations_data_classes.LocationResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LocationWithParametersRC : Callback<LocationResponse> {

    val locationListWithParametersLiveData = MutableLiveData<LocationResponse>()

    fun getLocationListWithParameters(stringMap: Map<String, String>) {

        val retrofit = Utils.retrofit
        val api: ApiRickMorty = retrofit.create(ApiRickMorty::class.java)
        val call: Call<LocationResponse> = api.getLocationListWithParameters(stringMap)
        call.enqueue(this)
    }

    override fun onResponse(call: Call<LocationResponse>, response: Response<LocationResponse>) {
        if (response.isSuccessful) {
            locationListWithParametersLiveData.postValue(response.body())
            Log.d("TAG", response.body().toString())
        } else {
            locationListWithParametersLiveData.postValue(
                LocationResponse(
                    Info(null, null, null, null), emptyList()
                )
            )
            println(response.errorBody())
        }
    }

    override fun onFailure(call: Call<LocationResponse>, t: Throwable) {
        t.printStackTrace()
    }
}