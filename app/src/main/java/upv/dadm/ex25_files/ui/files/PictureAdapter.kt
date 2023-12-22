/*
 * Copyright (c) 2022-2023 Universitat Politècnica de València
 * Authors: David de Andrés and Juan Carlos Ruiz
 *          Fault-Tolerant Systems
 *          Instituto ITACA
 *          Universitat Politècnica de València
 *
 * Distributed under MIT license
 * (See accompanying file LICENSE.txt)
 */

package upv.dadm.ex25_files.ui.files

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import upv.dadm.ex25_files.R
import upv.dadm.ex25_files.databinding.ItemPictureBinding
import upv.dadm.ex25_files.model.Picture

class PictureAdapter : ListAdapter<Picture, PictureAdapter.PictureViewHolder>(PictureDiff) {

    /**
     * Computes the diff between two pictures in the array.
     */
    object PictureDiff : DiffUtil.ItemCallback<Picture>() {
        /**
         * Determines whether two pictures are the same
         * (let's assume that their URI can identify them).
         */
        override fun areItemsTheSame(oldItem: Picture, newItem: Picture): Boolean =
            oldItem.uri == newItem.uri

        /**
         * Determines whether two pictures have the same data.
         */
        override fun areContentsTheSame(oldItem: Picture, newItem: Picture): Boolean =
            oldItem == newItem

    }

    /**
     * Holds a reference to a ViewBinding.
     */
    class PictureViewHolder(
        private val binding: ItemPictureBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        /**
         * Fills the elements within the View with provided Picture object.
         */
        fun bind(picture: Picture) {
            // Glide gets an image from the storage space using a background thread and
            // updates the selected View when available
            Glide.with(binding.clPicture)
                .load(picture.uri)
                .placeholder(R.drawable.ic_no_image)
                .into(binding.ivPicture)
            binding.tvPicture.text = picture.name
        }
    }

    /**
     * Creates the ViewBinding and attaches it to a ViewHolder
     * to easily access all the elements within the View.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PictureViewHolder =
        PictureViewHolder(
            ItemPictureBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    /**
     * Fills the elements within the View with the required data from the array.
     */
    override fun onBindViewHolder(holder: PictureViewHolder, position: Int) =
        holder.bind(getItem(position))
}

