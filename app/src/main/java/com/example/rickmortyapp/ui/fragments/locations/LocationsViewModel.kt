package com.example.rickmortyapp.ui.fragments.locations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rickmortyapp.data.models.locations_data_classes.LocationResult
import com.example.rickmortyapp.data.retrofit_controllers.LocationResponseRC

class LocationsViewModel: ViewModel() {

    private val controller = LocationResponseRC()
    val locationResponseLD = controller.locationResponseLiveData
    val locationList = mutableListOf<LocationResult>()

    fun getLocationResponse(pageNumber: Int){
        controller.getLocationResponse(pageNumber)
    }

    class LocationVMFactory() : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LocationsViewModel::class.java)) {
                return LocationsViewModel() as T
            }
            throw IllegalArgumentException("unknown VM class")
        }
    }
}