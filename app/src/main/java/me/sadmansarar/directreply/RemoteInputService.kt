package me.sadmansarar.directreply

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput


class RemoteInputService : Service() {
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let { handleIntent(it) }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun handleIntent(intent: Intent) {
        val remoteInput = RemoteInput.getResultsFromIntent(intent)
        if (remoteInput != null) {
            val inputString = remoteInput.getCharSequence(
                MainActivity.KEY_TEXT_REPLY).toString()

            val receivedMessage = intent.getStringExtra(
                MainActivity.EXTRA_RECEIVED_MESSAGE)

            Toast.makeText(this, inputString, Toast.LENGTH_SHORT).show()
            sendBroadcast(
                Intent(MainActivity.REPLY_BROADCAST_ACTION).apply {
                    putExtra(MainActivity.EXTRA_REPLY_MESSAGE, inputString)
                }
            )

            val repliedNotification: Notification = NotificationCompat.Builder(this,
                MainActivity.CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentText(receivedMessage + "\n" + inputString)
                .build()

            val notificationManager = getSystemService(
                Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(MainActivity.MESSAGE_NOTIFICATION_ID, repliedNotification)
        }
    }
}