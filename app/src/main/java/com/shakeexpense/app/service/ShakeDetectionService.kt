package com.shakeexpense.app.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.shakeexpense.app.MainActivity
import com.shakeexpense.app.R
import com.shakeexpense.app.data.repository.SettingsRepository
import com.shakeexpense.app.sensor.ShakeDetector
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@AndroidEntryPoint
class ShakeDetectionService : Service() {

    @Inject lateinit var settingsRepository: SettingsRepository

    private lateinit var sensorManager: SensorManager
    private lateinit var shakeDetector: ShakeDetector
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    companion object {
        const val CHANNEL_ID = "shake_expense_channel"
        const val NOTIFICATION_ID = 1001
        const val ACTION_OPEN_EXPENSE = "com.shakeexpense.app.OPEN_EXPENSE"

        fun start(context: Context) {
            val intent = Intent(context, ShakeDetectionService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                context.startForegroundService(intent)
            else context.startService(intent)
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, ShakeDetectionService::class.java))
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        shakeDetector = ShakeDetector { onShakeDetected() }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, buildNotification())
        scope.launch {
            val sensitivity = settingsRepository.sensitivity.first()
            shakeDetector.setSensitivity(sensitivity)
        }
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(shakeDetector, accelerometer,
            SensorManager.SENSOR_DELAY_GAME)
        return START_STICKY
    }

    private fun onShakeDetected() {
        val intent = Intent(this, MainActivity::class.java).apply {
            action = ACTION_OPEN_EXPENSE
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        startActivity(intent)
    }

    override fun onDestroy() {
        sensorManager.unregisterListener(shakeDetector)
        scope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun buildNotification(): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Shake Expense is active")
            .setContentText("Shake your phone to quickly add an expense.")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, "Shake Expense",
                NotificationManager.IMPORTANCE_LOW
            ).apply { description = "Shake detection service" }
            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }
    }
}
