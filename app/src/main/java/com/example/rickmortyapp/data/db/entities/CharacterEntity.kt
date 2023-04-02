package com.example.rickmortyapp.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "character_table")
data class CharacterEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "status") val status: String?,
    @ColumnInfo(name = "species") val species: String?,
    @ColumnInfo(name = "gender") val gender: String?,
    @ColumnInfo(name = "origin") val origin: String?,
    @ColumnInfo(name = "location") val location: String?,
    @ColumnInfo(name = "image") val image: String?,
)
