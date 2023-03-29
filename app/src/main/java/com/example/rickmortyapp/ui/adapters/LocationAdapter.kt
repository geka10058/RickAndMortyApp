package com.example.rickmortyapp.ui.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.rickmortyapp.R
import com.example.rickmortyapp.data.models.locations_data_classes.LocationResult
import com.example.rickmortyapp.databinding.ItemLocationBinding

class LocationAdapter(private val listener: OnLocationItemClickListener) :
    ListAdapter<LocationResult, LocationAdapter.LocationViewHolder>(DiffCallback()) {

    inner class LocationViewHolder(private val binding: ItemLocationBinding):
    RecyclerView.ViewHolder(binding.root){

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

        fun bind(result: LocationResult) {
            binding.apply {

                Glide.with(itemView)
                    .load(R.drawable.location_image)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.splash_image)
                    .into(ivLocation)

                tvName.text = result.name
                tvName.isSelected = true
                tvDimension.text = result.dimension
                tvDimension.isSelected = true
                tvType.text = result.type
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<LocationResult>() {
        override fun areItemsTheSame(oldItem: LocationResult, newItem: LocationResult) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: LocationResult, newItem: LocationResult) =
            oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val binding =
            ItemLocationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LocationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }
}

interface OnLocationItemClickListener {
    fun onItemClick(result: LocationResult)
}
