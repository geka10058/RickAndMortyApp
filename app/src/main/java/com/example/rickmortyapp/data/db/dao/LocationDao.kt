package com.example.rickmortyapp.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.rickmortyapp.data.db.entities.LocationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertLocationList(characterList: List<LocationEntity>)

    @Query("SELECT * FROM location_table ORDER BY id ASC")
    fun getAllLocations(): Flow<List<LocationEntity>>
}