package edu.uco.captainplanet.myapplication;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by drenf on 10/26/2016.
 */

public class BusApiConnector extends AsyncTask<Void, Void, ArrayList<Bus>>{
    private GoogleMap mMap;
    private Routes routes;
    private ArrayList<Bus> buses;
    private HttpClient client;
    private HttpUriRequest request;
    private InputStream stream;
    private boolean busExists;
    private Bus currentBus;
    private Marker currentMarker = null;
    private ArrayList<Marker> busMarkers;
    public BusApiConnectorResponse delegate;
    private String busTimeToStop;
    private Bus shortBus;
    Context mContext;
    private boolean dialogIsShown;


    public BusApiConnector(GoogleMap mMap, Routes routes, ArrayList<Bus> buses, BusApiConnectorResponse delegate, Context context) {
        this.mMap = mMap;
        this.routes = routes;
        this.buses = buses;
        client = new DefaultHttpClient();
        this.delegate = delegate;
        this.mContext = context;
        this.dialogIsShown = false;
    }

    @Override
    protected ArrayList<Bus> doInBackground(Void... voids) {

        request = new HttpGet("https://uco-edmond-bus.herokuapp.com/api/busservice/buses");
        HttpResponse httpResponse = null;
        try {

            httpResponse = client.execute(request);
            stream = httpResponse.getEntity().getContent();
            if (stream != null) {

               return jsonRead(stream);

            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return null;
    }

    private ArrayList<Bus> jsonRead(InputStream stream) throws IOException, JSONException{

        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        //ArrayList<Card> cards = new ArrayList<>();
        String line = "";
        String result = "";
        while ((line = br.readLine()) != null) {
            result += line;
        }


        stream.close();
        JSONArray theBuses = new JSONArray(result);

        try {

            int numTime = 200;
            shortBus = null;

            for(int x = 0; x < theBuses.length() ; x++)
            {

                busExists = false;
                if(x >= buses.size())
                {
                    currentBus = new Bus();
                    currentMarker = null;
                }
                else
                {
                    busExists = true;
                    currentBus = buses.get(x);
                    currentMarker = buses.get(x).getMyMarker();
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


                /*
                if(currentMarker != null)
                {
                    currentMarker.remove();
                }
                */
                if(currentBus.isActive())
                {


                    BusStop nextStop = routes.getNextStop(currentBus.getLastStop(), currentBus.getRoute());


                    request = new HttpGet("https://maps.googleapis.com/maps/api/directions/json?origin="+currentBus.getLat()+","+currentBus.getLongi()+"&destination="+nextStop.getLat()+","+nextStop.getLongi()+"&departure_time=1541202457&traffic_model=best_guess&key=AIzaSyCADdN-VW0vFCKz4uWqdL97Idk8ezENfHk");
                    HttpResponse httpResponse = null;
                    String timeToNextStop = "";
                    try {

                        httpResponse = client.execute(request);
                        stream = httpResponse.getEntity().getContent();
                        if (stream != null) {

                            BufferedReader br2 = new BufferedReader(new InputStreamReader(stream));

                            String line2 = "";
                            String result2 = "";
                            while ((line2 = br2.readLine()) != null) {
                                result2 += line2;
                            }


                            stream.close();
                            JSONObject distanceObject = new JSONObject(result2);

                            if(distanceObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("duration").has("text"))
                            {
                                timeToNextStop = distanceObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("duration").getString("text");
                            }
                            else
                            {
                                timeToNextStop = "Unknown";
                            }

                            //currentMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(currentBus.getLat(), currentBus.getLongi())).title("Time to Next Stop:" + timeToNextStop).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));

                            if(Integer.getInteger(timeToNextStop, 200) <= numTime ) {
                                busTimeToStop = timeToNextStop;
                                shortBus = currentBus;
                            }
                            br2.close();


                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } finally {
                        if(timeToNextStop.isEmpty()) {
                            timeToNextStop = "Unknown";
                        }
                        currentBus.setTimeToNextStop(timeToNextStop);
                    }
                }



                if(busExists) //this needs to be moved to the helper function and the varibles x, currentBus, mMap, and nextStop need to be passed to that function
                {
                    Bus temp = buses.set(x,currentBus);
                    //Marker tempM = busMarkers.set(x,currentMarker);

                }
                else
                {
                    buses.add(currentBus);
                    //busMarkers.add(currentMarker);
                }

            }

        } catch (JSONException ex) {

            ex.printStackTrace();

        }

        br.close();


        return buses;
    }


    @Override
    protected void onPostExecute(ArrayList<Bus> result) {
        delegate.processFinish(result);
        if(!dialogIsShown) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            if (busTimeToStop != null || busTimeToStop != null) {
                builder.setMessage("The closest bus is " + shortBus.getName() + ". It is " + busTimeToStop
                        + " away.");
            } else {
                builder.setMessage("We cannot determine the closest bus at this time.");
            }
            AlertDialog dialog = builder.create();
            dialogIsShown = true;
            dialog.show();
        }
    }





}
