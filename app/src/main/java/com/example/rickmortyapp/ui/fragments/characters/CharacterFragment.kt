package com.example.rickmortyapp.ui.fragments.characters

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
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
import com.example.rickmortyapp.data.json_models.characters_data_classes.CharacterResult
import com.example.rickmortyapp.databinding.FragmentCharacterBinding
import com.example.rickmortyapp.ui.adapters.CharacterAdapter
import com.example.rickmortyapp.ui.adapters.OnCharacterItemClickListener
import com.example.rickmortyapp.ui.fragments.characters.details.CharacterDetailsFragment
import java.util.*

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
    private lateinit var characterAdapter: CharacterAdapter
    private lateinit var gridLayoutManager: GridLayoutManager

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
                        if (!recyclerView.canScrollVertically(1) && !isScrollEnded) {
                            scrollIsEnded()
                        }
                    }
                })
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
    }

    private fun setupToolbarMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider{

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.toolbar_search_menu,menu)
                /*val manager = requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager
                val searchItem = menu.findItem(R.id.search)
                val searchView = searchItem.actionView as SearchView

                //searchView.setSearchableInfo(manager.getSearchableInfo())

                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        searchView.clearFocus()
                        searchView.setQuery("", false)
                        searchView.onActionViewCollapsed()
                        Toast.makeText(requireContext(),"$query", Toast.LENGTH_SHORT).show()
                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        return false
                    }
                })*/
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when(menuItem.itemId) {
                    R.id.search -> {
                        val searchView = menuItem.actionView as SearchView
                        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                            override fun onQueryTextSubmit(query: String?): Boolean {
                                searchView.clearFocus()
                                searchView.setQuery("", false)
                                searchView.onActionViewCollapsed()
                                searchByName(query)
                                return true
                            }

                            override fun onQueryTextChange(newText: String?): Boolean {
                                return false
                            }

                        })
                    }
                    R.id.filter -> Toast.makeText(requireContext(),"filter clicked", Toast.LENGTH_SHORT).show()
                }
                return true
            }
        },viewLifecycleOwner,Lifecycle.State.RESUMED)
    }

    private fun searchByName(searchString: String?) {
        if (checkConnection()) {

        } else {
            if (searchString != null && searchString.isNotEmpty()) {
                val list: List<CharacterResult> = viewModel.charactersList.filter {
                    it.name!!.uppercase(Locale.getDefault())
                        .contains(searchString.uppercase(Locale.getDefault()))
                }
                characterAdapter.submitList(list)
            }
        }
        characterAdapter.notifyDataSetChanged()
    }

    private fun setDataToAdapter(list: List<CharacterResult>) {
        viewModel.checkCharacterListIsContainsData(list)
        characterAdapter.submitList(viewModel.charactersList)
        characterAdapter.notifyDataSetChanged()
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


