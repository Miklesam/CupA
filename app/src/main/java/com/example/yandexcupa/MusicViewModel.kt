package com.example.yandexcupa

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.yandexcupa.models.BottomState
import com.example.yandexcupa.models.Sample
import com.example.yandexcupa.models.SampleModel
import com.example.yandexcupa.recorder.MyAudioRecorder
import com.example.yandexcupa.utils.fromSeekProgressToFloat
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.MediaSourceFactory
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.example.yandexcupa.utils.SingleLiveEvent
import java.io.File

class MusicViewModel(application: Application) : AndroidViewModel(application) {

    private val point = 4

    val context = getApplication<Application>().applicationContext

    private val recorder by lazy {
        MyAudioRecorder(context)
    }
    var currentGuitar = 0
    var currentDrums = 0
    var currentTrumpert = 0
    private val _layerList =
        MutableLiveData<ArrayList<SampleModel>>(ArrayList())
    val layerList: LiveData<ArrayList<SampleModel>> = _layerList

    private val _botomState = MutableLiveData(BottomState())
    val botomState: LiveData<BottomState> = _botomState

    val sendAudio: SingleLiveEvent<Unit> = SingleLiveEvent()

    private val players = mutableMapOf<String, SimpleExoPlayer>()

    fun addLayer(sample: Sample) {
        val samplesList = _layerList.value
        val seq = samplesList?.filter { it.name == sample.name }?.size ?: 0
        val sampleModel = SampleModel(sample.file, sample.name, seq)
        createPlayerForLayer(sampleModel, sample is Sample.Voice)
        val previousActive = samplesList?.find { it.activeSample }
        samplesList?.remove(previousActive)
        previousActive?.let {
            samplesList.add(previousActive.copy(activeSample = false))
        }
        samplesList?.add(sampleModel)
        _layerList.value = samplesList
    }

    fun deleteLayer(sample: SampleModel) {
        val string = sample.name + sample.seq
        players[string]?.release()
        players.remove(string)
        val samplesList = _layerList.value
        samplesList?.let {
            samplesList.remove(sample)
            if (samplesList.isEmpty()) _botomState.value =
                _botomState.value?.copy(layersOpen = false)
            _layerList.value = samplesList
        }

    }

    fun changeVolume(sample: SampleModel) {
        val string = sample.name + sample.seq
        val newVolume = if (sample.volume > 0) {
            0f
        } else {
            0.5f
        }
        players[string]?.volume = newVolume
        val sampleList = _layerList.value
        val sample = sampleList?.find { (it.seq == sample.seq && it.name == sample.name) }
        sample?.let {
            val position = sampleList.indexOf(sample)
            sampleList.remove(sample)
            sampleList.add(position, sample.copy(volume = newVolume))
        }
        _layerList.value = sampleList
    }

    fun changePlayback(sample: SampleModel) {
        val string = sample.name + sample.seq
        val isPlaying = sample.isPlaying.not()
        if (isPlaying) {
            players[string]?.play()
        } else {
            players[string]?.pause()
        }
        val sampleList = _layerList.value
        val sample = sampleList?.find { (it.seq == sample.seq && it.name == sample.name) }
        sample?.let {
            val position = sampleList.indexOf(sample)
            sampleList.remove(sample)
            sampleList.add(position, sample.copy(isPlaying = isPlaying))
        }
        _layerList.value = sampleList
    }

    fun changeActiveSample(sample: SampleModel) {
        val sampleList = _layerList.value
        val currentActive = sampleList?.find { it.activeSample }
        if (currentActive == sample) return
        currentActive?.let {
            val position = sampleList.indexOf(currentActive)
            sampleList.remove(currentActive)
            sampleList.add(position, currentActive.copy(activeSample = false))
        }
        val newActive = sampleList?.find { it.name == sample.name && it.seq == sample.seq }
        newActive?.let {
            val position = sampleList.indexOf(newActive)
            sampleList.remove(newActive)
            sampleList.add(position, newActive.copy(activeSample = true))
        }
        _layerList.value = sampleList
    }


