package com.example.rickmortyapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.rickmortyapp.data.models.characters_data_classes.CharacterResult
import com.example.rickmortyapp.R
import com.example.rickmortyapp.databinding.ItemCharacterBinding

class CharacterAdapter(private val listener: OnResultItemClickListener) :
    ListAdapter<CharacterResult, CharacterAdapter.ResultViewHolder>(DiffCallback()) {

    inner class ResultViewHolder(private val binding: ItemCharacterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val result = getItem(position)
                        if (result != null) listener.onItemClick(result)
                    }
                }
            }
        }

        fun bind(result: CharacterResult) {
            binding.apply {

                Glide.with(itemView)
                    .load(result.image)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.splash_image)
                    .into(binding.ivCharacter)

                tvName.text = result.name
                tvStatus.text = result.status
                tvSpecies.text = result.species
                tvGender.text = result.gender
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<CharacterResult>() {
        override fun areItemsTheSame(oldItem: CharacterResult, newItem: CharacterResult) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: CharacterResult, newItem: CharacterResult) =
            oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val binding =
            ItemCharacterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ResultViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }
}

interface OnResultItemClickListener {
    fun onItemClick(result: CharacterResult)
}