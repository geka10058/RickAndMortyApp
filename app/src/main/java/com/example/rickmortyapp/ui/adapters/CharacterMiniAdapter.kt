package com.example.rickmortyapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.rickmortyapp.data.json_models.characters_data_classes.CharacterResult
import com.example.rickmortyapp.R
import com.example.rickmortyapp.databinding.ItemCharacterMiniBinding

class CharacterMiniAdapter :
    ListAdapter<CharacterResult, CharacterMiniAdapter.ResultMiniViewHolder>(DiffCallback()) {

    inner class ResultMiniViewHolder(private val binding: ItemCharacterMiniBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(result: CharacterResult) {
            binding.apply {

                Glide.with(itemView)
                    .load(result.image)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.splash_image)
                    .into(ivCharacter)

                tvName.text = result.name
                tvName.isSelected = true
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<CharacterResult>() {
        override fun areItemsTheSame(oldItem: CharacterResult, newItem: CharacterResult) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: CharacterResult, newItem: CharacterResult) =
            oldItem == newItem
    }

    override fun onBindViewHolder(holder: ResultMiniViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CharacterMiniAdapter.ResultMiniViewHolder {
        val binding =
            ItemCharacterMiniBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ResultMiniViewHolder(binding)
    }
}