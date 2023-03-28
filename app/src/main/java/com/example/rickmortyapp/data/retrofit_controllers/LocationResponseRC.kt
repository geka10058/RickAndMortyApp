package com.example.rickmortyapp.data.retrofit_controllers

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.rickmortyapp.api.ApiRickMorty
import com.example.rickmortyapp.data.json_models.locations_data_classes.LocationResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class LocationResponseRC: Callback<LocationResponse> {

    val locationResponseLiveData = MutableLiveData<LocationResponse>()

    fun getLocationResponse(pageNumber: Int){
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

        val retrofit = Retrofit.Builder()
            .baseUrl(ApiRickMorty.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(client)
            .build()

        val api: ApiRickMorty = retrofit.create(ApiRickMorty::class.java)
        val call: Call<LocationResponse> = api.getLocationsResponse(pageNumber)
        call.enqueue(this)
    }

    override fun onResponse(call: Call<LocationResponse>, response: Response<LocationResponse>) {
        if (response.isSuccessful){
            locationResponseLiveData.postValue(response.body())
            Log.d("TAG", response.body().toString())
        } else println(response.errorBody())
    }

    override fun onFailure(call: Call<LocationResponse>, t: Throwable) { t.printStackTrace() }
}