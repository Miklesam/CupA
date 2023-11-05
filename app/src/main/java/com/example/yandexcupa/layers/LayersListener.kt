package com.example.yandexcupa.layers

interface LayersListener {
    fun onDelete(position: Int)
    fun onChangeVolume(position: Int)
    fun onChangePlayback(position: Int)
    fun onActiveChange(position: Int)
}