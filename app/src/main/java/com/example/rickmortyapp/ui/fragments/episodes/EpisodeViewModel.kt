package com.example.rickmortyapp.ui.fragments.episodes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rickmortyapp.data.models.episodes_data_classes.EpisodeResult
import com.example.rickmortyapp.data.models.locations_data_classes.LocationResult
import com.example.rickmortyapp.data.retrofit_controllers.EpisodeResponseRC
import com.example.rickmortyapp.data.retrofit_controllers.LocationResponseRC

class EpisodeViewModel: ViewModel(){

    private val controller = EpisodeResponseRC()
    val episodeResponseLD = controller.episodeResponseLiveData
    val episodeList = mutableListOf<EpisodeResult>()

    fun getEpisodeResponse(pageNumber: Int){
        controller.getEpisodeResponse(pageNumber)
    }

    class EpisodeVMFactory() : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(EpisodeViewModel::class.java)) {
                return EpisodeViewModel() as T
            }
            throw IllegalArgumentException("unknown VM class")
        }
    }
}