package com.example.rickmortyapp.ui.fragments.episodes

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.example.rickmortyapp.data.db.entities.EpisodeEntity
import com.example.rickmortyapp.data.db.repositories.EpisodeRepo
import com.example.rickmortyapp.data.models.episodes_data_classes.EpisodeResult
import com.example.rickmortyapp.data.retrofit_controllers.EpisodeResponseRC

class EpisodeViewModel(private val episodeRepo: EpisodeRepo): ViewModel(){

    private val controller = EpisodeResponseRC()
    val episodeResponseLD = controller.episodeResponseLiveData
    val episodeList = mutableListOf<EpisodeResult>()
    val episodeEntityLD: LiveData<List<EpisodeEntity>> = episodeRepo.episodeFlow.asLiveData()

    fun getEpisodeResponse(pageNumber: Int){
        controller.getEpisodeResponse(pageNumber)
    }

    fun addEpisodesToDB(episodeList: List<EpisodeResult>){
        val list = convertResultToEntity(episodeList)
        episodeRepo.insertEpisodeList(list)
    }

    private fun convertResultToEntity(episodeList: List<EpisodeResult>):List<EpisodeEntity>{
        val episodeEntityList = mutableListOf<EpisodeEntity>()
        for (episode in episodeList) {
            val episodeEntity = EpisodeEntity(episode.id,episode.name,episode.airDate,episode.episode)
            episodeEntityList.add(episodeEntity)
        }
        return episodeEntityList
    }

    class EpisodeVMFactory(private val episodeRepo: EpisodeRepo) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(EpisodeViewModel::class.java)) {
                return EpisodeViewModel(episodeRepo) as T
            }
            throw IllegalArgumentException("unknown VM class")
        }
    }
}