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
import android.view.KeyEvent
import android.view.View
import android.view.animation.*
import java.util.concurrent.TimeUnit


class MainActivity : WearableActivity() {
    var timerRunning: Boolean = false
    var timeWhenStopped: Long = 0
    var minutes: Int = 25

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Enables Always-on
        setAmbientEnabled()
        this.durationDisplay.text = minutesToFormat(minutes)
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
        }
        this.container.setOnLongClickListener {
            timerPause()
            timerReset()
            true
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_NAVIGATE_NEXT -> {
                minutes += 5
                return true
            }
            KeyEvent.KEYCODE_NAVIGATE_PREVIOUS -> {
                if(minutes > 5) {
                    minutes -= 5
                    return true
                }
                return false

            }
        }
        this.durationDisplay.text = minutesToFormat(minutes)
        return super.onKeyDown(keyCode, event)
    }

    fun timerStart() {
        timerRunning = true
        this.chronos.base = SystemClock.elapsedRealtime() + timeWhenStopped
        this.chronos.start()
        this.durationDisplay.visibility = View.VISIBLE
        replace(0, -20)
    }

    fun timerPause() {
        timerRunning = false
        this.chronos.stop()
        timeWhenStopped = this.chronos.base - SystemClock.elapsedRealtime()
    }

    fun timerReset() {
        this.chronos.base = SystemClock.elapsedRealtime()
        this.chronos.text = "00:00"
        timeWhenStopped = 0
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

    fun minutesToFormat(minutes: Int) : String {
        val time = TimeUnit.MINUTES.toMillis(minutes.toLong())
        return DateFormat.format("mm:ss", time).toString()
    }

    fun replace(xTo: Int, yTo: Int) {
        // create set of animations
        val replaceAnimation = AnimationSet(false)
        replaceAnimation.interpolator = DecelerateInterpolator()
        // animations should be applied on the finish line
        replaceAnimation.fillAfter = true

        val translation = TranslateAnimation(0, 0f, TranslateAnimation.ABSOLUTE, (xTo - this.durationDisplay.left).toFloat(),
                0, 0f, TranslateAnimation.ABSOLUTE, (yTo - this.durationDisplay.right).toFloat())
        translation.duration = 1000
        val alpha = AlphaAnimation(1f, 0f)
        alpha.duration = 500
        replaceAnimation.addAnimation(translation)
        replaceAnimation.addAnimation(alpha)
        this.durationDisplay.animation = replaceAnimation
        this.durationDisplay.animate()
    }


}
