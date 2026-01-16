package fr.celld.smartalarm.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import fr.celld.smartalarm.R

/**
 * Service pour gérer l'alarme qui sonne
 */
class AlarmService : Service() {

    private val channelId = "alarm_channel"

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val alarmId = intent?.getLongExtra(AlarmScheduler.ALARM_ID_EXTRA, -1) ?: -1

        // Démarrer le service en foreground avec une notification
        val notification = createNotification()
        startForeground(1, notification)

        // TODO: Charger l'alarme depuis la base de données
        // TODO: Jouer la sonnerie
        // TODO: Vibrer
        // TODO: Vérifier le capteur de détection
        // TODO: Afficher l'activité pour arrêter l'alarme

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Alarmes",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications pour les alarmes"
                setSound(null, null) // Le son sera géré par le service
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Alarme en cours")
            .setContentText("Votre alarme sonne")
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm) // TODO: Utiliser une vraie icône
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setOngoing(true)
            .build()
    }
}
