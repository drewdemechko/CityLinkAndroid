package edu.uco.captainplanet.myapplication;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by drenf on 10/26/2016.
 */

public class Route {
    private int id;
    private String name;
    private ArrayList<BusStop> orderedStops;

    public Route() {
        orderedStops = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOrderedStops(JSONArray stops, ArrayList<BusStop> stopObjects)
    {
        for(int x = 0; x < stops.length() ; x++)
        {
            try {
                if(stops.getJSONObject(x).has("stopName"))
                {
                    String stopName = stops.getJSONObject(x).getString("stopName");
                    for(int y = 0 ; y < stopObjects.size() ; y++)
                    {
                        if(stopName.equals(stopObjects.get(y).getName()))
                        {
                            orderedStops.add(stopObjects.get(y));
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    public BusStop getNextStop(String lastStop) {
        for(int x = 0; x < orderedStops.size() ; x++)
        {
            if(orderedStops.get(x).getName().equals(lastStop))
            {
                if((x+1) == orderedStops.size())
                {
                    return orderedStops.get(0);
                }
                else
                {
                    return orderedStops.get(x+1);
                }

            }
        }

        return null;
    }
}
