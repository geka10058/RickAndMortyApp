package com.example.rickmortyapp.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.rickmortyapp.data.db.entities.CharacterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CharacterDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertCharacterList(characterList: List<CharacterEntity>)

    @Query("SELECT * FROM character_table ORDER BY id ASC")
    fun getAllCharacters(): Flow<List<CharacterEntity>>

    @Query("SELECT * FROM character_table WHERE id=:id")
    fun getCharacterById(id:Int): CharacterEntity
}