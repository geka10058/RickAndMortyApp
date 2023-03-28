package com.example.rickmortyapp.ui.fragments.locations

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
import com.example.rickmortyapp.data.json_models.locations_data_classes.LocationResult
import com.example.rickmortyapp.databinding.FragmentLocationsBinding
import com.example.rickmortyapp.ui.adapters.LocationAdapter
import com.example.rickmortyapp.ui.adapters.OnLocationItemClickListener

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
    private lateinit var locationAdapter: LocationAdapter
    private lateinit var gridLayoutManager: GridLayoutManager


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
    }

    /*private fun setLocationListToAdapter(response: LocationResponse) {
        Log.d("TAG", "setLocationListToAdapter RUN!!")
        val results = response.locationResults
        if (viewModel.locationList.containsAll(results)) {
            Log.d("TAG", "locationList.containsAll!!")
        } else {
            Log.d("TAG", "locationList added!!")
            viewModel.locationList.addAll(response.locationResults)
            viewModel.addLocationListToDB(response.locationResults)
        }
        allPagesNumber = response.info.pages!!
        locationAdapter.submitList(viewModel.locationList)
        locationAdapter.notifyDataSetChanged()
        binding.progressBar.visibility = View.INVISIBLE
    }*/

    private fun setDataToAdapter(list: List<LocationResult>) {
        viewModel.checkLocationListIsContainsData(list)
        locationAdapter.submitList(viewModel.locationList)
        locationAdapter.notifyDataSetChanged()
        binding.progressBar.visibility = View.INVISIBLE
        isScrollEnded = false
    }

    private fun checkCounterPages(counterPages: Int): Boolean {
        return counterPages <= allPagesNumber
    }

    private fun checkConnection():Boolean{
        return Utils.checkInternetConnection(requireActivity() as AppCompatActivity)
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
                    "This is all data that could be downloaded",
                    Toast.LENGTH_SHORT
                ).show()
                counterPages -= 1
            }
        } else {
            viewModel.selectDataSource(checkConnection())
            Toast.makeText(
                requireContext(),
                "False checkInternetConnection",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onItemClick(result: LocationResult) {
        Toast.makeText(requireContext(), "Item Clicked", Toast.LENGTH_SHORT).show()
    }

    override fun onPause() {
        super.onPause()
        val position = gridLayoutManager.findFirstCompletelyVisibleItemPosition()
        viewModel.restoredItemPosition = position
    }
}


