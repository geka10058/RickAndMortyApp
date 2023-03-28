package com.example.rickmortyapp.data.db.repositories

import androidx.lifecycle.MutableLiveData
import com.example.rickmortyapp.data.db.dao.EpisodeDao
import com.example.rickmortyapp.data.db.entities.EpisodeEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class EpisodeRepo(private val episodeDao: EpisodeDao) {

    private val scope = CoroutineScope((Dispatchers.IO))
    val episodeFlow: Flow<List<EpisodeEntity>> = episodeDao.getAllEpisodes()
    val episodeByIdLiveData =  MutableLiveData<EpisodeEntity>()

    fun insertEpisodeList(episodeList: List<EpisodeEntity>) =
        scope.launch { episodeDao.insertEpisodeList(episodeList) }

    fun getEpisodeById(id: Int) {
        scope.launch { episodeByIdLiveData.postValue(episodeDao.getEpisodeById(id)) }
    }

    fun onDestroyCoroutineScope() {
        scope.cancel()
    }
}