package com.example.rickmortyapp.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "episode_table")
data class EpisodeEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "air_date") val airDate: String?,
    @ColumnInfo(name = "episode") val episode: String?,
    //@ColumnInfo(name = "characters") val characters: List<String>?,
)
