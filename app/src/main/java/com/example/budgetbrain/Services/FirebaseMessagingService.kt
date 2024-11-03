package com.example.budgetbrain

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("FCM", "Message received from: ${remoteMessage.from}")

        val title = remoteMessage.notification?.title ?: "BudgetBrain Alert"
        val message = remoteMessage.notification?.body ?: "You have a new update!"

        // Show the notification
        NotificationHelper.showNotification(this, title, message)
    }
}
