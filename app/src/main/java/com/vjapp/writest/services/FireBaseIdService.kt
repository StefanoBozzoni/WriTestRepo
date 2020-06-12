package com.vjapp.writest.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.DEFAULT_SOUND
import androidx.core.app.NotificationCompat.DEFAULT_VIBRATE
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.vjapp.writest.LoginActivity
import com.vjapp.writest.R
import com.vjapp.writest.domain.interctor.UseCaseAddMessageToQueue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import java.util.*

class MyFirebaseMsgService() : FirebaseMessagingService() {

    val addMessageToQueueUseCase : UseCaseAddMessageToQueue = get()

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(
            LOG_TAG,"Refreshed token: $token"
        )
        sendRegistrationToServer(token)
    }

    /**
     * Persist token to third-party servers.
     *
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private fun sendRegistrationToServer(token: String) {
        // This method is blank, but if you were to build a server that stores users token
        // information, this is where you'd send the token to the server.
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // The server should always sends just *data* messages, meaning that onMessageReceived when
        // the app is both in the foreground AND the background
        Log.d(
            LOG_TAG,"From: " + remoteMessage.from
        )

        // Check if message contains a data payload.
        val data = remoteMessage.data
        if (data.size > 0) {
            Log.d(LOG_TAG, "Message data payload: $data")
            // Send a notification that you got a new message
            sendNotification(data)
        }
    }

    private fun sendNotification(data: Map<String, String>) {

        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        // Create the pending intent to launch the activity
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )
        val author    = data[JSON_KEY_AUTHOR]
        var message   = data[JSON_KEY_MESSAGE]
        val token     = data[JSON_KEY_TOKEN]
        val diagnosis = data[JSON_KEY_DIAGNOSIS]
        val email     = data[JSON_KEY_EMAIL]

        // If the message is longer than the max number of characters we want in our
        // notification, truncate it and add the unicode character for ellipsis
        if (message!!.length > NOTIFICATION_MAX_CHARACTERS) {
            message = message.substring(0, NOTIFICATION_MAX_CHARACTERS) + "\u2026"
        }

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(this,"writest channel")
            .setSmallIcon(R.drawable.ic_info)
            .setContentTitle(String.format(getString(R.string.notification_message), author))
            .setContentText(message)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setDefaults(DEFAULT_SOUND or DEFAULT_VIBRATE)
            .setContentIntent(pendingIntent)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        createNotificationChannel()

        notificationManager.notify(UUID.randomUUID().toString(),0 /* ID of notification */, notificationBuilder.build())
        addTokenToDBQueue(token,email,diagnosis) //Insert messages info into DB table queue

        /*
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .addAction(
                R.drawable.tim_games_notification_icon,
                remoteMessage.data[BUTTON] ?: resources.getString(R.string.show_notification),
                pendingIntent
            )
        */


    }

    fun addTokenToDBQueue(token:String?, email: String?, diagnosis:String?) {
        if (token!=null && diagnosis!=null && email!=null) {
            GlobalScope.launch(Dispatchers.IO) {
                addMessageToQueueUseCase.execute(UseCaseAddMessageToQueue.Params(token, email, diagnosis))
            }
        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "WriTest main Channel"
            val descriptionText = "notifiche WriTestApp"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        private val LOG_TAG    = MyFirebaseMsgService::class.java.simpleName
        const val JSON_KEY_AUTHOR    = "author"
        const val JSON_KEY_MESSAGE   = "message"
        const val JSON_KEY_TOKEN     = "token"
        const val JSON_KEY_DIAGNOSIS = "diagnosis"
        const val JSON_KEY_EMAIL     = "email"
        const val NOTIFICATION_MAX_CHARACTERS = 50
        const val CHANNEL_ID         = "writest channel"
    }
}