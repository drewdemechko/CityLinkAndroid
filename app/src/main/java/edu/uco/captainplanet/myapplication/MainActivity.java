package edu.uco.captainplanet.myapplication;

import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);
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

        switch (item.getItemId()) {
            case R.id.nav_menu_routes:
                Toast.makeText(MainActivity.this, "Clicked nav menu 1", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_menu_favorites:
                Toast.makeText(MainActivity.this, "Clicked nav menu 2", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_menu_account:
                Toast.makeText(MainActivity.this, "Clicked nav menu 3", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_menu_settings:
                Toast.makeText(MainActivity.this, "Clicked nav menu 4", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_menu_logout:
                Toast.makeText(MainActivity.this, "Clicked nav menu 5", Toast.LENGTH_SHORT).show();
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
