package com.example.yandexcupa.layers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.yandexcupa.R
import com.example.yandexcupa.models.SampleModel

class LayersAdapter(val layersListener: LayersListener) :
    ListAdapter<SampleModel, LayerViewHolder>(CALLBACK) {

    companion object {
        private val CALLBACK = object : DiffUtil.ItemCallback<SampleModel>() {
            override fun areItemsTheSame(oldItem: SampleModel, newItem: SampleModel): Boolean {
                return oldItem.name == newItem.name && oldItem.seq == newItem.seq
            }

            override fun areContentsTheSame(oldItem: SampleModel, newItem: SampleModel): Boolean {
                return oldItem == newItem
            }

            override fun getChangePayload(oldItem: SampleModel, newItem: SampleModel): Any? {
                return oldItem.volume != newItem.volume ||
                        oldItem.activeSample != newItem.activeSample
            }
        }
    }

    override fun submitList(list: List<SampleModel>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LayerViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.layer_layout, parent, false)
        return LayerViewHolder(itemView, layersListener)
    }

    override fun onBindViewHolder(holder: LayerViewHolder, position: Int) {
        val sample = currentList[position]
        val text = sample.name + " ${sample.seq}"
        holder.layerName.text = text
        val backGround =
            if (sample.activeSample) R.drawable.rounded_green_4dp else R.drawable.rounded_white_4dp
        holder.contaimer.background = holder.contaimer.context.getDrawable(backGround)
        val volumeIcon = if (sample.volume > 0) R.drawable.volume_on else R.drawable.volume_off
        holder.changeVolume.setImageDrawable(holder.changeVolume.context.getDrawable(volumeIcon))
        val playbackIcon = if (sample.isPlaying) R.drawable.pause else R.drawable.play
        holder.changePlayback.setImageDrawable(holder.changeVolume.context.getDrawable(playbackIcon))
    }


}