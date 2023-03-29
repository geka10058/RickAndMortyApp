package com.example.rickmortyapp.data.db.repositories

import androidx.lifecycle.MutableLiveData
import com.example.rickmortyapp.data.db.dao.CharacterDao
import com.example.rickmortyapp.data.db.entities.CharacterEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class CharacterRepo(private val characterDao: CharacterDao) {

    private val scope = CoroutineScope((Dispatchers.IO))
    val characterFlow: Flow<List<CharacterEntity>> = characterDao.getAllCharacters()
    val characterByIdLiveData = MutableLiveData<CharacterEntity>()
    val characterWithParametersLiveData = MutableLiveData<List<CharacterEntity>>()

    fun insertCharacterList(characterList: List<CharacterEntity>) =
        scope.launch { characterDao.insertCharacterList(characterList) }

    fun getCharacterById(id: Int) {
        scope.launch { characterByIdLiveData.postValue(characterDao.getCharacterById(id)) }
    }

    fun getCharacterWithParameters(
        name: String, status: String, species: String, gender: String, origin: String
    ) {
        scope.launch {
            characterWithParametersLiveData.postValue(
                characterDao.getCharacterWithParameters(name,status,species,gender,origin)
            )
        }
    }

    fun onDestroyCoroutineScope() {
        scope.cancel()
    }
}