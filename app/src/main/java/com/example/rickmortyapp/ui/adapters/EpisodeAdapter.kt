package com.example.rickmortyapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.rickmortyapp.R
import com.example.rickmortyapp.data.models.episodes_data_classes.EpisodeResult
import com.example.rickmortyapp.databinding.ItemEpisodeBinding

class EpisodeAdapter(private val listener: OnEpisodeItemClickListener) :
    ListAdapter<EpisodeResult, EpisodeAdapter.EpisodeViewHolder>(DiffCallback()) {

    inner class EpisodeViewHolder(private val binding: ItemEpisodeBinding) :
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

        fun bind(result: EpisodeResult) {
            binding.apply {

                Glide.with(itemView)
                    .load(R.drawable.episode_image)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.splash_image)
                    .into(ivLocation)

                tvName.text = result.name
                tvName.isSelected = true
                tvAirDate.text = result.airDate
                tvAirDate.isSelected = true
                tvEpisode.text = result.episode
            }
        }

    }


    class DiffCallback : DiffUtil.ItemCallback<EpisodeResult>() {
        override fun areItemsTheSame(oldItem: EpisodeResult, newItem: EpisodeResult) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: EpisodeResult, newItem: EpisodeResult) =
            oldItem == newItem

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeViewHolder {
        val binding = ItemEpisodeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EpisodeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EpisodeViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }
}

interface OnEpisodeItemClickListener {
    fun onItemClick(result: EpisodeResult)

}