package com.example.yandexcupa.utils

fun Int.fromSeekProgressToFloat(): Float {
    return when (this) {
        0 -> 0.25f
        1 -> 0.5f
        2 -> 1f
        3 -> 1.5f
        4 -> 2f
        else -> {
            1f
        }
    }
}


fun Float.fromSeekProgressToFloat(): Int {
    return when (this) {
        0.25f -> 0
        0.5f -> 1
        1f -> 2
        1.5f -> 3
        2f -> 4
        else -> {
            2
        }
    }
}