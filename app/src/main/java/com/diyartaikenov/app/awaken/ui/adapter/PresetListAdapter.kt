package com.diyartaikenov.app.awaken.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.diyartaikenov.app.awaken.databinding.ListItemPresetBinding
import com.diyartaikenov.app.awaken.model.MeditationPreset

/**
 * ListAdapter for the [MeditationPreset]s retrieved from the database.
 */
class PresetListAdapter(
    private val startSessionClickListener: (MeditationPreset) -> Unit,
    private val editPresetClickListener: (MeditationPreset) -> Unit,
    private val deletePresetClickListener: (MeditationPreset) -> Unit
): ListAdapter<MeditationPreset, PresetListAdapter.PresetViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PresetViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return PresetViewHolder(
            ListItemPresetBinding.inflate(layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PresetViewHolder, position: Int) {
        val preset = getItem(position)
        holder.setStartSessionClickListener(preset) { startSessionClickListener(preset) }
        holder.setEditPresetClickListener(preset) { editPresetClickListener(preset) }
        holder.setDeletePresetClickListener(preset) { deletePresetClickListener(preset) }
        holder.bind(preset)
    }

    class PresetViewHolder(
        private var binding: ListItemPresetBinding
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(preset: MeditationPreset) {
            binding.preset = preset
            binding.executePendingBindings()
        }

        fun setStartSessionClickListener(
            preset: MeditationPreset,
            clickListener: (MeditationPreset) -> Unit) {
            itemView.setOnClickListener { clickListener(preset) }
            binding.meditate.setOnClickListener { clickListener(preset) }
        }

        fun setEditPresetClickListener(
            preset: MeditationPreset,
            clickListener: (MeditationPreset) -> Unit) {
            binding.editPresetButton.setOnClickListener { clickListener(preset) }
        }

        fun setDeletePresetClickListener(
            preset: MeditationPreset,
            clickListener: (MeditationPreset) -> Unit) {
            binding.deletePresetButton.setOnClickListener { clickListener(preset) }
        }
    }

    companion object DiffCallback: DiffUtil.ItemCallback<MeditationPreset>() {
        override fun areItemsTheSame(
            oldItem: MeditationPreset,
            newItem: MeditationPreset
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: MeditationPreset,
            newItem: MeditationPreset
        ): Boolean {
            return oldItem == newItem
        }
    }
}