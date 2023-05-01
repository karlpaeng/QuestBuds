package app.questbuds.notifs;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AlarmHandler {
    private Context context;

    public AlarmHandler(Context context) {
        this.context = context;
    }

    //activate alarm
    public void setAlarmManager(int hr, int min){
        Intent intent = new Intent(context, ExecutableService.class);
        //Bundle bundle = new Bundle();
        //bundle.putString("notifid", "69421");
        //intent.putExtra("notifids", bundle);
        //intent.putExtras(bundle);
        //intent.putExtra("notifid", "999");
        int code = Integer.parseInt("" + (hr < 10 ? "0" + hr : "" + hr) + (min < 10 ? "0" + min : "" + min));
        PendingIntent sender = PendingIntent.getBroadcast(context, code, intent, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (am != null){
            long trigAfter = 5 * 1000;
            long trigEvery = AlarmManager.INTERVAL_DAY;
            trigEvery = 24 * 1000;
            int hrNow = Integer.parseInt(new SimpleDateFormat("HH", Locale.getDefault()).format(new Date()));
            int minNow = Integer.parseInt(new SimpleDateFormat("mm", Locale.getDefault()).format(new Date()));
            int secNow = Integer.parseInt(new SimpleDateFormat("ss", Locale.getDefault()).format(new Date()));


            //Calendar calendar = Calendar.getInstance();

            long x = System.currentTimeMillis();
            long temp;

            if(((hr * 60) + min) <= ((hrNow * 60) + minNow)){
                //tomorrow
                temp = AlarmManager.INTERVAL_DAY - (
                        (
                                (
                                        (
                                                (
                                                        hrNow * 60
                                                ) + minNow
                                        ) * 60
                                )
                                        -
                                        (
                                                (
                                                        (
                                                                (
                                                                        hr * 60
                                                                ) + min
                                                        ) * 60
                                                ) + secNow
                                        )
                        ) * 1000
                );

            }else{
                //later
                temp = ((((hr * 60) + min) * 60) - ((((hrNow * 60) + minNow) * 60) + secNow)) * 1000;

            }
            x = x + temp;
            Toast.makeText(context, temp + ":" + hrNow + minNow, Toast.LENGTH_SHORT).show();

//            calendar.set(Calendar.HOUR_OF_DAY, hr);
//            calendar.set(Calendar.MINUTE, min);
            //calendar.setTimeInMillis(x);//


            //x = calendar.getTimeInMillis();
            //am.setRepeating(AlarmManager.RTC_WAKEUP, (calendar.getTimeInMillis()+trigAfter)/1000, AlarmManager.INTERVAL_HALF_HOUR, sender);
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, x , sender);
            //am.setExact(AlarmManager.RTC_WAKEUP, trigEvery, sender);
//            am.setInexactRepeating(AlarmManager.RTC_WAKEUP,
//                    (calendar.getTimeInMillis()) ,
//                    trigEvery,
//                    sender);
        }

    }
    //cancel alarm
    public void cancelAlarmManager(int hr, int min){
        int code = Integer.parseInt("" + (hr < 10 ? "0" + hr : "" + hr) + (min < 10 ? "0" + min : "" + min));
        Intent intent = new Intent(context, ExecutableService.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, code, intent, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (am != null){
            am.cancel(sender);
        }
    }
}
