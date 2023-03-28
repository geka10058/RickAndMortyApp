package com.example.rickmortyapp.ui.fragments.characters.details

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rickmortyapp.data.db.entities.CharacterEntity
import com.example.rickmortyapp.data.db.repositories.CharacterRepo
import com.example.rickmortyapp.data.json_models.characters_data_classes.CharacterResult
import com.example.rickmortyapp.data.json_models.characters_data_classes.Location
import com.example.rickmortyapp.data.json_models.characters_data_classes.Origin
import com.example.rickmortyapp.data.retrofit_controllers.CharacterByIdResultRC

class CharacterDetailsViewModel(private val characterRepo: CharacterRepo): ViewModel() {

    private val controller = CharacterByIdResultRC()
    val characterDetailsResultLD = controller.characterByIdResultLiveData
    val characterEntityDetailsLD = characterRepo.characterByIdLiveData
    val characterDetailsLiveData = MutableLiveData<CharacterResult>()

    fun getCharactersDetails(characterID: Int, checkConnection:Boolean) {
        if (checkConnection){
            controller.getCharacterByIdResult(characterID)
            Log.d("TAG", "Выбран запрос")
        } else {
            characterRepo.getCharacterById(characterID)
            Log.d("TAG", "Выбрана БД")
        }
    }

    fun convertEntityToResult(characterEntity: CharacterEntity): CharacterResult {
        return CharacterResult(
            characterEntity.id,
            characterEntity.name,
            characterEntity.status,
            characterEntity.species,
            characterEntity.gender,
            Origin(characterEntity.origin, ""),
            Location(characterEntity.location, ""),
            characterEntity.image
        )
    }
}

class CharacterDetailsVMFactory(private val characterRepo: CharacterRepo) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CharacterDetailsViewModel::class.java)) {
            return CharacterDetailsViewModel(characterRepo) as T
        }
        throw IllegalArgumentException("unknown VM class")
    }
}