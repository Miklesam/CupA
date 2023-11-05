package com.example.yandexcupa.models

data class SampleModel(
    val file: String,
    val name: String,
    val seq: Int,
    val volume: Float = 0.5f,
    val playbackSpeed: Float = 1f,
    val isPlaying: Boolean = true,
    val activeSample: Boolean = true
)
