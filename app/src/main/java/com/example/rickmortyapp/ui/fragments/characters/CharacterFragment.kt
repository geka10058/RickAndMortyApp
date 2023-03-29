package com.example.rickmortyapp.ui.fragments.characters

import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Spinner
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
import com.example.rickmortyapp.data.models.characters_data_classes.CharacterResult
import com.example.rickmortyapp.databinding.FragmentCharacterBinding
import com.example.rickmortyapp.ui.adapters.CharacterAdapter
import com.example.rickmortyapp.ui.adapters.OnCharacterItemClickListener
import com.example.rickmortyapp.ui.fragments.characters.details.CharacterDetailsFragment

class CharacterFragment : Fragment(R.layout.fragment_character), OnCharacterItemClickListener {

    private var _binding: FragmentCharacterBinding? = null
    private val binding get() = requireNotNull(_binding)
    private val viewModel: CharacterViewModel by viewModels {
        CharacterViewModel.CharacterVMFactory(
            (activity?.application as RickMortyApplication).characterRepo
        )
    }
    private var counterPages = 1
    private var allPagesNumber = 42
    private var isScrollEnded = false
    private var searchFragmentIsVisible = false
    private var searchedListIsActive = false
    private lateinit var characterAdapter: CharacterAdapter
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var spinnerGender: Spinner
    private lateinit var spinnerStatus: Spinner
    private lateinit var name: String
    private lateinit var spec: String
    private lateinit var origin: String
    private lateinit var gender: String
    private lateinit var status: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCharacterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbarMenu()
        initSearchSpinners()
        addOnBackPressedCallback()

        if (viewModel.charactersList.isEmpty()) {
            viewModel.selectDataSource(checkConnection())
            viewModel.getCharacterResponse(counterPages)
        }

        characterAdapter = CharacterAdapter(this)
        gridLayoutManager = GridLayoutManager(requireContext(), 2)

        binding.apply {

            rvCharacters.apply {
                adapter = characterAdapter
                layoutManager = gridLayoutManager
                setHasFixedSize(true)
                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrollStateChanged(
                        recyclerView: RecyclerView, newState: Int
                    ) {
                        super.onScrollStateChanged(recyclerView, newState)
                        if (!searchedListIsActive) {
                            if (!recyclerView.canScrollVertically(1) && !isScrollEnded) {
                                scrollIsEnded()
                            }
                        }
                    }
                })
            }

            btnFilter.setOnClickListener {
                filterButtonClicked()
            }
        }

        viewModel.characterResponseLD.observe(viewLifecycleOwner) {
            it.let {
                val results = it.characterResults
                allPagesNumber = it.info.pages!!
                viewModel.characterResponse.clear()
                viewModel.characterResponse.addAll(results)
                viewModel.selectDataSource(checkConnection())
            }
        }

        viewModel.characterEntityLD.observe(viewLifecycleOwner) {
            it.let {
                val results = viewModel.convertEntityToResult(it)
                viewModel.characterEntity.clear()
                viewModel.characterEntity.addAll(results)
                viewModel.selectDataSource(checkConnection())
            }
        }

        viewModel.characterLD.observe(viewLifecycleOwner) {
            it.let {
                setDataToAdapter(it)
            }
        }

        viewModel.characterResponseForSearchWithParametersLD.observe(viewLifecycleOwner) {
            it.let {
                if (it.characterResults.isNullOrEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.no_data_on_request),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    setFoundDataToAdapter(it.characterResults)
                }
            }
        }

        viewModel.characterEntitiesForSearchWithParametersLD.observe(viewLifecycleOwner) {
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

    private fun initSearchSpinners() {
        spinnerGender = binding.spinnerGender
        spinnerStatus = binding.spinnerStatus
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.gender_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerGender.adapter
        }
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.status_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerStatus.adapter
        }
        initSearchVariables()
    }

    private fun initSearchVariables() {
        name = ""
        gender = spinnerGender.selectedItem.toString()
        status = spinnerStatus.selectedItem.toString()
        spec = ""
        origin = ""
    }

    private fun scrollIsEnded() {
        isScrollEnded = true
        if (checkConnection()) {
            if (viewModel.entityCounterPages > counterPages) counterPages =
                viewModel.entityCounterPages
            counterPages += 1
            if (checkCounterPages(counterPages)) {
                binding.progressBar.visibility = View.VISIBLE
                viewModel.getCharacterResponse(counterPages)
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
                            characterAdapter.submitList(viewModel.charactersList)
                            characterAdapter.notifyDataSetChanged()
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
            val newSpec = etSpecies.text.toString()
            val newOrigin = etOrigin.text.toString()
            val newGender = spinnerGender.selectedItem.toString()
            val newStatus = spinnerStatus.selectedItem.toString()
            parameters.add(Parameter(Utils.NAME, newName))
            parameters.add(Parameter(Utils.SPECIES, newSpec))
            parameters.add(Parameter(Utils.ORIGIN, newOrigin))
            if (newStatus != status) { parameters.add(Parameter(Utils.STATUS, newStatus)) } else {
                parameters.add(Parameter(Utils.STATUS, ""))
            }
            if (newGender != gender) { parameters.add(Parameter(Utils.GENDER, newGender)) } else {
                parameters.add(Parameter(Utils.GENDER, ""))
            }
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

    private fun setDataToAdapter(list: List<CharacterResult>) {
        viewModel.checkCharacterListIsContainsData(list)
        characterAdapter.submitList(viewModel.charactersList)
        characterAdapter.notifyDataSetChanged()
        binding.progressBar.visibility = View.INVISIBLE
        isScrollEnded = false
    }

    private fun setFoundDataToAdapter(list: List<CharacterResult>) {
        characterAdapter.submitList(list)
        characterAdapter.notifyDataSetChanged()
        searchedListIsActive = true
    }

    private fun checkCounterPages(counterPages: Int): Boolean {
        return counterPages <= allPagesNumber
    }

    private fun checkConnection(): Boolean {
        return checkInternetConnection(requireActivity() as AppCompatActivity)
    }

    override fun onItemClick(result: CharacterResult) {
        val bundle = Bundle()
        bundle.putInt(Utils.BUNDLE_FLAG_CHARACTER, result.id)
        val characterDetailsFragment = CharacterDetailsFragment()
        characterDetailsFragment.arguments = bundle
        requireActivity().supportFragmentManager
            .beginTransaction()
            .replace(R.id.container_for_fragments, characterDetailsFragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onPause() {
        super.onPause()
        viewModel.restoredItemPosition = gridLayoutManager.findFirstCompletelyVisibleItemPosition()
    }
}


