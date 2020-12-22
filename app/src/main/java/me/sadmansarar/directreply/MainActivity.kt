package me.sadmansarar.directreply

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.IconCompat


class MainActivity : AppCompatActivity() {

    companion object {
        const val CHANNEL_ID = "me.sadmansarar.directreply"
        const val KEY_TEXT_REPLY = "key_text_reply"
        const val MESSAGE_NOTIFICATION_ID = 101
        const val REPLY_BROADCAST_ACTION = "me.sadmansarar.directreply.reply_action"
        const val EXTRA_REPLY_MESSAGE = "extra_reply_message"
        const val EXTRA_RECEIVED_MESSAGE = "extra_received_message"
    }

    private lateinit var notificationManager: NotificationManager
    private val remoteInputBroadcastReceiver = RemoteInputBroadcastReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        createNotificationChannel(CHANNEL_ID, "DirectReplyApp", "News Channel");
        findViewById<View>(R.id.btnShowNotification).setOnClickListener {
            sendNotification()
        }
    }

    override fun onStart() {
        super.onStart()
        registerReciver()
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(remoteInputBroadcastReceiver)
    }

    private fun registerReciver() {
        val filter = IntentFilter(REPLY_BROADCAST_ACTION)
        registerReceiver(remoteInputBroadcastReceiver, filter)
    }

    private fun createNotificationChannel(id: String, name: String, description: String) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(id, name, importance)
            channel.description = description
            channel.enableLights(true)
            channel.lightColor = Color.RED
            channel.enableVibration(true)

            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification() {
        val replyLabel = "Enter your reply here"
        val remoteInput: RemoteInput = RemoteInput.Builder(KEY_TEXT_REPLY)
                .setLabel(replyLabel)
                .build()

        val receivedMessage = "Hello! How are you?"

        val resultIntent = Intent(this, RemoteInputService::class.java)
        resultIntent.putExtra(EXTRA_RECEIVED_MESSAGE, receivedMessage)

        val resultPendingIntent = PendingIntent.getService(
                this,
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        )

        val replyAction: NotificationCompat.Action = NotificationCompat.Action.Builder(
                IconCompat.createWithResource(this, android.R.drawable.ic_dialog_info), "Reply",
                resultPendingIntent)
                .addRemoteInput(remoteInput)
                .build()

        val newMessageNotification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setColor(ContextCompat.getColor(this, R.color.purple_200))
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("You received a new message")
                .setContentText(receivedMessage)
                .addAction(replyAction).build()

        notificationManager.notify(MESSAGE_NOTIFICATION_ID, newMessageNotification)
    }


    inner class RemoteInputBroadcastReceiver : BroadcastReceiver() {


        override fun onReceive(context: Context, intent: Intent) {
            val myTextView = this@MainActivity.findViewById<TextView>(R.id.txtMessage)
            val inputString = intent.getStringExtra(EXTRA_REPLY_MESSAGE)
            myTextView.text = inputString
        }
    }

}
