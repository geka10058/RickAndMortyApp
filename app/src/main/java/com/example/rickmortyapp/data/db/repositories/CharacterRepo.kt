package com.example.rickmortyapp.data.db.repositories

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

    fun insertCharacterList(characterList: List<CharacterEntity>) =
        scope.launch { characterDao.insertCharacterList(characterList) }

    fun onDestroyCoroutineScope() {
        scope.cancel()
    }
}