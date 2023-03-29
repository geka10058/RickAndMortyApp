package com.example.rickmortyapp.ui.fragments.locations.details

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rickmortyapp.R
import com.example.rickmortyapp.RickMortyApplication
import com.example.rickmortyapp.Utils
import com.example.rickmortyapp.data.models.characters_data_classes.CharacterResult
import com.example.rickmortyapp.data.models.locations_data_classes.LocationResult
import com.example.rickmortyapp.databinding.FragmentLocationDetailsBinding
import com.example.rickmortyapp.ui.adapters.CharacterMiniAdapter

class LocationDetailsFragment : Fragment(R.layout.fragment_episode_details) {

    private var _binding: FragmentLocationDetailsBinding? = null
    private val binding get() = requireNotNull(_binding)
    private var characterId = 0
    private val viewModel: LocationDetailsViewModel by viewModels {
        LocationDetailsVMFactory((activity?.application as RickMortyApplication).locationRepo)
    }
    private lateinit var args: Bundle
    private lateinit var characterAdapter: CharacterMiniAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocationDetailsBinding.inflate(inflater, container, false)
        args = this.requireArguments()
        characterId = args.getInt(Utils.BUNDLE_FLAG_LOCATION)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        characterAdapter = CharacterMiniAdapter()

        binding.apply {
            group.visibility = View.INVISIBLE
            progressBar.visibility = View.VISIBLE
            root.setOnRefreshListener {
                viewModel.getLocationDetails(characterId, checkConnection())
                root.isRefreshing = false
            }
        }

        viewModel.getLocationDetails(characterId, checkConnection())

        viewModel.locationDetailsResultLD.observe(viewLifecycleOwner) {
            if (checkConnection()) {
                it.let {
                    viewModel.locationDetailsLiveData.value = it
                    val characterList =
                        viewModel.convertLocationListToIdList(it.residents ?: emptyList())
                    viewModel.getCharacterListForEpisode(characterList)
                }
            }
        }

        viewModel.locationEntityDetailsLD.observe(viewLifecycleOwner) {
            if (!checkConnection()) {
                it.let {
                    val result = viewModel.convertEntityToResult(it)
                    viewModel.locationDetailsLiveData.value = result
                    binding.tvResidents.text = getString(R.string.internet_connection_off)
                }
            }
        }

        viewModel.locationDetailsLiveData.observe(viewLifecycleOwner) {
            it.let {
                setDataToView(it)
            }
        }

        viewModel.residentsListForEpisodeLD.observe(viewLifecycleOwner) {
            it.let {
                setDataToAdapter(it)
            }
        }
    }

    private fun setDataToView(locationResult: LocationResult) {
        binding.apply {
            tvName.text = locationResult.name
            tvType.text = locationResult.type
            tvDimension.text = locationResult.dimension
            tvResidents.text = ""
            progressBar.visibility = View.INVISIBLE
            group.visibility = View.VISIBLE
        }
    }

    private fun setDataToAdapter(result: List<CharacterResult>) {
        binding.rvCharacters.apply {
            adapter = characterAdapter
            layoutManager = LinearLayoutManager(
                requireContext(),
                RecyclerView.HORIZONTAL, false
            )
            setHasFixedSize(true)
            characterAdapter.submitList(result)
            characterAdapter.notifyDataSetChanged()
        }
    }

    private fun checkConnection(): Boolean {
        return Utils.checkInternetConnection(requireActivity() as AppCompatActivity)
    }

}