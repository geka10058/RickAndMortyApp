package com.example.rickmortyapp.ui.fragments.characters.details

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.rickmortyapp.R
import com.example.rickmortyapp.RickMortyApplication
import com.example.rickmortyapp.Utils
import com.example.rickmortyapp.data.models.characters_data_classes.CharacterResult
import com.example.rickmortyapp.databinding.FragmentCharacterDetailsBinding

class CharacterDetailsFragment: Fragment(R.layout.fragment_character_details) {

    private var _binding: FragmentCharacterDetailsBinding? = null
    private val binding get() = requireNotNull(_binding)
    lateinit var args: Bundle
    private var characterId = 0
    private val viewModel: CharacterDetailsViewModel by viewModels {
        CharacterDetailsVMFactory(
            (activity?.application as RickMortyApplication).characterRepo
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCharacterDetailsBinding.inflate(inflater, container, false)
        args = this.requireArguments()
        characterId = args.getInt(Utils.BUNDLE_FLAG_CHARACTER)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("TAG", "onViewCreated")

        binding.group.visibility = View.INVISIBLE
        binding.progressBar.visibility = View.VISIBLE
        viewModel.getCharactersDetails(characterId, checkConnection())

        viewModel.characterDetailsResultLD.observe(viewLifecycleOwner){
            if (checkConnection()){
                it.let {
                    viewModel.characterDetailsLiveData.value = it
                }
            }
        }

        viewModel.characterEntityDetailsLD.observe(viewLifecycleOwner){
            if (!checkConnection()){
                it.let{
                    val result = viewModel.convertEntityToResult(it)
                    viewModel.characterDetailsLiveData.value = result
                }
            }
        }

        viewModel.characterDetailsLiveData.observe(viewLifecycleOwner){
            it.let{
                Log.d("TAG", "Данные есть")
                Log.d("TAG", it.toString())
                setData(it)
            }
        }
    }

    private fun setData(characterResult: CharacterResult){
        binding.apply {
            tvName.text = characterResult.name
            tvStatus.text = characterResult.status
            tvSpecies.text = characterResult.species
            tvGender.text = characterResult.gender
            tvOrigin.text = characterResult.origin?.name ?: "Unknown"
            tvLocation.text = characterResult.location?.name ?: "Unknown"

            Glide.with(requireContext())
                .load(characterResult.image)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(R.drawable.splash_image)
                .into(ivCharacter)

            binding.progressBar.visibility = View.INVISIBLE
            binding.group.visibility = View.VISIBLE
        }
    }

    private fun checkConnection(): Boolean {
        return Utils.checkInternetConnection(requireActivity() as AppCompatActivity)
    }
}