/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pem.mustafa.servertest.GcmFiles;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.pem.mustafa.servertest.Activities.FriendRequestsActivity;
import com.pem.mustafa.servertest.Activities.LoginActivity;
import com.pem.mustafa.servertest.Activities.MessageBodyActivity;
import com.pem.mustafa.servertest.Activities.ProfileActivity;
import com.pem.mustafa.servertest.R;

public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";
    private static final int MESSAGE_NOTIFICATION_ID = 0;
    private static final int FOLLOWER_NOTIFICATION_ID = 1;
    private static final int FOLLOWREQUEST_NOTIFICATION_ID = 2;
    private static final int REQUEST_NOTIFICATION_ID = 3;
    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        String messageTitle = data.getString("title");
        String sender = data.getString("sender");
        String notificationType = data.getString("type");
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);

        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }

        // [START_EXCLUDE]
        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */
        sendNotification(sender, message, messageTitle, notificationType);
        // [END_EXCLUDE]
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String sender, String message, String title, String type) {

        if(type.equals("messagenotification"))
        {
            Intent intent = new Intent(this, MessageBodyActivity.class);
            intent.putExtra("username", sender);
            Intent broadcastIntent = new Intent("broadcast_intent");
            broadcastIntent.putExtra("newmessage", message );
            broadcastIntent.putExtra("sender", sender );
            LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_message_white_24dp)
                    .setContentTitle(sender)
                    .setLargeIcon(((BitmapDrawable) ContextCompat.getDrawable(getApplicationContext(), R.mipmap.appicon)).getBitmap())
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setVibrate(new long[]{100, 400, 200, 400})
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(MESSAGE_NOTIFICATION_ID, notificationBuilder.build());

        }
        else if(type.equals("followernotification"))
        {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_favorite_white_24dp)
                    .setContentTitle(sender)
                    .setLargeIcon(((BitmapDrawable) ContextCompat.getDrawable(getApplicationContext(), R.mipmap.appicon)).getBitmap())
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setVibrate(new long[] { 100, 400, 200, 400})
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(FOLLOWER_NOTIFICATION_ID , notificationBuilder.build());
        }
        else if(type.equals("followrequestnotification"))
        {
            Intent intent = new Intent(this, FriendRequestsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_favorite_white_24dp)
                    .setContentTitle(sender)
                    .setLargeIcon(((BitmapDrawable) ContextCompat.getDrawable(getApplicationContext(), R.mipmap.appicon)).getBitmap())
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setVibrate(new long[] { 100, 400, 200, 400})
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(FOLLOWREQUEST_NOTIFICATION_ID , notificationBuilder.build());
        }
        else if(type.equals("requestnotification"))
        {
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_access_time_white_24dp)
                    .setContentTitle(sender)
                    .setLargeIcon(((BitmapDrawable) ContextCompat.getDrawable(getApplicationContext(), R.mipmap.appicon)).getBitmap())
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setVibrate(new long[] { 100, 400, 200, 400})
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(REQUEST_NOTIFICATION_ID , notificationBuilder.build());
        }


    }
}
