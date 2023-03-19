package com.example.rickmortyapp.ui.fragments.locations

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rickmortyapp.R
import com.example.rickmortyapp.data.models.locations_data_classes.LocationResponse
import com.example.rickmortyapp.data.models.locations_data_classes.LocationResult
import com.example.rickmortyapp.databinding.FragmentLocationsBinding
import com.example.rickmortyapp.ui.adapters.LocationAdapter
import com.example.rickmortyapp.ui.adapters.OnLocationItemClickListener
import com.example.rickmortyapp.ui.fragments.characters.CharacterViewModel

class LocationsFragment: Fragment(R.layout.fragment_locations), OnLocationItemClickListener {

    private var _binding: FragmentLocationsBinding? = null
    private val binding get() = requireNotNull(_binding)
    private val viewModel: LocationsViewModel by viewModels {LocationsViewModel.LocationVMFactory()}
    private var counterPages = 1
    private var allPagesNumber = 42
    private lateinit var locationAdapter: LocationAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocationsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (viewModel.locationList.isEmpty()) {
            viewModel.getLocationResponse(counterPages)
            Log.d("TAG", "getLocationResponse RUN!!")
        }

        locationAdapter = LocationAdapter(this)

        binding.apply {

            rvLocations.apply {
                adapter = locationAdapter
                layoutManager = GridLayoutManager(requireContext(), 2)
                setHasFixedSize(true)
                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        super.onScrollStateChanged(recyclerView, newState)
                        if (!recyclerView.canScrollVertically(1)) {
                            counterPages += 1
                            if (checkCounterPages(counterPages)) {
                                binding.progressBar.visibility = View.VISIBLE
                                viewModel.getLocationResponse(counterPages)

                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "This is all data that could be downloaded",
                                    Toast.LENGTH_LONG
                                ).show()
                                counterPages -= 1
                            }
                        }
                    }
                })
            }
        }

        viewModel.locationResponseLD.observe(viewLifecycleOwner){
            if (checkCounterPages(counterPages)) {
                Log.d("TAG", "Observe RUN!!")
                it.let {
                    setLocationListToAdapter(it)
                }
            }
        }
    }

    private fun setLocationListToAdapter(response: LocationResponse) {
        Log.d("TAG", "setLocationListToAdapter RUN!!")
        val a = response.locationResults
        if (viewModel.locationList.containsAll(a)) {
            Log.d("TAG", "locationList.containsAll!!")
        } else {
            Log.d("TAG", "locationList added!!")
            viewModel.locationList.addAll(response.locationResults)
        }
        allPagesNumber = response.info.pages!!
        locationAdapter.submitList(viewModel.locationList)
        locationAdapter.notifyDataSetChanged()
        binding.progressBar.visibility = View.INVISIBLE
    }

    private fun checkCounterPages(counterPages: Int): Boolean {
        Log.d("TAG", "checkCounterPages RUN!!")
        return counterPages <= allPagesNumber
    }

    override fun onItemClick(result: LocationResult) {
        Toast.makeText(requireContext(), "Item Clicked", Toast.LENGTH_LONG).show()
    }
}


