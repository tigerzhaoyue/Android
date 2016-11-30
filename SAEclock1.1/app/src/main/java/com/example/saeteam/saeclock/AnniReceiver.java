package com.example.saeteam.saeclock;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
/**
 * Created by Tiger on 2016/11/3.
 */
public class AnniReceiver extends BroadcastReceiver{

    public AnniReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("Anniversary executed!");  //For debug

        AlarmManager an = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        an.cancel(PendingIntent.getBroadcast(context,getResultCode(),
                new Intent(context,AnniReceiver.class),0));

        Intent i = new Intent(context, PlayAnniAty.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}