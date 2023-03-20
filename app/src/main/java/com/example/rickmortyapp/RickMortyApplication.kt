package com.example.rickmortyapp

import android.app.Application
import com.example.rickmortyapp.data.db.AppDatabase
import com.example.rickmortyapp.data.db.repositories.CharacterRepo
import com.example.rickmortyapp.data.db.repositories.EpisodeRepo
import com.example.rickmortyapp.data.db.repositories.LocationRepo

class RickMortyApplication:Application() {
    private val database by lazy { AppDatabase.getDatabase(this) }
    val characterRepo by lazy { CharacterRepo(database.characterDao()) }
    val episodeRepo by lazy { EpisodeRepo(database.episodeDao()) }
    val locationRepo by lazy { LocationRepo(database.locationDao()) }
}