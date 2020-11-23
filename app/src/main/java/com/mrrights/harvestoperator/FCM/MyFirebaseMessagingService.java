package com.mrrights.harvestoperator.FCM;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mrrights.harvestoperator.R;
import com.mrrights.harvestoperator.fragments.FragmentHome;
import com.mrrights.harvestoperator.fragments.FragmentHome.*;
import com.mrrights.harvestoperator.fragments.FragmentHomeKt;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCM Data Tag" ;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            String data=  remoteMessage.getNotification().getBody();
           String details[] = (data.split("\n"));
           String username,location,serviceType,date,time,area;
            username=details[0];
            location=details[1];
            serviceType=details[2];
            date=details[3];
            time=details[4];
            area=details[5];

            Intent i=new Intent("receiver");
            i.putExtra("User",username);
            i.putExtra("Loc",location);
            i.putExtra("Type","Service :"+serviceType);
            i.putExtra("Date","Date :"+date);
            i.putExtra("Time","Time :"+time);
            i.putExtra("Area","Area :"+area);

               LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(i);

            //Log.d(TAG, "");

            //Toast.makeText(getApplicationContext(),"Data is "+details[0],Toast.LENGTH_SHORT).show();

        }
        sendNotification(remoteMessage.getNotification().getBody());
    }
    private void sendNotification(String messageBody) {


        Intent intent = new Intent(this, FragmentHome.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        String channelId = "Default";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new  NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(messageBody)
                .setContentText(messageBody)
                .setSound(defaultSoundUri).setAutoCancel(true).setContentIntent(pendingIntent);;
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Default channel", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }
        manager.notify(0, builder.build());
    }
}
