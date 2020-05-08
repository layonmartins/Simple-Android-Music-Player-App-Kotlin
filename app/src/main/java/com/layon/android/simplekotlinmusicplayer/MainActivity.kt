package com.layon.android.simplekotlinmusicplayer

import android.annotation.SuppressLint
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.ImageButton
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var mp: MediaPlayer
    private var totalTime: Int = 0
    private lateinit var playBtn: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        playBtn = findViewById(R.id.palyBtn)

        mp = MediaPlayer.create(this, R.raw.music)
        mp.apply{
            isLooping = true
            setVolume(.05f, 0.5f)
            totalTime = duration
        }

        // volume bar
        volumeBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    var volumeNum = progress / 100.0f
                    mp.setVolume(volumeNum, volumeNum)
                }
                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }
                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                }
            }
        )

        // position bar
        positionBar.max = totalTime
        positionBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser) {
                        mp.seekTo(progress)
                    }
                }
                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }
                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                }

            }
        )

        fun createTimeLabel(time: Int): String {
            var timeLabel = ""
            var min = time / 1000 / 60
            var sec = time / 1000 % 60

            timeLabel = "$min:"
            if (sec < 10) timeLabel += "0"
            timeLabel += sec

            return timeLabel
        }

        @SuppressLint("HandlerLeak")
        var handler = object : Handler() {
            override fun handleMessage(msg: Message) {
                var currentPosition = msg.what

                //Update positionBar
                positionBar.progress = currentPosition

                //Update Labels
                var elapsedTime = createTimeLabel(currentPosition)
                elapsedTimeLabel.text = elapsedTime

                var remainingTime = createTimeLabel(totalTime - currentPosition)
                remainingTimeLabel.text = "-$remainingTime"
            }
        }

        // Thread
        Thread(Runnable {
            while (mp != null) {
                try {
                    var msg = Message()
                    msg.what = mp.currentPosition
                    handler.sendMessage(msg)
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                }
            }
        }).start()
    }

    fun playBtnClick(v: View){
        if (mp.isPlaying) {
            // Stop
            mp.pause()
            playBtn.setImageResource(R.drawable.ic_play)
        } else {
            mp.start()
            playBtn.setImageResource(R.drawable.ic_pause)
        }
    }
}
