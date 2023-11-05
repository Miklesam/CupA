package com.example.yandexcupa.layers

import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.yandexcupa.R

class LayerViewHolder(itemView: View, var layersListener: LayersListener) :
    RecyclerView.ViewHolder(itemView), View.OnClickListener {
    val layerName: TextView
    val delete: FrameLayout
    val contaimer: ConstraintLayout
    val changeVolume: AppCompatImageView
    val changePlayback: AppCompatImageView

    init {
        layerName = itemView.findViewById(R.id.layer_name)
        delete = itemView.findViewById(R.id.delete_box)
        contaimer = itemView.findViewById(R.id.container)
        changeVolume = itemView.findViewById(R.id.change_volume)
        changePlayback = itemView.findViewById(R.id.playback)
        contaimer.setOnClickListener(this)
        changePlayback.setOnClickListener(this)
        changeVolume.setOnClickListener(this)
        delete.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            delete -> layersListener.onDelete(adapterPosition)
            changeVolume -> layersListener.onChangeVolume(adapterPosition)
            changePlayback -> layersListener.onChangePlayback(adapterPosition)
            contaimer -> layersListener.onActiveChange(adapterPosition)
        }

    }
}