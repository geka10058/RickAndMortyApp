package com.example.rickmortyapp.ui.fragments.locations

import androidx.lifecycle.*
import com.example.rickmortyapp.data.db.entities.LocationEntity
import com.example.rickmortyapp.data.db.repositories.LocationRepo
import com.example.rickmortyapp.data.models.locations_data_classes.LocationResult
import com.example.rickmortyapp.data.retrofit_controllers.LocationResponseRC

class LocationsViewModel(private val locationRepo: LocationRepo) : ViewModel() {

    private val controller = LocationResponseRC()
    val locationResponseLD = controller.locationResponseLiveData
    val locationEntityLD = locationRepo.locationFlow.asLiveData()
    val locationLD = MutableLiveData<List<LocationResult>>()
    val locationList = mutableListOf<LocationResult>()
    val locationResponse = mutableListOf<LocationResult>()
    val locationEntity = mutableListOf<LocationResult>()

    var locationCounterPage = 0
    var restoredItemPosition = 1
    private val numberOfItemsInResponse = 20

    fun getLocationResponse(pageNumber: Int) {
        controller.getLocationResponse(pageNumber)
    }

    fun addLocationListToDB(locationList: List<LocationResult>) {
        val list = convertResultToEntity(locationList)
        locationRepo.insertLocationList(list)
    }

    private fun convertResultToEntity(locationList: List<LocationResult>): List<LocationEntity> {
        val locationEntityList = mutableListOf<LocationEntity>()
        for (location in locationList) {
            val locationEntity =
                LocationEntity(
                    location.id,
                    location.name,
                    location.type,
                    location.dimension,
                    location.residents.toString()
                )
            locationEntityList.add(locationEntity)
        }
        return locationEntityList
    }

    fun convertEntityToResult(locationEntityList: List<LocationEntity>): List<LocationResult> {
        val locationList = mutableListOf<LocationResult>()
        for (location in locationEntityList) {
            val locationResult =
                LocationResult(
                    location.id,
                    location.name,
                    location.type,
                    location.dimension,
                    location.residents?.split(",") ?: emptyList<String>()
                )
            locationList.add(locationResult)
        }
        return locationList
    }

    fun selectDataSource(checkConnection: Boolean) {
        if (checkConnection) {
            locationLD.value = locationResponse
        } else {
            locationLD.value = checkLocationListIsContainsList(locationEntity)
            locationCounterPage = locationEntity.size / numberOfItemsInResponse
        }
    }

    fun checkLocationListIsContainsData(list: List<LocationResult>) {
        if (!locationList.containsAll(list)) {
            locationList.addAll(list)
            addLocationListToDB(list)
        }
    }

    private fun checkLocationListIsContainsList(locationEntityList: List<LocationResult>): List<LocationResult> {
        var newList = locationEntityList
        if (locationEntityList.containsAll(locationList)) newList =
            locationEntityList - locationList.toSet()
        return newList
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