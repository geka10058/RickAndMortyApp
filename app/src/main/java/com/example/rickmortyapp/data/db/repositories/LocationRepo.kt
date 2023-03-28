package com.example.rickmortyapp.data.db.repositories

import androidx.lifecycle.MutableLiveData
import com.example.rickmortyapp.data.db.dao.LocationDao
import com.example.rickmortyapp.data.db.entities.EpisodeEntity
import com.example.rickmortyapp.data.db.entities.LocationEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class LocationRepo(private val locationDao: LocationDao) {

    private val scope = CoroutineScope((Dispatchers.IO))
    val locationFlow: Flow<List<LocationEntity>> = locationDao.getAllLocations()
    val locationByIdLiveData =  MutableLiveData<LocationEntity>()

    fun insertLocationList(locationList: List<LocationEntity>) =
        scope.launch { locationDao.insertLocationList(locationList) }

    fun getLocationById(id:Int){
        scope.launch {locationByIdLiveData.postValue(locationDao.getLocationByID(id)) }
    }

    fun onDestroyCoroutineScope() {
        scope.cancel()
    }
}