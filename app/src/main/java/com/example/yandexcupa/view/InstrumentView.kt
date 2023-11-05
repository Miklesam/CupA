package com.example.yandexcupa.view

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.yandexcupa.R
import com.example.yandexcupa.databinding.InstrumentLayoutBinding


class InstrumentView : ConstraintLayout {

    private val binding = InstrumentLayoutBinding.inflate(LayoutInflater.from(context), this)
    private var expanded: Boolean = false
    private var currentIndex: Int = 0

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        background = context.getDrawable(R.drawable.rounded_white)
    }

    override fun onSaveInstanceState(): Parcelable {
        val bundle = Bundle()
        bundle.putParcelable("superState", super.onSaveInstanceState())
        bundle.putBoolean("expanded", this.expanded)
        bundle.putInt("index", this.currentIndex)
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        var viewState = state
        if (viewState is Bundle) {
            expanded = viewState.getBoolean("expanded", false)
            currentIndex = viewState.getInt("index", 0)
            if (expanded) setExpand(currentIndex) else setCollapsed()
            viewState = viewState.getParcelable("superState")
        }
        super.onRestoreInstanceState(viewState)
    }


    fun setExpand(index: Int) {
        expanded = true
        currentIndex = index
        binding.sample1.visibility = VISIBLE
        binding.sample2.visibility = VISIBLE
        binding.sample3.visibility = VISIBLE
        binding.paddingView.visibility = VISIBLE
        when (index) {
            0 -> {
                binding.sample1.background = context.getDrawable(R.drawable.rounded_white_4dp)
                binding.sample2.background = null
                binding.sample3.background = null
            }
            1 -> {
                binding.sample2.background = context.getDrawable(R.drawable.rounded_white_4dp)
                binding.sample1.background = null
                binding.sample3.background = null
            }
            2 -> {
                binding.sample3.background = context.getDrawable(R.drawable.rounded_white_4dp)
                binding.sample1.background = null
                binding.sample2.background = null
            }
        }
        binding.instIcon.background = context.getDrawable(R.drawable.rounded_green)
        background = context.getDrawable(R.drawable.rounded_green)
    }

    fun setCollapsed() {
        expanded = false
        binding.sample1.visibility = GONE
        binding.sample2.visibility = GONE
        binding.sample3.visibility = GONE
        binding.paddingView.visibility = GONE
        binding.instIcon.background = context.getDrawable(R.drawable.rounded_white)
        background = context.getDrawable(R.drawable.rounded_white)
    }


}