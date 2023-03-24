package com.example.rickmortyapp.ui.fragments.characters

import android.util.Log
import androidx.lifecycle.*
import com.example.rickmortyapp.data.db.entities.CharacterEntity
import com.example.rickmortyapp.data.db.repositories.CharacterRepo
import com.example.rickmortyapp.data.models.characters_data_classes.CharacterResult
import com.example.rickmortyapp.data.retrofit_controllers.CharacterResponseRC

class CharacterViewModel(private val characterRepo: CharacterRepo) : ViewModel() {

    private val controller = CharacterResponseRC()
    val characterResponseLD = controller.characterResponseLiveData
    val characterEntityLD = characterRepo.characterFlow.asLiveData()
    val characterLD = MutableLiveData<List<CharacterResult>>()
    val charactersList = mutableListOf<CharacterResult>()
    val characterResponse = mutableListOf<CharacterResult>()
    val characterEntity = mutableListOf<CharacterResult>()

    var entityCounterPages = 0
    private val numberOfItemsInResponse = 20

    fun getCharacterResponse(pageNumber: Int) {
        controller.getCharacterResponse(pageNumber)
    }

    private fun addCharacterListToDB(characterList: List<CharacterResult>) {
        val list = convertResultToEntity(characterList)
        characterRepo.insertCharacterList(list)
    }

    private fun convertResultToEntity(characterList: List<CharacterResult>): List<CharacterEntity> {
        val characterEntityList = mutableListOf<CharacterEntity>()
        for (character in characterList) {
            val characterEntity =
                CharacterEntity(
                    character.gender,
                    character.id,
                    character.image,
                    character.name,
                    character.species,
                    character.status
                )
            characterEntityList.add(characterEntity)
        }
        return characterEntityList
    }

    fun convertEntityToResult(characterEntityList: List<CharacterEntity>):List<CharacterResult> {
        val characterList = mutableListOf<CharacterResult>()
        for (character in characterEntityList) {
            val characterResult =
                CharacterResult(
                    character.gender,
                    character.id,
                    character.image,
                    character.name,
                    character.species,
                    character.status
                )
            characterList.add(characterResult)
        }
        return characterList
    }

    fun selectDataSource(checkConnection: Boolean){
        if (checkConnection) {
            Log.d("TAG", "True checkInternetConnection")
            characterLD.value = characterResponse
        } else {
            Log.d("TAG", "False checkInternetConnection")
            characterLD.value = checkCharacterEntityIsContainsList(characterEntity)
            entityCounterPages = characterEntity.size / numberOfItemsInResponse
        }
    }

    fun checkCharacterListIsContainsData(list:List<CharacterResult>){
        if (charactersList.containsAll(list)) {
            Log.d("TAG", "charactersList.containsAll!!")
        } else {
            Log.d("TAG", "charactersList added!!")
            charactersList.addAll(list)
            addCharacterListToDB(list)
        }
    }

    private fun checkCharacterEntityIsContainsList(characterEntity: List<CharacterResult>):List<CharacterResult>{
        var newList = characterEntity
        if (characterEntity.containsAll(charactersList)) newList = characterEntity - charactersList.toSet()
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