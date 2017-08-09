package xyz.pgsideris.pomodoro

import android.content.Context
import android.os.Bundle
import android.os.SystemClock
import android.support.wearable.activity.WearableActivity
import android.text.format.DateFormat
import android.widget.Chronometer
import kotlinx.android.synthetic.main.activity_main.*
import android.os.Vibrator
import android.util.Log


class MainActivity : WearableActivity() {
    var timerRunning = false
    var timeWhenStopped: Long = 0
    val minutes = 25

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Enables Always-on
        setAmbientEnabled()
        this.chronos.onChronometerTickListener = Chronometer.OnChronometerTickListener {
            cArg ->
                val t = SystemClock.elapsedRealtime() - cArg.base
                val slices = getTimeFromMillis(t)
                cArg.text = DateFormat.format("mm:ss", t)
                if (slices[1] == minutes && slices[2] == 0) {
                    timerPause()
                    vibrate()
                }
        }

        this.container.setOnClickListener {
            if(timerRunning)    timerPause()
            else                timerStart()
            timerRunning = !timerRunning
        }
        this.container.setOnLongClickListener {
            timerPause()
            timerReset()
            true
        }
    }

    fun timerStart() {
        this.chronos.base = SystemClock.elapsedRealtime() + timeWhenStopped
        this.chronos.start()
    }

    fun timerPause() {
        this.chronos.stop()
        timeWhenStopped = this.chronos.base - SystemClock.elapsedRealtime()
    }

    fun timerReset() {
        this.chronos.base = SystemClock.elapsedRealtime()
        this.chronos.text = "00:00"
        timeWhenStopped = 0
        timerRunning = false
    }

    fun getTimeFromMillis(millis: Long): IntArray {
        val hours = millis / 3600000
        val minutes = (millis - hours * 3600000) / 60000
        val seconds = (millis - hours * 3600000 - minutes * 60000) / 1000
        val results: IntArray = intArrayOf(hours.toInt(), minutes.toInt(), seconds.toInt())
        return results
    }

    fun vibrate() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val vibrationPattern = longArrayOf(0, 500, 50, 300)
        //-1 - don't repeat
        val indexInPatternToRepeat = -1
        vibrator.vibrate(vibrationPattern, indexInPatternToRepeat)
    }


}
