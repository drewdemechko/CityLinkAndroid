

package edu.uco.captainplanet.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
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
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.Header;


public class MainMapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        BusApiConnectorResponse
{

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    Bitmap busBitmap;
    Bitmap benchBitmap;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private Marker mCurrLocationMarker;
    private ArrayList<BusStop> stops;
    private ArrayList<Bus> buses;
    private Handler h = new Handler();
    private final int delay = 20000; //milliseconds
    private ArrayList<Marker> busMarkers;
    private Bus currentBus;
    private Marker currentMarker = null;
    private Routes routes;
    private boolean busExists;
    private boolean busClosestExists;
    private String timeToNextStop;
    private BusApiConnectorResponse me;
    private Context mContext;

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
        routes = new Routes();
        me = this;
        mContext = this;
        busClosestExists = false;
        busBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.shuttle);
        benchBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bench);
    }

    public void setRoutes(JSONArray theRoutes)
    {
        try {

            for(int x = 0; x < theRoutes.length() ; x++)
            {
                Log.d("theApp","forLoop");
                Route currentRoute = new Route();
                JSONObject routeJSON = theRoutes.getJSONObject(x);
                if(routeJSON.has("name"))
                {
                    currentRoute.setName(routeJSON.getString("name"));
                }
                if(routeJSON.has("id"))
                {
                    currentRoute.setId(routeJSON.getInt("id"));
                }
                if(routeJSON.has("busStops"))
                {
                    currentRoute.setOrderedStops(routeJSON.getJSONArray("busStops"), stops);
                }/*
                if(theStops.getJSONObject(x).has("inactive")) {
                    currentStop.setInactive(theStops.getJSONObject(x).getBoolean("inactive"));
                }*/
                routes.addRoute(currentRoute);
            }








        } catch (JSONException ex) {

            ex.printStackTrace();

        }

        callAsynchronousTask();
        /*
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
        */
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
                }/*
                if(theStops.getJSONObject(x).has("firstcrossstreet"))
                {
                    currentStop.setFirstCrossStreet(theStops.getJSONObject(x).getString("firstcrossstreet"));
                }
                if(theStops.getJSONObject(x).has("secondcrossstreet"))
                {
                    currentStop.setSecondCrossStreet(theStops.getJSONObject(x).getString("secondcrossstreet"));
                }*/
                if(theStops.getJSONObject(x).has("latitude"))
                {
                    currentStop.setLat(theStops.getJSONObject(x).getDouble("latitude"));
                }
                if(theStops.getJSONObject(x).has("longitude"))
                {
                    currentStop.setLongi(theStops.getJSONObject(x).getDouble("longitude"));
                }/*
                if(theStops.getJSONObject(x).has("inactive")) {
                    currentStop.setInactive(theStops.getJSONObject(x).getBoolean("inactive"));
                }*/
                stops.add(currentStop);
                mMap.addMarker(new MarkerOptions().position(new LatLng(currentStop.getLat(), currentStop.getLongi())).title(currentStop.getName()).icon(BitmapDescriptorFactory.fromBitmap(benchBitmap)));
            }








        } catch (JSONException ex) {

            ex.printStackTrace();

        }

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(delay);
        client.get("https://uco-edmond-bus.herokuapp.com/api/routeservice/routes"
                , new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray theRoutes) {
                        setRoutes(theRoutes);

                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                    }



                });
    }

    public void setBuses(JSONArray theBuses)
    {


        try {

            for(int x = 0; x < theBuses.length() ; x++)
            {

                busExists = false;
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
                if(theBuses.getJSONObject(x).has("driver"))
                {
                    currentBus.setDriver(theBuses.getJSONObject(x).getString("driver"));
                }

                if(theBuses.getJSONObject(x).has("route"))
                {
                    currentBus.setRoute(theBuses.getJSONObject(x).getString("route"));
                }
                if(theBuses.getJSONObject(x).has("active"))
                {
                    currentBus.setActive(theBuses.getJSONObject(x).getBoolean("active"));
                }
                if(theBuses.getJSONObject(x).has("lastStop"))
                {
                    currentBus.setLastStop(theBuses.getJSONObject(x).getString("lastStop"));
                }
                if(theBuses.getJSONObject(x).has("lastLong"))
                {
                    currentBus.setLongi(theBuses.getJSONObject(x).getDouble("lastLong"));
                }
                if(theBuses.getJSONObject(x).has("lastLat")) {
                    currentBus.setLat(theBuses.getJSONObject(x).getDouble("lastLat"));
                }



                if(currentMarker != null)
                {
                    currentMarker.remove();
                }

                if(currentBus.isActive())
                {

                    BusStop nextStop = routes.getNextStop(currentBus.getLastStop(), currentBus.getRoute());

                    AsyncHttpClient client = new AsyncHttpClient();
                    client.setTimeout(delay);
                    client.get("https://maps.googleapis.com/maps/api/directions/json?origin="+currentBus.getLat()+"," +
                            ""+currentBus.getLongi()+"&destination="+nextStop.getLat()+","+nextStop.getLongi()+"&departure_time=1541202457&traffic_model=best_guess&key=AIzaSyCADdN-VW0vFCKz4uWqdL97Idk8ezENfHk"
                            , new JsonHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject distanceObject) {

                                    setBusHelper(distanceObject);

                                }



                                @Override
                                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                                }



                            });

                }



                if(busExists) //this needs to be moved to the helper function and the varibles x, currentBus, mMap, and nextStop need to be passed to that function
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

    private void setBusHelper(JSONObject distanceObject) {
        String timeToNextStop = "Unknown";
        try {
            timeToNextStop = distanceObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("duration").getString("text");

            int timeNumToNextStop = Integer.getInteger(timeToNextStop.substring(0, timeToNextStop.length() - 4));
            if(timeNumToNextStop < 10 && busClosestExists == false) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("The bus is only " + timeToNextStop + " away.");
                AlertDialog dialog = builder.create();
                dialog.show();
            }


            currentMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(currentBus.getLat(), currentBus.getLongi())).title("Time to Next Stop:" + timeToNextStop).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));

        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if(timeToNextStop == "Unknown") {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("We are unable to calculate how far away your bus is. Be Ready!");
                AlertDialog dialog = builder.create();
                dialog.show();
            }
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

    public void callAsynchronousTask() {
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            BusApiConnector performBackgroundTask = new BusApiConnector(mMap,
                                    routes, buses, me, mContext, busClosestExists);
                            // PerformBackgroundTask this class is the class that extends AsynchTask

                            performBackgroundTask.execute();
                            busClosestExists = true;
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 5000); //execute in every 5000 ms
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(5000);
        client.get("https://uco-edmond-bus.herokuapp.com/api/busstopservice/stops"
                , new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray theStops) {
                        setStops(theStops);

                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                    }



                });

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

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
        {

            @Override
            public boolean onMarkerClick(Marker arg0) {
                for(int x = 0 ; x < stops.size() ; x++)
                {
                    if(arg0.getTitle().equals(stops.get(x).getName()))
                    {
                        double lat = stops.get(x).getLat();
                        double theLong = stops.get(x).getLongi();
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                Uri.parse("http://maps.google.com/maps?daddr="+lat+","+theLong));
                        startActivity(intent);
                    }
                }
                arg0.showInfoWindow();
                // if marker source is clicked

                return true;
            }

        });
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
        //change this to be in the middle of all Edmond bus stops
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(35.642507,-97.4596315),13));
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


    @Override
    public void processFinish(ArrayList<Bus> output) {
        for(int x = 0 ; x < output.size() ; x++)
        {
            Bus currentBus = output.get(x);
            Marker currentMarker = currentBus.getMyMarker();
            if(currentMarker != null)
            {
                currentMarker.remove();
            }
            currentMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(currentBus.getLongi(), currentBus.getLat())).title("Time to Next Stop:" + currentBus.getTimeToNextStop()).icon(BitmapDescriptorFactory.fromBitmap(busBitmap)));
            currentBus.setMyMarker(currentMarker);
            buses.set(x,currentBus);
        }
    }
}
