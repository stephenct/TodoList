package vn.com.detai.todolist.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.LOLLIPOP
import android.preference.PreferenceManager
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import android.support.v4.media.AudioAttributesCompat.USAGE_NOTIFICATION
import vn.com.detai.R
import vn.com.detai.todolist.activity.MainActivity
import vn.com.detai.todolist.utils.MyApplication

/**
 * Lớp cài đặt thông báo.
 */
class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title")
        val timeStamp = intent.getLongExtra("time_stamp", 0).toInt()

        // Intent khởi chạy ứng dụng khi bạn nhấp vào thông báo
        var resultIntent = Intent(context, MainActivity::class.java)

        if (MyApplication.isActivityVisible) {
            resultIntent = intent
        }

        resultIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent = PendingIntent.getActivity(context, timeStamp, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val ringtonePath = preferences.getString("notification_sound", "")

        // Cài đặt kênh thông báo
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "SimpleToDo Notifications",
                    NotificationManager.IMPORTANCE_HIGH)
            channel.enableLights(true)
            channel.lightColor = Color.GREEN
            channel.enableVibration(true)
            notificationManager.createNotificationChannel(channel)
        }

        // Tùy chỉnh và tạo thông báo
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        if (ringtonePath != "") {
            if (SDK_INT >= LOLLIPOP) {
                builder.setSound(Uri.parse(ringtonePath), USAGE_NOTIFICATION)
            } else {
                builder.setSound(Uri.parse(ringtonePath))
            }
        }
        builder.setContentTitle(context.getString(R.string.reminder_text))
        builder.setContentText(title)
        builder.setStyle(NotificationCompat.BigTextStyle().bigText(title))
        builder.color = ContextCompat.getColor(context, R.color.colorAccent)
        builder.setSmallIcon(R.drawable.ic_check_circle_white_24dp)
        builder.setContentIntent(pendingIntent)
        builder.setAutoCancel(true)

        val notification = builder.build()
        notificationManager.notify(timeStamp, notification)
    }

    companion object {
        const val CHANNEL_ID = "1"
    }
}
