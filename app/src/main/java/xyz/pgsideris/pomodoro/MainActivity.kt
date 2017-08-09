package xyz.pgsideris.pomodoro

import android.content.Context
import android.os.Bundle
import android.os.SystemClock
import android.support.wearable.activity.WearableActivity
import android.text.format.DateFormat
import android.widget.Chronometer
import kotlinx.android.synthetic.main.activity_main.*
import android.os.Vibrator



class MainActivity : WearableActivity() {
    var timerRunning = false
    var timeWhenStopped = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Enables Always-on
        setAmbientEnabled()
        this.chronos.onChronometerTickListener = Chronometer.OnChronometerTickListener {
            cArg ->
                cArg.format = "mm:ss"
                val t = SystemClock.elapsedRealtime() - cArg.base
                cArg.text = DateFormat.format("mm:ss", t)
        }

        this.container.setOnClickListener {
            if(timerRunning)    timerPause()
            else                timerStart()
            timerRunning = !timerRunning
        }
    }

    fun timerStart() {
        this.chronos.base = SystemClock.elapsedRealtime() + timeWhenStopped;
        this.chronos.start()
    }

    fun timerPause() {
        this.chronos.stop()
        timeWhenStopped = (this.chronos.base - SystemClock.elapsedRealtime()).toInt()
    }

    fun timerReset() {
        this.chronos.base = SystemClock.elapsedRealtime();
        timeWhenStopped = 0
    }

    fun vibrate() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val vibrationPattern = longArrayOf(0, 500, 50, 300)
        //-1 - don't repeat
        val indexInPatternToRepeat = -1
        vibrator.vibrate(vibrationPattern, indexInPatternToRepeat)
    }


}
