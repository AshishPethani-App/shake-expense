package com.shakeexpense.app.sensor

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

class ShakeDetector(private val onShake: () -> Unit) : SensorEventListener {

    companion object {
        private const val SHAKE_THRESHOLD_LOW = 10f
        private const val SHAKE_THRESHOLD_MEDIUM = 13f
        private const val SHAKE_THRESHOLD_HIGH = 17f
        private const val SHAKE_SLOP_TIME_MS = 500L
        private const val SHAKE_COUNT_RESET_TIME_MS = 3000L
        private const val SHAKE_MIN_COUNT = 2
        private const val COOLDOWN_MS = 3000L
    }

    private var sensitivity = "MEDIUM"
    private var shakeCount = 0
    private var lastShakeTime = 0L
    private var firstShakeTime = 0L
    private var lastCooldownTime = 0L

    fun setSensitivity(s: String) { sensitivity = s }

    private fun getThreshold() = when (sensitivity) {
        "LOW" -> SHAKE_THRESHOLD_HIGH
        "HIGH" -> SHAKE_THRESHOLD_LOW
        else -> SHAKE_THRESHOLD_MEDIUM
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type != Sensor.TYPE_ACCELEROMETER) return
        val now = System.currentTimeMillis()
        if (now - lastCooldownTime < COOLDOWN_MS) return

        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]
        val acceleration = sqrt(x * x + y * y + z * z)

        if (acceleration < getThreshold()) return

        if (now - firstShakeTime > SHAKE_COUNT_RESET_TIME_MS) {
            shakeCount = 0
            firstShakeTime = now
        }
        if (now - lastShakeTime < SHAKE_SLOP_TIME_MS) return

        lastShakeTime = now
        shakeCount++

        if (shakeCount >= SHAKE_MIN_COUNT) {
            shakeCount = 0
            lastCooldownTime = now
            onShake()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
}
