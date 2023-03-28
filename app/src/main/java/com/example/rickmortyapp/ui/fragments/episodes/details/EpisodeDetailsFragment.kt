package com.example.rickmortyapp.ui.fragments.episodes.details

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
import com.example.rickmortyapp.data.json_models.characters_data_classes.CharacterResult
import com.example.rickmortyapp.data.json_models.episodes_data_classes.EpisodeResult
import com.example.rickmortyapp.databinding.FragmentEpisodeDetailsBinding
import com.example.rickmortyapp.ui.adapters.CharacterMiniAdapter

class EpisodeDetailsFragment : Fragment(R.layout.fragment_episode_details) {

    private var _binding: FragmentEpisodeDetailsBinding? = null
    private val binding get() = requireNotNull(_binding)
    private var characterId = 0
    private val viewModel: EpisodeDetailsViewModel by viewModels {
        EpisodeDetailsVMFactory((activity?.application as RickMortyApplication).episodeRepo)
    }
    private lateinit var args: Bundle
    private lateinit var characterAdapter: CharacterMiniAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEpisodeDetailsBinding.inflate(inflater, container, false)
        args = this.requireArguments()
        characterId = args.getInt(Utils.BUNDLE_FLAG_EPISODE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        characterAdapter = CharacterMiniAdapter()

        binding.apply {
            group.visibility = View.INVISIBLE
            progressBar.visibility = View.VISIBLE
            root.setOnRefreshListener {
                viewModel.getEpisodeDetails(characterId, checkConnection())
                root.isRefreshing = false
            }
        }

        viewModel.getEpisodeDetails(characterId, checkConnection())

        viewModel.episodeDetailsResultLD.observe(viewLifecycleOwner) {
            if (checkConnection()) {
                it.let {
                    viewModel.episodeDetailsLiveData.value = it
                    val characterList =
                        viewModel.convertCharacterListToIdList(it.characters ?: emptyList())
                    viewModel.getCharacterListForEpisode(characterList)
                }
            }
        }

        viewModel.episodeEntityDetailsLD.observe(viewLifecycleOwner) {
            if (!checkConnection()) {
                it.let {
                    val result = viewModel.convertEntityToResult(it)
                    viewModel.episodeDetailsLiveData.value = result
                    binding.tvCharacters.text = getString(R.string.internet_connection_off)
                }
            }
        }

        viewModel.episodeDetailsLiveData.observe(viewLifecycleOwner) {
            it.let {
                setDataToView(it)
            }
        }

        viewModel.characterListForEpisodeLD.observe(viewLifecycleOwner) {
            it.let {
                setDataToAdapter(it)
            }
        }
    }

    private fun setDataToView(episodeResult: EpisodeResult) {
        binding.apply {
            tvName.text = episodeResult.name
            tvEpisode.text = episodeResult.episode
            tvAirDate.text = episodeResult.airDate
            tvCharacters.text = ""
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