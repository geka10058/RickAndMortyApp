package com.example.rickmortyapp

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatActivity

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

    const val CHARACTER_FIRST_ITEM_POSITION = "CFIP"
    const val LOCATION_FIRST_ITEM_POSITION = "LFIP"
    const val EPISODE_FIRST_ITEM_POSITION = "EFIP"
    const val BUNDLE_FLAG_CHARACTER = "bundle_flag_character_id"
}