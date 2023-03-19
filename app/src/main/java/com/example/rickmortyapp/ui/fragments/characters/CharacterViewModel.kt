package com.example.rickmortyapp.ui.fragments.characters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rickmortyapp.data.models.characters_data_classes.CharacterResult
import com.example.rickmortyapp.data.retrofit_controllers.CharacterResponseRC

class CharacterViewModel:ViewModel() {

    private val controller = CharacterResponseRC()
    val characterResponseLD = controller.characterResponseLiveData
    val charactersList = mutableListOf<CharacterResult>()

    fun getCharacterResponse(pageNumber: Int){
        controller.getCharacterResponse(pageNumber)
    }

    class CharacterVMFactory() : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CharacterViewModel::class.java)) {
                return CharacterViewModel() as T
            }
            throw IllegalArgumentException("unknown VM class")
        }
    }
}