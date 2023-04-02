package com.example.rickmortyapp.ui.fragments.episodes

import androidx.lifecycle.*
import com.example.rickmortyapp.Utils
import com.example.rickmortyapp.data.db.entities.EpisodeEntity
import com.example.rickmortyapp.data.db.repositories.EpisodeRepo
import com.example.rickmortyapp.data.models.Parameter
import com.example.rickmortyapp.data.models.episodes_data_classes.EpisodeResult
import com.example.rickmortyapp.data.retrofit_controllers.EpisodeResponseRC
import com.example.rickmortyapp.data.retrofit_controllers.EpisodeWithParametersRC

class EpisodeViewModel(private val episodeRepo: EpisodeRepo) : ViewModel() {

    private val controller = EpisodeResponseRC()
    private val controllerForSearchWithParameters = EpisodeWithParametersRC()
    val episodeResponseForSearchWithParametersLD = controllerForSearchWithParameters.episodeListWithParametersLiveData
    val episodeEntitiesForSearchWithParametersLD = episodeRepo.episodeWithParametersLiveData
    val episodeResponseLD = controller.episodeResponseLiveData
    val episodeEntityLD = episodeRepo.episodeFlow.asLiveData()
    val episodeLD = MutableLiveData<List<EpisodeResult>>()
    val episodeList = mutableListOf<EpisodeResult>()
    val episodeResponse = mutableListOf<EpisodeResult>()
    val episodeEntity = mutableListOf<EpisodeResult>()

    var entityCounterPages = 0
    var restoredItemPosition = 1
    private val numberOfItemsInResponse = 20

    fun getEpisodeResponse(pageNumber: Int) {
        controller.getEpisodeResponse(pageNumber)
    }

    fun addEpisodeListToDB(episodeList: List<EpisodeResult>) {
        val list = convertResultToEntity(episodeList)
        episodeRepo.insertEpisodeList(list)
    }

    fun getSearchWithParameters(parameters: List<Parameter>, checkConnection: Boolean){
        val params = convertParametersToMap(parameters)
        if (checkConnection){
            controllerForSearchWithParameters.getEpisodeListWithParameters(params)
        } else {
            episodeRepo.getEpisodeWithParameters(
                "%${params[Utils.NAME]!!}%",
                "%${params[Utils.EPISODE]!!}%"
            )
        }
    }

    private fun convertParametersToMap(parameters: List<Parameter>):Map<String,String>{
        val map = mutableMapOf<String,String>()//
        for (param in parameters){
            when(param.name){
                Utils.NAME -> { map.put(param.name,param.value) }
                Utils.EPISODE -> { map.put(param.name,param.value) }
            }
        }
        return map
    }

    private fun convertResultToEntity(episodeList: List<EpisodeResult>): List<EpisodeEntity> {
        val episodeEntityList = mutableListOf<EpisodeEntity>()
        for (episode in episodeList) {
            val episodeEntity =
                EpisodeEntity(episode.id, episode.name, episode.airDate, episode.episode, episode.characters.toString())
            episodeEntityList.add(episodeEntity)
        }
        return episodeEntityList
    }

    fun convertEntityToResult(episodeEntityList: List<EpisodeEntity>): List<EpisodeResult> {
        val episodeList = mutableListOf<EpisodeResult>()
        for (episode in episodeEntityList) {
            val episodeResult = EpisodeResult(
                episode.id,
                episode.name,
                episode.airDate,
                episode.episode,
                episode.characters?.split(",") ?: emptyList<String>()
            )
            episodeList.add(episodeResult)
        }
        return episodeList
    }

    fun selectDataSource(checkConnection: Boolean) {
        if (checkConnection) {
            episodeLD.value = episodeResponse
        } else {
            episodeLD.value = checkEpisodeEntityIsContainsList(episodeEntity)
            entityCounterPages = episodeEntity.size / numberOfItemsInResponse
        }
    }

    fun checkEpisodeListIsContainsData(list: List<EpisodeResult>) {
        if (!episodeList.containsAll(list)) {
            episodeList.addAll(list)
            addEpisodeListToDB(list)
        }
    }

    private fun checkEpisodeEntityIsContainsList(episodeEntitiesList: List<EpisodeResult>): List<EpisodeResult> {
        var newList = episodeEntitiesList
        if (episodeEntitiesList.containsAll(episodeList)) newList = episodeEntitiesList - episodeList.toSet()
        return newList
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