package edu.uco.captainplanet.myapplication;

import android.*;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceActivity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.LocationListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.*;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;


public class MainMapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener
{

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private Marker mCurrLocationMarker;
    private ArrayList<BusStop> stops;
    private ArrayList<Bus> buses;
    private Handler h = new Handler();
    private int delay = 5000; //milliseconds
    private ArrayList<Marker> busMarkers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_maps);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        stops = new ArrayList<>();
        buses = new ArrayList<>();
        busMarkers = new ArrayList<>();

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(5000);
        client.get("https://uco-edmond-bus.herokuapp.com/api/busservice/stops"
                , new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray theStops) {
                       setStops(theStops);

                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                    }



                });



        h.postDelayed(new Runnable(){
            public void run(){
                AsyncHttpClient client = new AsyncHttpClient();
                client.setTimeout(5000);
                client.get("https://uco-edmond-bus.herokuapp.com/api/busservice/buses"
                        , new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONArray theBuses) {
                                setBuses(theBuses);

                            }
                            @Override
                            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                            }



                        });





                h.postDelayed(this, delay);
            }
        }, delay);





    }

    public void setStops(JSONArray theStops)
    {
        try {

            for(int x = 0; x < theStops.length() ; x++)
            {
                Log.d("theApp","forLoop");
                BusStop currentStop = new BusStop();

                if(theStops.getJSONObject(x).has("name"))
                {
                    currentStop.setName(theStops.getJSONObject(x).getString("name"));
                }
                if(theStops.getJSONObject(x).has("firstcrossstreet"))
                {
                    currentStop.setFirstCrossStreet(theStops.getJSONObject(x).getString("firstcrossstreet"));
                }
                if(theStops.getJSONObject(x).has("secondcrossstreet"))
                {
                    currentStop.setSecondCrossStreet(theStops.getJSONObject(x).getString("secondcrossstreet"));
                }
                if(theStops.getJSONObject(x).has("latitude"))
                {
                    currentStop.setLat(theStops.getJSONObject(x).getDouble("latitude"));
                }
                if(theStops.getJSONObject(x).has("longitude"))
                {
                    currentStop.setLongi(theStops.getJSONObject(x).getDouble("longitude"));
                }
                if(theStops.getJSONObject(x).has("inactive")) {
                    currentStop.setInactive(theStops.getJSONObject(x).getBoolean("inactive"));
                }
                stops.add(currentStop);
                mMap.addMarker(new MarkerOptions().position(new LatLng(currentStop.getLat(), currentStop.getLongi())).title(currentStop.getName()));
            }






        } catch (JSONException ex) {

            ex.printStackTrace();

        }
    }

    public void setBuses(JSONArray theBuses)
    {


        try {

            for(int x = 0; x < theBuses.length() ; x++)
            {
                Bus currentBus;
                Marker currentMarker = null;
                boolean busExists = false;
                if(x >= buses.size())
                {
                    currentBus = new Bus();

                }
                else
                {
                    busExists = true;
                    currentBus = buses.get(x);
                    currentMarker = busMarkers.get(x);
                }


                if(theBuses.getJSONObject(x).has("name"))
                {
                    currentBus.setName(theBuses.getJSONObject(x).getString("name"));
                }
                if(theBuses.getJSONObject(x).has("route"))
                {
                    currentBus.setRoute(theBuses.getJSONObject(x).getString("route"));
                }
                if(theBuses.getJSONObject(x).has("active"))
                {
                    currentBus.setActive(theBuses.getJSONObject(x).getBoolean("active"));
                }
                if(theBuses.getJSONObject(x).has("laststop"))
                {
                    currentBus.setLastStop(theBuses.getJSONObject(x).getString("laststop"));
                }
                if(theBuses.getJSONObject(x).has("lastLongitude"))
                {
                    currentBus.setLongi(theBuses.getJSONObject(x).getDouble("lastLongitude"));
                }
                if(theBuses.getJSONObject(x).has("lastLatitude")) {
                    currentBus.setLat(theBuses.getJSONObject(x).getDouble("lastLatitude"));
                }



                if(currentMarker != null)
                {
                    currentMarker.remove();
                }

                if(currentBus.isActive())
                {
                    currentMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(currentBus.getLat(), currentBus.getLongi())).title(currentBus.getName()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                }



                if(busExists)
                {
                    Bus temp = buses.set(x,currentBus);
                    Marker tempM = busMarkers.set(x,currentMarker);

                }
                else
                {
                    buses.add(currentBus);
                    busMarkers.add(currentMarker);
                }

            }






        } catch (JSONException ex) {

            ex.printStackTrace();

        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();


        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Permission was granted.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            //You can add here other case statements according to your requirement.
        }
    }


}
