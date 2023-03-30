package com.example.rickmortyapp.data.retrofit_controllers

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.rickmortyapp.Utils
import com.example.rickmortyapp.api.ApiRickMorty
import com.example.rickmortyapp.data.models.locations_data_classes.LocationResult
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class LocationByIdResultRC: Callback<LocationResult> {

    val locationByIdResultLiveData = MutableLiveData<LocationResult>()

    fun getLocationByIdResult(id: Int){
        val retrofit = Utils.retrofit
        val api: ApiRickMorty = retrofit.create(ApiRickMorty::class.java)
        val call: Call<LocationResult> = api.getLocationById(id)
        call.enqueue(this)
    }

    override fun onResponse(call: Call<LocationResult>, response: Response<LocationResult>) {
        if (response.isSuccessful){
            locationByIdResultLiveData.postValue(response.body())
            Log.d("TAG", response.body().toString())
        } else println(response.errorBody())
    }

    override fun onFailure(call: Call<LocationResult>, t: Throwable) { t.printStackTrace() }
}