package com.example.shawon.foodies;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class ListenOrderServer extends Service implements ChildEventListener{

    DatabaseReference mDatabaseRequest;

    public ListenOrderServer() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mDatabaseRequest = FirebaseDatabase.getInstance().getReference().child("Request");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mDatabaseRequest.addChildEventListener(this);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

        Request request = dataSnapshot.getValue(Request.class);
        if(request.getStatus().equals("0")){
            showNotification(dataSnapshot.getKey(),request);
        }

    }

    private void showNotification(String key, Request request) {

        Intent intent = new Intent(getApplicationContext(),OrderStatusServerActivity.class);

        PendingIntent contentIntent = PendingIntent.getActivity(this,0,intent,0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        builder.setSmallIcon(R.drawable.ic_restaurant_black_24dp)
                .setContentTitle("Foodies")
                .setContentText("You have new order #"+key)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(contentIntent);

        NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        // If you want to show many notification, then you need to give a unique id to each notification...
        int randomInt = new Random().nextInt(9999-1)+1;
        manager.notify(randomInt,builder.build());

    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
