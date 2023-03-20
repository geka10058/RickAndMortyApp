package com.example.rickmortyapp.ui.fragments.characters

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
import com.example.rickmortyapp.data.models.characters_data_classes.CharacterResponse
import com.example.rickmortyapp.data.models.characters_data_classes.CharacterResult
import com.example.rickmortyapp.databinding.FragmentCharacterBinding
import com.example.rickmortyapp.ui.adapters.CharacterAdapter
import com.example.rickmortyapp.ui.adapters.OnCharacterItemClickListener

class CharacterFragment : Fragment(R.layout.fragment_character), OnCharacterItemClickListener {

    private var _binding: FragmentCharacterBinding? = null
    private val binding get() = requireNotNull(_binding)
    private val viewModel: CharacterViewModel by viewModels { CharacterViewModel.CharacterVMFactory() }
    private var counterPages = 1
    private var allPagesNumber = 42
    private lateinit var characterAdapter: CharacterAdapter

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

        //requireActivity().title = "FragmentCharacter"

        if (viewModel.charactersList.isEmpty()) {
            viewModel.getCharacterResponse(counterPages)
            Log.d("TAG", "getCharacterResponse RUN!!")
        }

        characterAdapter = CharacterAdapter(this)

        binding.apply {

            rvCharacters.apply {
                adapter = characterAdapter
                layoutManager = GridLayoutManager(requireContext(), 2)
                setHasFixedSize(true)
                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        super.onScrollStateChanged(recyclerView, newState)
                        if (!recyclerView.canScrollVertically(1)) {
                            counterPages += 1
                            if (checkCounterPages(counterPages)) {
                                binding.progressBar.visibility = View.VISIBLE
                                viewModel.getCharacterResponse(counterPages)

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

        viewModel.characterResponseLD.observe(viewLifecycleOwner) {
            if (checkCounterPages(counterPages)) {
                Log.d("TAG", "Observe RUN!!")
                it.let {
                    setCharacterListToAdapter(it)
                }
            }
        }
    }

    private fun setCharacterListToAdapter(response: CharacterResponse) {
        Log.d("TAG", "setCharacterListToAdapter RUN!!")
        val results = response.characterResults
        if (viewModel.charactersList.containsAll(results)) {
            Log.d("TAG", "charactersList.containsAll!!")
        } else {
            Log.d("TAG", "charactersList added!!")
            viewModel.charactersList.addAll(response.characterResults)
        }
        allPagesNumber = response.info.pages!!
        characterAdapter.submitList(viewModel.charactersList)
        characterAdapter.notifyDataSetChanged()
        binding.progressBar.visibility = View.INVISIBLE
    }

    private fun checkCounterPages(counterPages: Int): Boolean {
        Log.d("TAG", "checkCounterPages RUN!!")
        return counterPages <= allPagesNumber
    }

    override fun onItemClick(result: CharacterResult) {
        Toast.makeText(requireContext(), "Item Clicked", Toast.LENGTH_LONG).show()
    }
}


