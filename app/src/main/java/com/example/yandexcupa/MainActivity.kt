package com.example.yandexcupa


import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yandexcupa.databinding.ActivityMainBinding
import com.example.yandexcupa.layers.LayersAdapter
import com.example.yandexcupa.layers.LayersListener
import com.example.yandexcupa.layers.verticalItemDecoration
import com.example.yandexcupa.utils.fromSeekProgressToFloat
import java.io.File


class MainActivity : AppCompatActivity(), LayersListener {

    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<MusicViewModel>()
    lateinit var layerAdapter: LayersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.RECORD_AUDIO), 0
        )


        layerAdapter = LayersAdapter(this)
        val layerManager = LinearLayoutManager(this)
        layerManager.stackFromEnd = true

        layerAdapter.registerAdapterDataObserver(object :
            RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                binding.layerRw.scrollToPosition(positionStart)
            }
        })
        binding.layerRw.apply {
            adapter = layerAdapter
            layoutManager = layerManager
            addItemDecoration(verticalItemDecoration(context, R.dimen.layers_divider))
        }
        viewModel.layerList.observe(this) {
            layerAdapter.submitList(it)
            val active = it.find { it.activeSample } ?: return@observe
            binding.playbackSpeed.progress = active.playbackSpeed.fromSeekProgressToFloat()
            binding.playerVolume.progress = (active.volume * 10).toInt()
        }

        viewModel.botomState.observe(this) { bottomState ->
            if (bottomState.layersOpen) {
                binding.layerRw.visibility = View.VISIBLE
                binding.layers.background = getDrawable(R.drawable.rounded_green_4dp)
                binding.layersArrow.setImageDrawable(getDrawable(R.drawable.arrow_down))
            } else {
                binding.layerRw.visibility = View.GONE
                binding.layers.background = getDrawable(R.drawable.rounded_white_4dp)
                binding.layersArrow.setImageDrawable(getDrawable(R.drawable.arrow_up))
            }

            if (bottomState.voiceRecoding) {
                binding.recordIcon.setImageDrawable(getDrawable(R.drawable.record_voice_on))
            } else {
                binding.recordIcon.setImageDrawable(getDrawable(R.drawable.record_voice))
            }

            if (bottomState.isAllPlaying) {
                binding.playIcon.setImageDrawable(getDrawable(R.drawable.pause))
            } else {
                binding.playIcon.setImageDrawable(getDrawable(R.drawable.play))
            }

            if (bottomState.isRecording) {
                binding.recordAudioIcon.setImageDrawable(getDrawable(R.drawable.record_red))
            } else {
                binding.recordAudioIcon.setImageDrawable(getDrawable(R.drawable.record))
            }
        }

        binding.layers.setOnClickListener {
            viewModel.changeLayersState()
        }

        binding.guitar.setOnClickListener {
            viewModel.addGuitarLayer()
            binding.guitar.setCollapsed()
        }
        binding.drums.setOnClickListener {
            viewModel.addDrumsLayer()
            binding.drums.setCollapsed()
        }
        val drumsIcon = binding.drums.findViewById<AppCompatImageView>(R.id.icon)
        drumsIcon.setImageDrawable(getDrawable(R.drawable.drums))
        val params = FrameLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.CENTER
        }
        drumsIcon.layoutParams = params
        binding.trumpert.setOnClickListener {
            viewModel.addTrumpertLayer()
            binding.trumpert.setCollapsed()
        }
        val trumperIcon = binding.trumpert.findViewById<AppCompatImageView>(R.id.icon)
        trumperIcon.setImageDrawable(getDrawable(R.drawable.trumpet))
        trumperIcon.layoutParams = params

        binding.playbackSpeed.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                val progress = seekBar?.progress ?: 0
                viewModel.changePlaybackSpeed(progress)
            }

        })

        binding.playerVolume.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                viewModel.changeVolume(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })

        binding.boxRecordVoice.setOnClickListener {
            viewModel.voiceBoxClicked()
        }

        binding.guitar.setOnLongClickListener {
            binding.guitar.setExpand(viewModel.currentGuitar)
            true
        }
        binding.drums.setOnLongClickListener {
            binding.drums.setExpand(viewModel.currentDrums)
            true
        }
        binding.trumpert.setOnLongClickListener {
            binding.trumpert.setExpand(viewModel.currentTrumpert)
            true
        }
        val sample1 = binding.guitar.findViewById<TextView>(R.id.sample_1)
        val sample2 = binding.guitar.findViewById<TextView>(R.id.sample_2)
        val sample3 = binding.guitar.findViewById<TextView>(R.id.sample_3)
        val guitarSample = listOf(sample1, sample2, sample3)
        guitarSample.forEachIndexed { index, textView ->
            textView.setOnClickListener {
                viewModel.changeCurrentGuitar(index)
                binding.guitar.setCollapsed()
            }
        }
        val sampleDrums1 = binding.drums.findViewById<TextView>(R.id.sample_1)
        val sampleDrums2 = binding.drums.findViewById<TextView>(R.id.sample_2)
        val sampleDrums3 = binding.drums.findViewById<TextView>(R.id.sample_3)
        val drumsSample = listOf(sampleDrums1, sampleDrums2, sampleDrums3)
        drumsSample.forEachIndexed { index, textView ->
            textView.setOnClickListener {
                viewModel.changeCurrentDrums(index)
                binding.drums.setCollapsed()
            }
        }

        val sampleTrumpert1 = binding.trumpert.findViewById<TextView>(R.id.sample_1)
        val sampleTrumpert2 = binding.trumpert.findViewById<TextView>(R.id.sample_2)
        val sampleTrumpert3 = binding.trumpert.findViewById<TextView>(R.id.sample_3)
        val trumpertSample = listOf(sampleTrumpert1, sampleTrumpert2, sampleTrumpert3)
        trumpertSample.forEachIndexed { index, textView ->
            textView.setOnClickListener {
                viewModel.changeCurrentTrumpert(index)
                binding.trumpert.setCollapsed()
            }
        }

        binding.boxPlay.setOnClickListener {
            viewModel.playAllCliced()
        }

        binding.boxRecord.setOnClickListener {
            viewModel.recordClicked()
        }

        viewModel.sendAudio.observe(this){
            val shareIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(
                    Intent.EXTRA_STREAM,
                    FileProvider.getUriForFile(
                        this@MainActivity,
                        this@MainActivity.packageName + ".provider",
                        File(this@MainActivity.filesDir, MusicViewModel.FILE_NAME)
                    )
                )
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                type = "audio/*"
            }
            this@MainActivity.startActivity(Intent.createChooser(shareIntent, null))
        }

    }

    override fun onDelete(position: Int) {
        val item = layerAdapter.currentList[position]
        viewModel.deleteLayer(item)
    }

    override fun onChangeVolume(position: Int) {
        val item = layerAdapter.currentList[position]
        viewModel.changeVolume(item)
    }

    override fun onChangePlayback(position: Int) {
        val item = layerAdapter.currentList[position]
        viewModel.changePlayback(item)
    }

    override fun onActiveChange(position: Int) {
        val item = layerAdapter.currentList[position]
        viewModel.changeActiveSample(item)
    }


}