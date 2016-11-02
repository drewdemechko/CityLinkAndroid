package edu.uco.captainplanet.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class ListRoutesActivity extends Activity {
    private Routes routes;
    private ArrayList<BusStop> stops;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes);

        routes = new Routes();
        stops = new ArrayList<>();

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
                        // do nothing
                    }
                });
    }

    public void setRoutes(JSONArray theRoutes)
    {
        try {
            for (int x = 0; x < theRoutes.length(); x++)
            {
                Log.d("theApp","forLoop");
                Route currentRoute = new Route();
                JSONObject routeJSON = theRoutes.getJSONObject(x);
                if (routeJSON.has("name")) {
                    currentRoute.setName(routeJSON.getString("name"));
                }
                if (routeJSON.has("id")) {
                    currentRoute.setId(routeJSON.getInt("id"));
                }
                if (routeJSON.has("busStops")) {
                    currentRoute.setOrderedStops(routeJSON.getJSONArray("busStops"), stops);
                }
                routes.addRoute(currentRoute);
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    public void setStops(JSONArray theStops)
    {
        try {
            for (int x = 0; x < theStops.length(); x++)
            {
                Log.d("theApp","forLoop");
                BusStop currentStop = new BusStop();

                if (theStops.getJSONObject(x).has("name")) {
                    currentStop.setName(theStops.getJSONObject(x).getString("name"));
                }
                stops.add(currentStop);
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(5000);
        client.get("https://uco-edmond-bus.herokuapp.com/api/routeservice/routes"
                , new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray theRoutes) {
                        setRoutes(theRoutes);

                        RouteArrayAdapter adapter = new RouteArrayAdapter(getApplicationContext(), routes.getRoutes());
                        ListView listView = (ListView) findViewById(R.id.lvRoutes);
                        listView.setAdapter(adapter);
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        // do nothing
                    }
                });
    }
}