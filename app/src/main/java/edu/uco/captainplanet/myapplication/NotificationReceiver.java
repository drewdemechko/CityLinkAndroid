package edu.uco.captainplanet.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class NotificationReceiver extends BroadcastReceiver {
    public NotificationReceiver() {
    }

    @Override
    public void onReceive(Context arg0, Intent arg1) {
        //look for notifications based on user location
        Toast.makeText(arg0, "I'm running", Toast.LENGTH_SHORT).show();

        //if user is logged in, search for userspecific notifications
        if(UserInfoApplication.getInstance().isLoggedIn())
            Toast.makeText(arg0, "I am searching for new notifications that are specific to the user.", Toast.LENGTH_SHORT).show();
    }
}
