package app.questbuds.notifs;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class BootReceiver extends BroadcastReceiver {
    public static String NOTIFICATION_CHANNEL_ID = "qnotif-chan-id-boot";
    public static String NOTIFICATION_CHANNEL_NAME = "qnotif-chan-name-boot";
    FirebaseFirestore fbfs;
    @Override
    public void onReceive(Context context, Intent intent) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        fbfs = FirebaseFirestore.getInstance();
        AlarmHandler alarmHandler = new AlarmHandler(context);
        Intent intent1 = new Intent(context, MainActivity.class);
        intent1.putExtra("to", "notifs");



        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            //Toast.makeText(context, "booted questbuds", Toast.LENGTH_SHORT).show();

            //
//            /*
            fbfs.collection("user").document(user.getEmail()).collection("notifs")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {


                            for (DocumentSnapshot snap: task.getResult()) {
                                alarmHandler.setAlarmManager(
                                        snap.getLong("hour").intValue(),
                                        snap.getLong("min").intValue()
                                );
                            }
                            showNotification(
                                    context,
                                    "Notifications automatically set",
                                    "QuestBuds Notifications were reset \nafter the reboot",
                                    intent1
                            );

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            //
                            showNotification(
                                    context,
                                    "Rebooting cancels Questbuds Notifications",
                                    "Tapping \"Update\" on each Notification item \n" +
                                            "in the app is suggested. Tap to proceed. ",
                                    intent1
                            );
                        }
                    });
            //
//            */
        }
    }

    public void showNotification(Context context, String title, String body, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationId = 2;

        int importance = NotificationManager.IMPORTANCE_HIGH;
        Uri sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/" + R.raw.questbud_ringtone);  //Here is FILE_NAME is the name of file that you want to play
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, importance);
            mChannel.setSound(null, null);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.GRAY);
            mChannel.enableVibration(true);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setSilent(true)
                .setSound(null)
                .setContentText(body);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                1,
                PendingIntent.FLAG_IMMUTABLE
        );
        mBuilder.setContentIntent(resultPendingIntent);

        notificationManager.notify(notificationId, mBuilder.build());

        MediaPlayer mp = MediaPlayer.create(context, sound);
        mp.start();
    }
}
