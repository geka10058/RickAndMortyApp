package com.example.rickmortyapp

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatActivity
import com.example.rickmortyapp.api.ApiRickMorty
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object Utils {

    fun checkInternetConnection(activity: AppCompatActivity): Boolean {
        val connectivityManager =
            activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    }

    const val BUNDLE_FLAG_CHARACTER = "bundle_flag_character_id"
    const val BUNDLE_FLAG_EPISODE = "bundle_flag_episode_id"
    const val BUNDLE_FLAG_LOCATION = "bundle_flag_location_id"
    const val NAME = "name"
    const val SPECIES = "species"
    const val STATUS = "status"
    const val ORIGIN = "origin"
    const val GENDER = "gender"
    const val TYPE = "type"
    const val DIMENSION = "dimension"

    private fun getRetrofitInstance() : Retrofit {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

        val retrofit = Retrofit.Builder()
            .baseUrl(ApiRickMorty.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(client)
            .build()
        return retrofit
    }

    val retrofit = getRetrofitInstance()
}