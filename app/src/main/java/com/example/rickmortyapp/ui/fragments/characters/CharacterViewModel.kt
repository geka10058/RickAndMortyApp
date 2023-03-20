package com.example.rickmortyapp.ui.fragments.characters

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.example.rickmortyapp.data.db.entities.CharacterEntity
import com.example.rickmortyapp.data.db.repositories.CharacterRepo
import com.example.rickmortyapp.data.models.characters_data_classes.CharacterResult
import com.example.rickmortyapp.data.retrofit_controllers.CharacterResponseRC

class CharacterViewModel(private val characterRepo: CharacterRepo) : ViewModel() {

    private val controller = CharacterResponseRC()
    val characterResponseLD = controller.characterResponseLiveData
    val charactersList = mutableListOf<CharacterResult>()
    val characterEntityLD: LiveData<List<CharacterEntity>> =
        characterRepo.characterFlow.asLiveData()

    fun getCharacterResponse(pageNumber: Int) {
        controller.getCharacterResponse(pageNumber)
    }

    fun addCharacterListToDB(characterList: List<CharacterResult>) {
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

    class CharacterVMFactory(private val characterRepo: CharacterRepo) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CharacterViewModel::class.java)) {
                return CharacterViewModel(characterRepo) as T
            }
            throw IllegalArgumentException("unknown VM class")
        }
    }
}