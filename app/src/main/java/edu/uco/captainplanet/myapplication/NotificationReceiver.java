package edu.uco.captainplanet.myapplication;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import edu.uco.captainplanet.myapplication.Dtos.Notification;

public class NotificationReceiver extends BroadcastReceiver {
    public NotificationReceiver() {
        List<Notification> userSpecificNotifications = new ArrayList<>();
    }

    @Override
    public void onReceive(Context arg0, Intent arg1) {
        //look for notifications based on user location
        //Toast.makeText(arg0, "I'm running", Toast.LENGTH_SHORT).show(); //REMOVE TOAST ONCE IMPLEMENTED

        //if user is logged in, search for userspecific notifications
        if(UserInfoApplication.getInstance().isLoggedIn()) {

        getUserNotifications(arg0);

            //if there are no user specific notifications
            if(UserInfoApplication.getInstance().getUserNotifications().isEmpty())
                return;
        }
    }

    public void getUserNotifications(final Context context)
    {
         /*
         * Attempt to get JSON info
         * Reference: http://loopj.com/android-async-http/
        */
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(5000); // give enough time for client to get the JSON data
        client.get("https://uco-edmond-bus.herokuapp.com/api/favoriteservice/favorites/notifications/"
                        + UserInfoApplication.getInstance().getUsername()
                , new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        // Called when response HTTP status is "200 OK"
                        try {
                            setUserNotifications(response,context);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });
    }

    private void setUserNotifications(JSONArray response, Context context)
    {
        try {
            for (int i = 0; i < response.length(); i++) {
                JSONObject item = new JSONObject(response.getString(i));
                Notification newNotification = new Notification(item.getString("text"));

                //if user has not already been notified
                if(!(UserInfoApplication.getInstance().getUserNotifications().contains(newNotification))){

                UserInfoApplication.getInstance().getUserNotifications().add(newNotification);
                //send notification
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
                mBuilder.setSmallIcon(R.drawable.cast_ic_notification_small_icon);
                mBuilder.setContentTitle("Favorites Update");
                mBuilder.setContentText(newNotification.getMessage());

                NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(i, mBuilder.build());}
                }
            } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