    private fun createPlayerForLayer(sample: SampleModel, voice: Boolean) {
        val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(
            context, Util.getUserAgent(
                context,
                context.getString(R.string.app_name)
            )
        )

        val mediaSourceFactory: MediaSourceFactory = DefaultMediaSourceFactory(dataSourceFactory)


        val player =
            SimpleExoPlayer.Builder(context).setMediaSourceFactory(mediaSourceFactory).build()
        val contentUri = if (voice) {
            val file = File(context.cacheDir, "voice_${sample.seq}.mp3")
            Uri.parse(file.absolutePath)
        } else {
            Uri.parse("asset:///${sample.file}")
        }

        val mediaItem = MediaItem.Builder().setUri(contentUri).build()

        player.setMediaItem(mediaItem)

        player.playWhenReady = true
        player.repeatMode = ExoPlayer.REPEAT_MODE_ONE
        player.volume = 0.5f
        player.prepare()
        val string = sample.name + sample.seq
        players[string] = player
    }

    fun changeLayersState() {
        val currentState = _botomState.value?.layersOpen
        currentState?.let {
            val newState = _botomState.value?.copy(layersOpen = currentState.not())
            _botomState.value = newState
        }
    }

    fun changePlaybackSpeed(progress: Int) {
        val floatSpeed = progress.fromSeekProgressToFloat()
        val activeSample = _layerList.value?.find { it.activeSample } ?: return
        val hash = activeSample.name + activeSample.seq
        //val floatSpeed = maxOf(0.1f, (progress / 10).toFloat())
        players[hash]?.setPlaybackSpeed(floatSpeed)
    }

    fun changeVolume(progress: Int) {
        val floatVolume = maxOf(0f, (progress.toFloat() / 10))
        val activeSample = _layerList.value?.find { it.activeSample } ?: return
        val hash = activeSample.name + activeSample.seq
        players[hash]?.volume = floatVolume
    }

    fun voiceBoxClicked() {
        val currentState = _botomState.value?.voiceRecoding
        if (currentState!!.not()) {
            startRecording()
        } else {
            stopRecording()
        }
        currentState?.let {
            val newState = _botomState.value?.copy(voiceRecoding = currentState.not())
            _botomState.value = newState
        }
    }

    private fun startRecording() {
        val seq = _layerList.value?.filter { it.name == "Голос" }?.size
        File(context.cacheDir, "voice_$seq.mp3").also {
            recorder.start(it)
        }
    }

    private fun stopRecording() {
        recorder.stop()
        val seq = _layerList.value?.filter { it.name == "Голос" }?.size
        addLayer(Sample.Voice("voice_$seq.mp3"))
    }

    fun addGuitarLayer() {
        val sample = when (currentGuitar) {
            0 -> Sample.Guitar1
            1 -> Sample.Guitar2
            2 -> Sample.Guitar3
            else -> {
                Sample.Guitar1
            }
        }
        addLayer(sample)
    }

    fun addDrumsLayer() {
        val sample = when (currentDrums) {
            0 -> Sample.Drums1
            1 -> Sample.Drums2
            2 -> Sample.Drums3
            else -> {
                Sample.Drums1
            }
        }
        addLayer(sample)
    }

    fun addTrumpertLayer() {
        val sample = when (currentTrumpert) {
            0 -> Sample.Trumper1
            1 -> Sample.Trumper2
            2 -> Sample.Trumper3
            else -> {
                Sample.Trumper1
            }
        }
        addLayer(sample)
    }

    fun changeCurrentGuitar(position: Int) {
        currentGuitar = position
        addGuitarLayer()
    }

    fun changeCurrentDrums(position: Int) {
        currentDrums = position
        addDrumsLayer()
    }

    fun changeCurrentTrumpert(position: Int) {
        currentTrumpert = position
        addTrumpertLayer()
    }

    fun playAllCliced() {
        val currentState = _botomState.value?.isAllPlaying
        if (currentState!!.not()) {
            players.forEach {
                it.value.seekTo(0)
                it.value.play()
            }
        } else {
            players.forEach {
                it.value.seekTo(0)
                it.value.pause()
            }
        }
        currentState?.let {
            val newState = _botomState.value?.copy(isAllPlaying = currentState.not())
            _botomState.value = newState
        }
    }

    fun recordClicked() {
        val currentState = _botomState.value?.isRecording
        if (currentState!!.not()) {
            File(context.filesDir, FILE_NAME).also {
                recorder.start(it)
            }
        } else {
            recorder.stop()
            players.forEach {
                it.value.pause()
            }
            sendAudio.value = Unit
        }
        currentState?.let {
            val newState = _botomState.value?.copy(isRecording = currentState.not())
            _botomState.value = newState
        }
    }

    companion object {
        const val FILE_NAME = "result.mp3"
    }

}