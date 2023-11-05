package com.example.yandexcupa.recorder

import java.io.File

interface AudioRecord {
    fun start(outputFile: File)
    fun stop()
}