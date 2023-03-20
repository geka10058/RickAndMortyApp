package com.example.rickmortyapp.ui.fragments.locations

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.example.rickmortyapp.data.db.entities.LocationEntity
import com.example.rickmortyapp.data.db.repositories.LocationRepo
import com.example.rickmortyapp.data.models.locations_data_classes.LocationResult
import com.example.rickmortyapp.data.retrofit_controllers.LocationResponseRC

class LocationsViewModel(private val locationRepo: LocationRepo) : ViewModel() {

    private val controller = LocationResponseRC()
    val locationResponseLD = controller.locationResponseLiveData
    val locationList = mutableListOf<LocationResult>()
    val locationEntityLD: LiveData<List<LocationEntity>> = locationRepo.locationFlow.asLiveData()

    fun getLocationResponse(pageNumber: Int) {
        controller.getLocationResponse(pageNumber)
    }

    fun addLocationToDB(locationList: List<LocationResult>) {
        val list = convertResultToEntity(locationList)
        locationRepo.insertLocationList(list)
    }

    private fun convertResultToEntity(locationList: List<LocationResult>): List<LocationEntity> {
        val locationEntityList = mutableListOf<LocationEntity>()
        for (location in locationList) {
            val locationEntity =
                LocationEntity(location.id, location.name, location.type, location.dimension)
            locationEntityList.add(locationEntity)
        }
        return locationEntityList
    }

    class LocationVMFactory(private val locationRepo: LocationRepo) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LocationsViewModel::class.java)) {
                return LocationsViewModel(locationRepo) as T
            }
            throw IllegalArgumentException("unknown VM class")
        }
    }
}