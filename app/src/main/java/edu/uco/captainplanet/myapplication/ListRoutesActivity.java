package edu.uco.captainplanet.myapplication;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class ListRoutesActivity extends ListActivity {
    private Routes routes;
    private ArrayList<BusStop> stops;

    private ListView listView;
    private List<ListRowItem> rowItems;

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

    @Override
    public void onListItemClick(ListView l, View view, int position, long id) {
        super.onListItemClick(l, view, position, id);
        Toast toast = Toast.makeText(getApplicationContext(),
                "Item " + (position + 1) + ": " + rowItems.get(position),
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }

    public void setListRowItems() {
        rowItems = new ArrayList<>();
        for (int i = 0; i < routes.getRoutes().size(); i++) {
            ListRowItem item = new ListRowItem("Bus " + i, routes.getRoutes().get(i).getName());
            rowItems.add(item);
        }
    }

    public void setRoutes(JSONArray theRoutes)
    {
        try {
            for (int x = 0; x < theRoutes.length(); x++)
            {
                Log.d("setRoutes","forLoop");
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
                Log.d("setStops","forLoop");
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
                        setListRowItems();
                        RouteArrayAdapter adapter = new RouteArrayAdapter(getApplicationContext(), rowItems);
                        listView = (ListView) findViewById(android.R.id.list);
                        listView.setAdapter(adapter);
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        // do nothing
                    }
                });
    }
}