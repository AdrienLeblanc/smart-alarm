package fr.celld.smartalarm.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import fr.celld.smartalarm.MainActivity
import fr.celld.smartalarm.data.local.AlarmDatabase
import fr.celld.smartalarm.data.model.Alarm
import fr.celld.smartalarm.ui.AlarmRingingActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * Service to manage the ringing alarm
 */
class AlarmService : Service() {

    private val channelId = "alarm_channel"
    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null
    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())
    private var currentAlarmId: Long = -1

    private val actionReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                ACTION_STOP_ALARM -> stopAlarm()
                ACTION_SNOOZE_ALARM -> {
                    val alarmId = intent.getLongExtra(AlarmScheduler.ALARM_ID_EXTRA, currentAlarmId)
                    if (alarmId != -1L) {
                        val scheduler = AlarmScheduler(applicationContext)
                        scheduler.scheduleSnooze(alarmId, 5)
                    }
                    stopAlarm()
                }
            }
        }
    }

    companion object {
        const val ACTION_STOP_ALARM = "fr.celld.smartalarm.STOP_ALARM"
        const val ACTION_SNOOZE_ALARM = "fr.celld.smartalarm.SNOOZE_ALARM"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        // Enregistrer le receiver pour les actions
        val filter = IntentFilter().apply {
            addAction(ACTION_STOP_ALARM)
            addAction(ACTION_SNOOZE_ALARM)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(actionReceiver, filter, RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(actionReceiver, filter)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val alarmId = intent?.getLongExtra(AlarmScheduler.ALARM_ID_EXTRA, -1) ?: -1

        if (alarmId == -1L) {
            Log.e("AlarmService", "Invalid alarm ID")
            stopSelf()
            return START_NOT_STICKY
        }

        // Load the alarm and start ringing
        serviceScope.launch {
            try {
                val database = AlarmDatabase.getDatabase(applicationContext)
                val alarm = database.alarmDao().getAlarmById(alarmId)

                if (alarm == null) {
                    Log.e("AlarmService", "Alarm not found: $alarmId")
                    stopSelf()
                    return@launch
                }

                startAlarmRinging(alarm)
            } catch (e: Exception) {
                Log.e("AlarmService", "Error starting alarm", e)
                stopSelf()
            }
        }

        return START_NOT_STICKY
    }

    private fun startAlarmRinging(alarm: Alarm) {
        currentAlarmId = alarm.id

        // Start foreground service with notification
        val notification = createNotification(alarm)
        startForeground(1, notification)

        // Play ringtone
        playRingtone(alarm.ringtoneUri)

        // Vibrate if enabled
        if (alarm.vibrate) {
            startVibration()
        }

        // Launch activity to display the alarm in fullscreen
        launchAlarmActivity(alarm.id)
    }

    private fun playRingtone(ringtoneUri: String) {
        try {
            val uri = if (ringtoneUri.isNotEmpty()) {
                ringtoneUri.toUri()
            } else {
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                    ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            }

            mediaPlayer = MediaPlayer().apply {
                setDataSource(applicationContext, uri)
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                isLooping = true
                prepare()
                start()
            }
        } catch (e: Exception) {
            Log.e("AlarmService", "Error playing ringtone", e)
        }
    }

    private fun startVibration() {
        try {
            vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                getSystemService(VIBRATOR_SERVICE) as Vibrator
            }

            val pattern = longArrayOf(0, 1000, 500, 1000, 500)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(
                    VibrationEffect.createWaveform(pattern, 0)
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(pattern, 0)
            }
        } catch (e: Exception) {
            Log.e("AlarmService", "Error starting vibration", e)
        }
    }

    private fun launchAlarmActivity(alarmId: Long) {
        try {
            val activityIntent = Intent(this, AlarmRingingActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra(AlarmScheduler.ALARM_ID_EXTRA, alarmId)
            }
            startActivity(activityIntent)
        } catch (e: Exception) {
            Log.e("AlarmService", "Error launching alarm activity", e)
        }
    }

    private fun stopAlarm() {
        // Stop ringtone
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
        }
        mediaPlayer = null

        // Stop vibration
        vibrator?.cancel()
        vibrator = null

        // Stop service
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(actionReceiver)
        } catch (e: Exception) {
            // Receiver already unregistered
        }
        stopAlarm()
        serviceScope.cancel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Alarmes",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications pour les alarmes"
                setSound(null, null) // Sound will be managed by the service
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(alarm: Alarm): Notification {
        // Intent pour l'activité plein écran
        val fullScreenIntent = Intent(this, AlarmRingingActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(AlarmScheduler.ALARM_ID_EXTRA, alarm.id)
        }
        val fullScreenPendingIntent = PendingIntent.getActivity(
            this,
            alarm.id.toInt(),
            fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Intent to open the application
        val openIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(AlarmScheduler.ALARM_ID_EXTRA, alarm.id)
        }
        val openPendingIntent = PendingIntent.getActivity(
            this,
            alarm.id.toInt() + 500,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Intent to stop the alarm - utiliser broadcast au lieu de service
        val stopIntent = Intent(ACTION_STOP_ALARM).apply {
            setPackage(packageName)
        }
        val stopPendingIntent = PendingIntent.getBroadcast(
            this,
            alarm.id.toInt() + 1000,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Intent pour snooze - utiliser broadcast au lieu de service
        val snoozeIntent = Intent(ACTION_SNOOZE_ALARM).apply {
            setPackage(packageName)
            putExtra(AlarmScheduler.ALARM_ID_EXTRA, alarm.id)
        }
        val snoozePendingIntent = PendingIntent.getBroadcast(
            this,
            alarm.id.toInt() + 2000,
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle(if (alarm.label.isNotEmpty()) alarm.label else "Alarme")
            .setContentText(String.format(Locale.getDefault(), "%02d:%02d", alarm.hour, alarm.minute))
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setOngoing(true)
            .setAutoCancel(false)
            .setContentIntent(openPendingIntent)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .addAction(
                android.R.drawable.ic_delete,
                "Stop",
                stopPendingIntent
            )

        if (alarm.snoozeEnabled) {
            notificationBuilder.addAction(
                android.R.drawable.ic_menu_recent_history,
                "Retarder 5 min",
                snoozePendingIntent
            )
        }

        return notificationBuilder.build()
    }
}
