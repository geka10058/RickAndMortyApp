package com.example.rickmortyapp.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.rickmortyapp.data.db.entities.EpisodeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EpisodeDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertEpisodeList(characterList: List<EpisodeEntity>)

    @Query("SELECT * FROM episode_table ORDER BY id ASC")
    fun getAllEpisodes(): Flow<List<EpisodeEntity>>
}