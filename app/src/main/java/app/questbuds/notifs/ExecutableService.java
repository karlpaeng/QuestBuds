package app.questbuds.notifs;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
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

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ExecutableService extends BroadcastReceiver {
    public static String NOTIFICATION_ID = "qnotif-id";
    public static String NOTIFICATION_CHANNEL_ID = "qnotif-chan-id";
    public static String NOTIFICATION = "qnotif";
    public static String NOTIFICATION_CHANNEL_NAME = "qnotif-chan-name";

    GoogleSignInClient client;
    GoogleSignInOptions options;

    FirebaseFirestore fbfs;

    @Override
    public void onReceive(Context context, Intent intent) {
        //
        Intent intent1 = new Intent(context, MainActivity.class);
        intent1.putExtra("to", "current");

        //Bundle bundle = intent.getBundleExtra("notifid");
        
        //String idStr = new SimpleDateFormat("hhmmss", Locale.getDefault()).format(new Date());
        int hr = Integer.parseInt(new SimpleDateFormat("HH", Locale.getDefault()).format(new Date()));
        int min = Integer.parseInt(new SimpleDateFormat("mm", Locale.getDefault()).format(new Date()));
        //String idStr = intent.getStringExtra("notifid");
        //intent.getBundleExtra("notifids").getString("notifid");//intent.getStringExtra("notifid");

        //PendingIntent pendingIntent = PendingIntent.getActivity(context, 69, intent1, PendingIntent.FLAG_IMMUTABLE);

        //Toast.makeText(context, "ooh toasty", Toast.LENGTH_SHORT).show();

        AlarmHandler alarmHandler = new AlarmHandler(context);
        //alarmHandler.setAlarmManager(hr, min);//

        //important
        /*
        - get notifs collection from firestore
        - iterate through result
        - get closest time from current time
        - get text value
        - set notification id
        - show notification

        showNotification(
                context,
                "Buddy, you've got Quests!",
                "Tap to view your Quests",
                intent1);

         */
//        /*
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        fbfs = FirebaseFirestore.getInstance();

        fbfs.collection("user").document(user.getEmail()).collection("notifs")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        int difference = 1440;
                        DocumentSnapshot out = null;
                        for (DocumentSnapshot snap: task.getResult()) {
                            //Toast.makeText(context, "got here" + task.getResult().size(), Toast.LENGTH_SHORT).show();
                            //get closest time
                            int temp = snap.getLong("hour").intValue();
                            temp = (temp*60) + snap.getLong("min").intValue();
//                            /*
                            if (difference >= Math.abs(temp - ((hr*60) + min))){
                                difference = Math.abs(temp - ((hr*60) + min));
                                out = snap;
                            }else {
                                break;
                            }
//                            */

                        }
                        showNotification(
                                context,
                                "Buddy, you've got Quests!",
                                out.getString("text").equals("") ? "Tap to view your Quests" : out.getString("text"),
                                intent1);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "failed to retrieve notification details", Toast.LENGTH_SHORT).show();
                    }
                });

//         */



    }
    public void showNotification(Context context, String title, String body, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationId = 1;

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
                0,
                PendingIntent.FLAG_IMMUTABLE
        );
        mBuilder.setContentIntent(resultPendingIntent);

        notificationManager.notify(notificationId, mBuilder.build());

        MediaPlayer mp = MediaPlayer.create(context, sound);
        mp.start();
    }
}