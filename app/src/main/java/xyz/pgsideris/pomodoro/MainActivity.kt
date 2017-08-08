package xyz.pgsideris.pomodoro

import android.os.Bundle
import android.os.SystemClock
import android.support.wearable.activity.WearableActivity
import android.text.format.DateFormat
import android.util.Log
import android.widget.Chronometer
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : WearableActivity() {
    var timerRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var timeWhenStopped = 0
        // Enables Always-on
        setAmbientEnabled()
        this.chronos.onChronometerTickListener = Chronometer.OnChronometerTickListener {
            cArg ->
                cArg.format = "mm:ss"
                val t = SystemClock.elapsedRealtime() - cArg.base
                cArg.text = DateFormat.format("mm:ss", t)
        }

        this.container.setOnClickListener {
            if(timerRunning){
                this.chronos.stop()
                timeWhenStopped = (this.chronos.base - SystemClock.elapsedRealtime()).toInt()
            } else {
                this.chronos.base = SystemClock.elapsedRealtime() + timeWhenStopped;
                this.chronos.start()
            }
            timerRunning = !timerRunning
        }
    }


}
