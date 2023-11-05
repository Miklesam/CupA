package com.example.yandexcupa.layers

import android.content.Context
import androidx.annotation.DimenRes
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.yandexcupa.R
import com.google.android.material.divider.MaterialDividerItemDecoration

fun verticalItemDecoration(
    context: Context,
    @DimenRes thicknessId: Int,
) = itemDecoration(
    context,
    DividerItemDecoration.VERTICAL,
    thicknessId
)

internal fun itemDecoration(
    context: Context,
    orientation: Int,
    @DimenRes thicknessId: Int,
) = MaterialDividerItemDecoration(context, orientation).apply {
    setDividerThicknessResource(context, thicknessId)
    setDividerColorResource(context, R.color.transparent)
}