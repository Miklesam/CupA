package com.example.yandexcupa.models

sealed class Sample(val file: String, val name: String, val internalSeq: Int) {
    object Guitar1 : Sample("guitar_1.wav", "Гитара", 0)
    object Guitar2 : Sample("guitar_2.wav", "Гитара", 1)
    object Guitar3 : Sample("guitar_3.wav", "Гитара", 2)
    object Drums1 : Sample("drums_1.wav", "Барабаны", 0)
    object Drums2 : Sample("drums_2.wav", "Барабаны", 1)
    object Drums3 : Sample("drums_3.wav", "Барабаны", 2)
    object Trumper1 : Sample("flute_1.wav", "Труба", 0)
    object Trumper2 : Sample("flute_2.wav", "Труба", 1)
    object Trumper3 : Sample("flute_3.wav", "Труба", 2)
    data class Voice(val uri: String) : Sample(uri, "Голос", 0)
}
