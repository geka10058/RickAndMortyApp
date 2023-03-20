package com.example.rickmortyapp.ui.fragments.episodes

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rickmortyapp.R
import com.example.rickmortyapp.RickMortyApplication
import com.example.rickmortyapp.data.models.episodes_data_classes.EpisodeResponse
import com.example.rickmortyapp.data.models.episodes_data_classes.EpisodeResult
import com.example.rickmortyapp.databinding.FragmentEpisodesBinding
import com.example.rickmortyapp.ui.adapters.EpisodeAdapter
import com.example.rickmortyapp.ui.adapters.OnEpisodeItemClickListener

class EpisodeFragment : Fragment(R.layout.fragment_episodes), OnEpisodeItemClickListener {

    private var _binding: FragmentEpisodesBinding? = null
    private val binding get() = requireNotNull(_binding)
    private val viewModel: EpisodeViewModel by viewModels {
        EpisodeViewModel.EpisodeVMFactory(
            (activity?.application as RickMortyApplication).episodeRepo
        )
    }
    private var counterPages = 1
    private var allPagesNumber = 8
    private lateinit var episodesAdapter: EpisodeAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEpisodesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (viewModel.episodeList.isEmpty()) {
            viewModel.getEpisodeResponse(counterPages)
            Log.d("TAG", "getEpisodeResponse RUN!!")
        }

        episodesAdapter = EpisodeAdapter(this)

        binding.apply {
            rvEpisode.apply {
                adapter = episodesAdapter
                layoutManager = GridLayoutManager(requireContext(), 2)
                setHasFixedSize(true)
                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        super.onScrollStateChanged(recyclerView, newState)
                        if (!recyclerView.canScrollVertically(1)) {
                            counterPages += 1
                            if (checkCounterPages(counterPages)) {
                                binding.progressBar.visibility = View.VISIBLE
                                viewModel.getEpisodeResponse(counterPages)

                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "This is all data that could be downloaded",
                                    Toast.LENGTH_SHORT
                                ).show()
                                counterPages -= 1
                            }
                        }
                    }
                })
            }
        }

        viewModel.episodeResponseLD.observe(viewLifecycleOwner) {
            if (checkCounterPages(counterPages)) {
                Log.d("TAG", "Observe RUN!!")
                it.let {
                    setEpisodeListToAdapter(it)
                }
            }
        }

        viewModel.episodeEntityLD.observe(viewLifecycleOwner) {
            it.let {
                Log.d("TAG", "episodeEntityLD $it")
            }
        }
    }

    private fun setEpisodeListToAdapter(response: EpisodeResponse) {
        Log.d("TAG", "setCharacterListToAdapter RUN!!")
        val results = response.episodeResults
        if (viewModel.episodeList.containsAll(results)) {
            Log.d("TAG", "charactersList.containsAll!!")
        } else {
            Log.d("TAG", "charactersList added!!")
            viewModel.episodeList.addAll(response.episodeResults)
            viewModel.addEpisodesToDB(response.episodeResults)
        }
        allPagesNumber = response.info.pages!!
        episodesAdapter.submitList(viewModel.episodeList)
        episodesAdapter.notifyDataSetChanged()
        binding.progressBar.visibility = View.INVISIBLE
    }

    private fun checkCounterPages(counterPages: Int): Boolean {
        Log.d("TAG", "checkCounterPages RUN!!")
        return counterPages <= allPagesNumber
    }

    override fun onItemClick(result: EpisodeResult) {
        Toast.makeText(requireContext(), "Item Clicked", Toast.LENGTH_LONG).show()
    }
}


