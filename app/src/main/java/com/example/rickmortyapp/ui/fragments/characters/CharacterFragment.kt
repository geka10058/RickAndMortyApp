package com.example.rickmortyapp.ui.fragments.characters

import android.os.Bundle
import android.util.Log
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
import com.example.rickmortyapp.Utils.checkInternetConnection
import com.example.rickmortyapp.data.models.characters_data_classes.CharacterResult
import com.example.rickmortyapp.databinding.FragmentCharacterBinding
import com.example.rickmortyapp.ui.adapters.CharacterAdapter
import com.example.rickmortyapp.ui.adapters.OnCharacterItemClickListener

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

        if (viewModel.charactersList.isEmpty()) {
            viewModel.selectDataSource(checkConnection())
            viewModel.getCharacterResponse(counterPages)
        }

        characterAdapter = CharacterAdapter(this)

        binding.apply {

            rvCharacters.apply {
                adapter = characterAdapter
                layoutManager = GridLayoutManager(requireContext(), 2)
                setHasFixedSize(true)
                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int
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
                Log.d("TAG", "characterLD.observe $it ")
                setDataToAdapter(it)
            }
        }

    }

    private fun setDataToAdapter(list: List<CharacterResult>) {
        viewModel.checkCharacterListIsContainsData(list)
        characterAdapter.submitList(viewModel.charactersList)
        characterAdapter.notifyDataSetChanged()
        binding.progressBar.visibility = View.INVISIBLE
        isScrollEnded = false
    }

    private fun checkCounterPages(counterPages: Int): Boolean {
        Log.d("TAG", "checkCounterPages RUN!!")
        return counterPages <= allPagesNumber
    }

    private fun checkConnection():Boolean{
        return checkInternetConnection(requireActivity() as AppCompatActivity)
    }

    private fun scrollIsEnded(){
        isScrollEnded = true
        Log.d("TAG", "recyclerView.canScrollVertically RUN")
        if (checkConnection()) {
            if (viewModel.entityCounterPages > counterPages) counterPages = viewModel.entityCounterPages
            counterPages += 1
            Log.d("TAG", "Next counterPages= $counterPages")
            if (checkCounterPages(counterPages)) {
                binding.progressBar.visibility = View.VISIBLE
                viewModel.getCharacterResponse(counterPages)
            } else {
                Toast.makeText(
                    requireContext(),
                    "This is all data that could be downloaded",
                    Toast.LENGTH_SHORT
                ).show()
                counterPages -= 1
                Log.d("TAG", "False СounterPages= $counterPages")
            }
        } else {
            viewModel.selectDataSource(checkConnection())
            Log.d("TAG", "False checkInternetConnection")
            Toast.makeText(
                requireContext(),
                "False checkInternetConnection",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onItemClick(result: CharacterResult) {
        Toast.makeText(requireContext(), "Item Clicked", Toast.LENGTH_LONG).show()
    }
}


