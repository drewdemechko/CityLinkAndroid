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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class ListRoutesActivity extends ListActivity {
    private Routes routes;
    private ArrayList<BusStop> stops;
    private ArrayList<Bus> buses;

    private ListView listView;
    private List<ListRowItem> rowItems;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes);

        routes = new Routes();
        stops = new ArrayList<>();
        buses = new ArrayList<>();

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
        if (!rowItems.get(position).getBusStops().equals("")) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Ordered Bus Stops: " + rowItems.get(position).getBusStops(),
                    Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 200);
            toast.show();
        }
    }

    public void setListRowItems() {
        rowItems = new ArrayList<>();
        for (int i = 0; i < buses.size(); i++) {
            // match buses to routes
            for (int j = 0; j < routes.getRoutes().size(); j++) {
                if (buses.get(i).getRoute().equals(routes.getRoutes().get(j).getName())) {
                    // get ordered bus stops based on route
                    ArrayList<BusStop> busStops = routes.getRoutes().get(j).getOrderedStops();
                    StringBuilder sbBusStops = new StringBuilder();
                    for (int k = 0; k < busStops.size(); k++) {
                        BusStop busStop = busStops.get(k);
                        sbBusStops.append(busStop.getName());
                        if (k != busStops.size() - 1) {
                            sbBusStops.append(" - ");
                        }
                    }

                    String strListBusStops = "";
                    if (sbBusStops.length() > 0) {
                        strListBusStops = sbBusStops.toString();
                    }

                    ListRowItem item = new ListRowItem(buses.get(i).getName(), routes.getRoutes().get(j).getName(), strListBusStops);
                    rowItems.add(item);
                    break; // move onto next bus
                }
            }
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

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(5000);
        client.get("https://uco-edmond-bus.herokuapp.com/api/busservice/buses"
                , new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray theStops) {
                        setBuses(theStops);
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        // do nothing
                    }
                });
    }

    public void setBuses(JSONArray theBuses)
    {
        try {
            for (int x = 0; x < theBuses.length(); x++)
            {
                Log.d("setBuses","forLoop");
                Bus currentBus = new Bus();
                JSONObject busJSON = theBuses.getJSONObject(x);

                if (busJSON.has("name")) {
                    currentBus.setName(busJSON.getString("name"));
                }
                if (busJSON.has("driver")) {
                    currentBus.setDriver(busJSON.getString("driver"));
                }
                if (busJSON.has("route")) {
                    currentBus.setRoute(busJSON.getString("route"));
                }
                if (busJSON.has("lastStop")) {
                    currentBus.setLastStop(busJSON.getString("lastStop"));
                }
                if (busJSON.has("active")) {
                    currentBus.setActive(busJSON.getBoolean("active"));
                }
                if (busJSON.has("lastActive")) {
                    String dateStr = busJSON.getString("lastActive");
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                    Date lastActiveDate = sdf.parse(dateStr);
                    currentBus.setLastActive(lastActiveDate);
                }
                buses.add(currentBus);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(5000);
        client.get("https://uco-edmond-bus.herokuapp.com/api/busservice/buses"
                , new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray theStops) {
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