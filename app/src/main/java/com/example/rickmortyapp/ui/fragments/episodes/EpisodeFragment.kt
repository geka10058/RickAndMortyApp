package com.example.rickmortyapp.ui.fragments.episodes

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rickmortyapp.R
import com.example.rickmortyapp.RickMortyApplication
import com.example.rickmortyapp.Utils
import com.example.rickmortyapp.Utils.checkInternetConnection
import com.example.rickmortyapp.data.models.episodes_data_classes.EpisodeResult
import com.example.rickmortyapp.databinding.FragmentEpisodesBinding
import com.example.rickmortyapp.ui.adapters.EpisodeAdapter
import com.example.rickmortyapp.ui.adapters.OnEpisodeItemClickListener
import com.example.rickmortyapp.ui.fragments.episodes.details.EpisodeDetailsFragment

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
    private var isScrollEnded = false
    private lateinit var episodesAdapter: EpisodeAdapter
    private lateinit var gridLayoutManager: GridLayoutManager

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
            viewModel.selectDataSource(checkConnection())
            viewModel.getEpisodeResponse(counterPages)
        }

        episodesAdapter = EpisodeAdapter(this)
        gridLayoutManager = GridLayoutManager(requireContext(), 2)

        binding.apply {
            rvEpisode.apply {
                adapter = episodesAdapter
                layoutManager = gridLayoutManager
                setHasFixedSize(true)
                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        super.onScrollStateChanged(recyclerView, newState)
                        if (!recyclerView.canScrollVertically(1) && !isScrollEnded) {
                            scrollIsEnded()
                        }
                    }
                })
            }
        }

        viewModel.episodeResponseLD.observe(viewLifecycleOwner) {
            it.let {
                if (it.episodeResults != null) {
                    val result = it.episodeResults
                    allPagesNumber = it.info.pages!!
                    viewModel.episodeResponse.clear()
                    viewModel.episodeResponse.addAll(result)
                    viewModel.selectDataSource(checkConnection())
                }
            }
        }

        viewModel.episodeEntityLD.observe(viewLifecycleOwner) {
            it.let {
                val result = viewModel.convertEntityToResult(it)
                viewModel.episodeEntity.clear()
                viewModel.episodeEntity.addAll(result)
                viewModel.selectDataSource(checkConnection())
            }
        }

        viewModel.episodeLD.observe(viewLifecycleOwner) {
            it.let {
                setDataToAdapter(it)
            }
        }
    }


    private fun setDataToAdapter(list: List<EpisodeResult>) {
        viewModel.checkEpisodeListIsContainsData(list)
        episodesAdapter.submitList(viewModel.episodeList)
        episodesAdapter.notifyDataSetChanged()
        binding.progressBar.visibility = View.INVISIBLE
        isScrollEnded = false
    }

    private fun checkCounterPages(counterPages: Int): Boolean {
        return counterPages <= allPagesNumber
    }

    private fun checkConnection(): Boolean {
        return checkInternetConnection(requireActivity() as AppCompatActivity)
    }

    private fun scrollIsEnded() {
        isScrollEnded = true
        if (checkConnection()) {
            if (viewModel.entityCounterPages > counterPages) counterPages =
                viewModel.entityCounterPages
            counterPages += 1
            if (checkCounterPages(counterPages)) {
                binding.progressBar.visibility = View.VISIBLE
                viewModel.getEpisodeResponse(counterPages)
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.all_data_uploaded),
                    Toast.LENGTH_SHORT
                ).show()
                counterPages -= 1
            }
        } else {
            viewModel.selectDataSource(checkConnection())
            Toast.makeText(
                requireContext(),
                getString(R.string.internet_connection_off),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onItemClick(result: EpisodeResult) {
        val bundle = Bundle()
        bundle.putInt(Utils.BUNDLE_FLAG_EPISODE, result.id)
        val episodeDetailsFragment = EpisodeDetailsFragment()
        episodeDetailsFragment.arguments = bundle
        requireActivity().supportFragmentManager
            .beginTransaction()
            .replace(R.id.container_for_fragments, episodeDetailsFragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onPause() {
        super.onPause()
        viewModel.restoredItemPosition = gridLayoutManager.findFirstCompletelyVisibleItemPosition()
    }
}


