package com.example.rickmortyapp.ui.fragments.episodes

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rickmortyapp.R
import com.example.rickmortyapp.RickMortyApplication
import com.example.rickmortyapp.Utils
import com.example.rickmortyapp.Utils.checkInternetConnection
import com.example.rickmortyapp.data.models.Parameter
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
    private var searchFragmentIsVisible = false
    private var searchedListIsActive = false
    private lateinit var episodesAdapter: EpisodeAdapter
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var name: String
    private lateinit var episode: String

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
        setupToolbarMenu()
        initSearchVariables()
        addOnBackPressedCallback()

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

            btnFilter.setOnClickListener {
                filterButtonClicked()
            }

            root.setOnRefreshListener {
                episodesAdapter.submitList(viewModel.episodeList)
                episodesAdapter.notifyDataSetChanged()
                root.isRefreshing = false
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

        viewModel.episodeResponseForSearchWithParametersLD.observe(viewLifecycleOwner){
            it.let {
                if (it.episodeResults.isNullOrEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.no_data_on_request),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    setFoundDataToAdapter(it.episodeResults)
                }
            }
        }

        viewModel.episodeEntitiesForSearchWithParametersLD.observe(viewLifecycleOwner) {
            it.let {
                val results = viewModel.convertEntityToResult(it)
                setFoundDataToAdapter(results)
            }
        }
    }

    private fun setupToolbarMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.toolbar_search_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.filter -> {
                        showOrHideSearchFragment()
                    }
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun showOrHideSearchFragment() {
        if (!searchFragmentIsVisible) {
            binding.groupSearch.visibility = View.VISIBLE
            searchFragmentIsVisible = true
        } else {
            binding.groupSearch.visibility = View.INVISIBLE
            searchFragmentIsVisible = false
        }
    }

    private fun initSearchVariables() {
        name = ""
        episode = ""
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

    private fun addOnBackPressedCallback() {
        val callback = requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    when (isEnabled) {
                        searchedListIsActive -> {
                            episodesAdapter.submitList(viewModel.episodeList)
                            episodesAdapter.notifyDataSetChanged()
                            searchedListIsActive = false
                            isEnabled = false
                        }
                        searchFragmentIsVisible -> {
                            showOrHideSearchFragment()
                        }
                        else -> {
                            isEnabled = false
                            requireActivity().onBackPressed()
                        }
                    }
                }
            })
    }

    private fun filterButtonClicked() {
        val parameters = mutableListOf<Parameter>()
        binding.apply {
            val newName = etName.text.toString()
            val newEpisode = etEpisode.text.toString()
            parameters.add(Parameter(Utils.NAME, newName))
            parameters.add(Parameter(Utils.EPISODE, newEpisode))
        }
        if (checkSelectedParameters(parameters)) {
            viewModel.getSearchWithParameters(parameters, checkConnection())
            showOrHideSearchFragment()
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.parameters_not_selected),
                Toast.LENGTH_SHORT
            ).show()
            showOrHideSearchFragment()
        }
    }

    private fun checkSelectedParameters(parameters: List<Parameter>): Boolean{
        var counter = 0
        for (value in parameters) {
            if (value.value == "") counter += 1
        }
        return parameters.size !== counter
    }

    private fun setDataToAdapter(list: List<EpisodeResult>) {
        viewModel.checkEpisodeListIsContainsData(list)
        episodesAdapter.submitList(viewModel.episodeList)
        episodesAdapter.notifyDataSetChanged()
        binding.progressBar.visibility = View.INVISIBLE
        isScrollEnded = false
    }

    private fun setFoundDataToAdapter(list: List<EpisodeResult>) {
        episodesAdapter.submitList(list)
        episodesAdapter.notifyDataSetChanged()
        searchedListIsActive = true
    }

    private fun checkCounterPages(counterPages: Int): Boolean {
        return counterPages <= allPagesNumber
    }

    private fun checkConnection(): Boolean {
        return checkInternetConnection(requireActivity() as AppCompatActivity)
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


