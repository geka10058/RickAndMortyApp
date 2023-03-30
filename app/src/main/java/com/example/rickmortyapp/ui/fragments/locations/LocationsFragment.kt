package com.example.rickmortyapp.ui.fragments.locations

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
import com.example.rickmortyapp.data.models.Parameter
import com.example.rickmortyapp.data.models.locations_data_classes.LocationResult
import com.example.rickmortyapp.databinding.FragmentLocationsBinding
import com.example.rickmortyapp.ui.adapters.LocationAdapter
import com.example.rickmortyapp.ui.adapters.OnLocationItemClickListener
import com.example.rickmortyapp.ui.fragments.locations.details.LocationDetailsFragment

class LocationsFragment : Fragment(R.layout.fragment_locations), OnLocationItemClickListener {

    private var _binding: FragmentLocationsBinding? = null
    private val binding get() = requireNotNull(_binding)
    private val viewModel: LocationsViewModel by viewModels {
        LocationsViewModel.LocationVMFactory(
            (activity?.application as RickMortyApplication).locationRepo
        )
    }
    private var counterPages = 1
    private var allPagesNumber = 42
    private var isScrollEnded = false
    private var searchFragmentIsVisible = false
    private var searchedListIsActive = false
    private lateinit var locationAdapter: LocationAdapter
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var name: String
    private lateinit var type: String
    private lateinit var dimension: String


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbarMenu()
        initSearchVariables()
        addOnBackPressedCallback()

        if (viewModel.locationList.isEmpty()) {
            viewModel.selectDataSource(checkConnection())
            viewModel.getLocationResponse(counterPages)
        }

        locationAdapter = LocationAdapter(this)
        gridLayoutManager = GridLayoutManager(requireContext(), 2)

        binding.apply {
            rvLocations.apply {
                adapter = locationAdapter
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
                locationAdapter.submitList(viewModel.locationList)
                locationAdapter.notifyDataSetChanged()
                root.isRefreshing = false
            }
        }



        viewModel.locationResponseLD.observe(viewLifecycleOwner) {
            it.let {
                val result = it.locationResults
                allPagesNumber = it.info.pages!!
                viewModel.locationResponse.clear()
                viewModel.locationResponse.addAll(result)
                viewModel.selectDataSource(checkConnection())
            }
        }

        viewModel.locationEntityLD.observe(viewLifecycleOwner) {
            it.let {
                val result = viewModel.convertEntityToResult(it)
                viewModel.locationEntity.clear()
                viewModel.locationEntity.addAll(result)
                viewModel.selectDataSource(checkConnection())
            }
        }

        viewModel.locationLD.observe(viewLifecycleOwner){
            it.let{
                setDataToAdapter(it)
            }
        }

        viewModel.locationResponseForSearchWithParametersLD.observe(viewLifecycleOwner){
            it.let {
                if (it.locationResults.isNullOrEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.no_data_on_request),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    setFoundDataToAdapter(it.locationResults)
                }
            }
        }

        viewModel.locationEntitiesForSearchWithParametersLD.observe(viewLifecycleOwner) {
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
        type = ""
        dimension = ""
    }

    private fun scrollIsEnded(){
        isScrollEnded = true
        if (checkConnection()) {
            if (viewModel.locationCounterPage > counterPages) counterPages = viewModel.locationCounterPage
            counterPages +=1
            if (checkCounterPages(counterPages)) {
                binding.progressBar.visibility = View.VISIBLE
                viewModel.getLocationResponse(counterPages)
            }else {
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
                            locationAdapter.submitList(viewModel.locationList)
                            locationAdapter.notifyDataSetChanged()
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
            val newType = etType.text.toString()
            val newDimension = etDimension.text.toString()
            parameters.add(Parameter(Utils.NAME, newName))
            parameters.add(Parameter(Utils.TYPE, newType))
            parameters.add(Parameter(Utils.DIMENSION, newDimension))
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

    private fun setDataToAdapter(list: List<LocationResult>) {
        viewModel.checkLocationListIsContainsData(list)
        locationAdapter.submitList(viewModel.locationList)
        locationAdapter.notifyDataSetChanged()
        binding.progressBar.visibility = View.INVISIBLE
        isScrollEnded = false
    }

    private fun setFoundDataToAdapter(list: List<LocationResult>) {
        locationAdapter.submitList(list)
        locationAdapter.notifyDataSetChanged()
        searchedListIsActive = true
    }

    private fun checkCounterPages(counterPages: Int): Boolean {
        return counterPages <= allPagesNumber
    }

    private fun checkConnection():Boolean{
        return Utils.checkInternetConnection(requireActivity() as AppCompatActivity)
    }



    override fun onItemClick(result: LocationResult) {
        val bundle = Bundle()
        bundle.putInt(Utils.BUNDLE_FLAG_LOCATION, result.id)
        val locationDetailsFragment = LocationDetailsFragment()
        locationDetailsFragment.arguments = bundle
        requireActivity().supportFragmentManager
            .beginTransaction()
            .replace(R.id.container_for_fragments, locationDetailsFragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onPause() {
        super.onPause()
        val position = gridLayoutManager.findFirstCompletelyVisibleItemPosition()
        viewModel.restoredItemPosition = position
    }
}


