package com.example.rickmortyapp.ui.fragments.locations.details

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rickmortyapp.data.db.entities.LocationEntity
import com.example.rickmortyapp.data.db.repositories.LocationRepo
import com.example.rickmortyapp.data.json_models.locations_data_classes.LocationResult
import com.example.rickmortyapp.data.retrofit_controllers.CharacterListForEpisodeRC
import com.example.rickmortyapp.data.retrofit_controllers.LocationByIdResultRC

class LocationDetailsViewModel(private val locationRepo: LocationRepo): ViewModel() {

    private val controllerForLocationResponse = LocationByIdResultRC()
    private val controllerForCharacterList = CharacterListForEpisodeRC()
    val residentsListForEpisodeLD = controllerForCharacterList.characterListForEpisodeLiveData
    val locationDetailsResultLD = controllerForLocationResponse.locationByIdResultLiveData
    val locationEntityDetailsLD = locationRepo.locationByIdLiveData
    val locationDetailsLiveData = MutableLiveData<LocationResult>()

    fun getLocationDetails(characterID: Int, checkConnection:Boolean) {
        if (checkConnection){
            controllerForLocationResponse.getLocationByIdResult(characterID)
        } else {
            locationRepo.getLocationById(characterID)
        }
    }

    fun getCharacterListForEpisode(idList: String){
        controllerForCharacterList.getCharacterListForEpisode(idList)
    }

    fun convertEntityToResult(locationEntity: LocationEntity): LocationResult {
        return LocationResult(
            locationEntity.id,
            locationEntity.name,
            locationEntity.type,
            locationEntity.dimension,
            locationEntity.residents?.split(",") ?: listOf("Unknown")
        )
    }

    fun convertLocationListToIdList(locationList: List<String>): String {
        val idList = mutableListOf<String>()
        for (location in locationList) {
            val elementList = location.split("/")
            val id = elementList.last()
            idList.add(id)
        }
        return idList.toString()
    }
}

class LocationDetailsVMFactory(private val locationRepo: LocationRepo) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LocationDetailsViewModel::class.java)) {
            return LocationDetailsViewModel(locationRepo) as T
        }
        throw IllegalArgumentException("unknown VM class")
    }
}