package com.example.rickmortyapp.ui.fragments.episodes.details

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rickmortyapp.data.db.entities.EpisodeEntity
import com.example.rickmortyapp.data.db.repositories.EpisodeRepo
import com.example.rickmortyapp.data.models.episodes_data_classes.EpisodeResult
import com.example.rickmortyapp.data.retrofit_controllers.CharacterListForEpisodeRC
import com.example.rickmortyapp.data.retrofit_controllers.EpisodeByIdResultRC

class EpisodeDetailsViewModel(private val episodeRepo: EpisodeRepo): ViewModel() {

    private val controllerForEpisodeResponse = EpisodeByIdResultRC()
    private val controllerForCharacterList = CharacterListForEpisodeRC()
    val characterListForEpisodeLD = controllerForCharacterList.characterListForEpisodeLiveData
    val episodeDetailsResultLD = controllerForEpisodeResponse.episodeByIdResultLiveData
    val episodeEntityDetailsLD = episodeRepo.episodeByIdLiveData
    val episodeDetailsLiveData = MutableLiveData<EpisodeResult>()

    fun getEpisodeDetails(characterID: Int, checkConnection:Boolean) {
        if (checkConnection){
            controllerForEpisodeResponse.getEpisodeByIdResult(characterID)
        } else {
            episodeRepo.getEpisodeById(characterID)
        }
    }

    fun getCharacterListForEpisode(idList: String){
        controllerForCharacterList.getCharacterListForEpisode(idList)
    }

    fun convertEntityToResult(episodeEntity: EpisodeEntity): EpisodeResult {
        return EpisodeResult(
            episodeEntity.id,
            episodeEntity.name,
            episodeEntity.airDate,
            episodeEntity.episode,
            episodeEntity.characters?.split(",") ?: listOf("Unknown")
        )
    }

    fun convertCharacterListToIdList(characterList: List<String>): String {
        val idList = mutableListOf<String>()
        for (character in characterList) {
            val elementList = character.split("/")
            val id = elementList.last()
            idList.add(id)
        }
        return idList.toString()
    }
}

class EpisodeDetailsVMFactory(private val episodeRepo: EpisodeRepo) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EpisodeDetailsViewModel::class.java)) {
            return EpisodeDetailsViewModel(episodeRepo) as T
        }
        throw IllegalArgumentException("unknown VM class")
    }
}