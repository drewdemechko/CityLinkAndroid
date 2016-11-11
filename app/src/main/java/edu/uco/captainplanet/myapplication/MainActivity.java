package edu.uco.captainplanet.myapplication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ActionBarDrawerToggle toggle;
    Button mapsButton;
    private PendingIntent pendingIntent;
    private AlarmManager manager;

    private static final int REQUEST_LOGIN = 1;

    // Update UI with new info
    private final Handler mHandler = new Handler();
    private final Runnable mUpdateUITimerTask = new Runnable() {
        public void run() {
            // Update username with login info
            TextView t = (TextView)findViewById(R.id.nav_header_username);
            String username = UserInfoApplication.getInstance().getUsername();

            if (!username.equals("") && UserInfoApplication.getInstance().isLoggedIn())
                t.setText(username);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapsButton = (Button) findViewById(R.id.googleMapsButton);

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);

        mapsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent update = new Intent(MainActivity.this, MainMapsActivity.class);
                startActivityForResult(update, RESULT_OK);
            }
        });

        // Retrieve a PendingIntent that will perform a broadcast
        Intent notificationsIntent = new Intent(this, NotificationReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, notificationsIntent, 0);
        startAlarm();
    }

    public void startAlarm() {
        manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        int interval = 10000;

        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Update info based on login success
        if (requestCode == REQUEST_LOGIN) {
            if (resultCode == RESULT_OK) {
                // Update username after delay (in ms)
                mHandler.postDelayed(mUpdateUITimerTask, 1);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        if(item.getTitle().equals("Bus Routes"))
        {
            Intent routesIntent = new Intent(this, ListRoutesActivity.class);
            startActivity(routesIntent);
        }
        else if(item.getTitle().equals("Bus Map"))
        {
            Intent update = new Intent(MainActivity.this, MainMapsActivity.class);
            startActivityForResult(update, RESULT_OK);
        }
        else if(item.getTitle().equals("Favorites"))
        {
            Intent favoritesIntent = new Intent(this, FavoritesActivity.class);
            startActivity(favoritesIntent);
        }
        else if(item.getTitle().equals("My Account"))
        {
            Intent accountIntent = new Intent(this, AccountActivity.class);
            startActivity(accountIntent);
        }
        else if(item.getTitle().equals("Settings"))
        {

        }
        else if(item.getTitle().equals("Login"))
        {

            Intent loginIntent = new Intent(this, LoginActivity.class);
            item.setTitle("Logout");
            startActivityForResult(loginIntent, REQUEST_LOGIN);


        }
        else if(item.getTitle().equals("Logout"))
        {
            UserInfoApplication.logout();
            updateMenu();

        }

        /* had  change from case to if else since you have to have a contant expressions for comparisson
        switch (item.getTitle() ) {


            case getString(R.string.bus_routes):
                Intent routesIntent = new Intent(this, ListRoutesActivity.class);
                startActivity(routesIntent);
                break;

            case R.id.nav_menu_favorites:
                Intent favoritesIntent = new Intent(this, FavoritesActivity.class);
                startActivity(favoritesIntent);
                break;

            case R.id.nav_menu_account:
                Intent accountIntent = new Intent(this, AccountActivity.class);
                startActivity(accountIntent);
                break;

            case R.id.nav_menu_settings:
                Toast.makeText(MainActivity.this, "Clicked nav menu 4", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_menu_login:
                if(item.getTitle() != "Logout") {
                    Intent loginIntent = new Intent(this, LoginActivity.class);
                    item.setTitle("Logout");
                    startActivityForResult(loginIntent, REQUEST_LOGIN);
                } else {
                    UserInfoApplication.logout();
                    item.setTitle("Log In");
                }

                break;
        }

        // After clicking on an option, close the nav menu
        DrawerLayout dl = (DrawerLayout) findViewById(R.id.drawerLayout);
        if (dl.isDrawerOpen(GravityCompat.START)) {
            dl.closeDrawer(GravityCompat.START);
        }

        return false;
    }
}
