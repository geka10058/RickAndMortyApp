package com.example.rickmortyapp.ui.fragments.characters

import android.util.Log
import androidx.lifecycle.*
import com.example.rickmortyapp.Utils
import com.example.rickmortyapp.data.db.entities.CharacterEntity
import com.example.rickmortyapp.data.db.repositories.CharacterRepo
import com.example.rickmortyapp.data.models.Parameter
import com.example.rickmortyapp.data.models.characters_data_classes.CharacterResult
import com.example.rickmortyapp.data.models.characters_data_classes.Location
import com.example.rickmortyapp.data.models.characters_data_classes.Origin
import com.example.rickmortyapp.data.retrofit_controllers.CharacterWithParametersRC
import com.example.rickmortyapp.data.retrofit_controllers.CharacterResponseRC

class CharacterViewModel(private val characterRepo: CharacterRepo) : ViewModel() {

    private val controllerResponse = CharacterResponseRC()
    private val controllerForSearchWithParameters = CharacterWithParametersRC()
    val characterResponseForSearchWithParametersLD = controllerForSearchWithParameters.characterListWithParametersLiveData
    val characterEntitiesForSearchWithParametersLD = characterRepo.characterWithParametersLiveData
    val characterResponseLD = controllerResponse.characterResponseLiveData
    val characterEntityLD = characterRepo.characterFlow.asLiveData()
    val characterLD = MutableLiveData<List<CharacterResult>>()
    val charactersList = mutableListOf<CharacterResult>()
    val characterResponse = mutableListOf<CharacterResult>()
    val characterEntity = mutableListOf<CharacterResult>()

    var entityCounterPages = 0
    var restoredItemPosition = 1
    private val numberOfItemsInResponse = 20

    fun getCharacterResponse(pageNumber: Int) {
        controllerResponse.getCharacterResponse(pageNumber)
    }

    private fun addCharacterListToDB(characterList: List<CharacterResult>) {
        val list = convertResultToEntity(characterList)
        characterRepo.insertCharacterList(list)
    }

    fun getSearchWithParameters(parameters: List<Parameter>, checkConnection: Boolean){
        val params = convertParametersToMap(parameters)
        if (checkConnection){
            controllerForSearchWithParameters.getCharacterListWithParameters(params)
        } else {
            characterRepo.getCharacterWithParameters(
                "%${params[Utils.NAME]!!}%",
                "%${params[Utils.STATUS]!!}%",
                "%${params[Utils.SPECIES]!!}%",
                    "%${params[Utils.GENDER]!!}%",
                        "%${params[Utils.ORIGIN]!!}%",
                )
        }

    }

    private fun convertParametersToMap(parameters: List<Parameter>):Map<String,String>{
        val map = mutableMapOf<String,String>()//
        for (param in parameters){
            when(param.name){
                Utils.NAME -> { map.put(param.name,param.value) }
                Utils.STATUS -> { map.put(param.name,param.value) }
                Utils.SPECIES -> { map.put(param.name,param.value) }
                Utils.GENDER -> { map.put(param.name,param.value) }
                Utils.ORIGIN -> { map.put(param.name,param.value) }
            }
        }
        return map
    }

    private fun convertResultToEntity(characterList: List<CharacterResult>): List<CharacterEntity> {
        val characterEntityList = mutableListOf<CharacterEntity>()
        for (character in characterList) {
            val characterEntity =
                CharacterEntity(
                    character.id,
                    character.name,
                    character.status,
                    character.species,
                    character.gender,
                    character.origin?.name ?: "unknown",
                    character.location?.name ?: "unknown",
                    character.image
                )
            characterEntityList.add(characterEntity)
        }
        return characterEntityList
    }

    fun convertEntityToResult(characterEntityList: List<CharacterEntity>): List<CharacterResult> {
        val characterList = mutableListOf<CharacterResult>()
        for (character in characterEntityList) {
            val characterResult =
                CharacterResult(
                    character.id,
                    character.name,
                    character.status,
                    character.species,
                    character.gender,
                    Origin(character.origin, ""),
                    Location(character.location, ""),
                    character.image
                )
            characterList.add(characterResult)
        }
        return characterList
    }

    fun selectDataSource(checkConnection: Boolean) {
        if (checkConnection) {
            characterLD.value = characterResponse
        } else {
            characterLD.value = checkCharacterEntityIsContainsList(characterEntity)
            entityCounterPages = characterEntity.size / numberOfItemsInResponse
        }
    }

    fun checkCharacterListIsContainsData(list: List<CharacterResult>) {
        if (!charactersList.containsAll(list)) {
            charactersList.addAll(list)
            addCharacterListToDB(list)
        }
    }

    private fun checkCharacterEntityIsContainsList(characterEntitiesList: List<CharacterResult>): List<CharacterResult> {
        var newList = characterEntitiesList
        if (characterEntitiesList.containsAll(charactersList)) newList =
            characterEntitiesList - charactersList.toSet()
        return newList
    }

    class CharacterVMFactory(private val characterRepo: CharacterRepo) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CharacterViewModel::class.java)) {
                return CharacterViewModel(characterRepo) as T
            }
            throw IllegalArgumentException("unknown VM class")
        }
    }
}