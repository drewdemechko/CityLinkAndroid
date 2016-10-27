package edu.uco.captainplanet.myapplication;

import java.util.ArrayList;

/**
 * Created by drenf on 10/26/2016.
 */

public class Routes {
    private ArrayList<Route> routes;

    public Routes() {
        routes = new ArrayList<>();
    }

    public ArrayList<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(ArrayList<Route> routes) {
        this.routes = routes;
    }

    public void addRoute(Route route)
    {
        routes.add(route);
    }

    public BusStop getNextStop(String lastStop, String route) {
        for(int x = 0; x < routes.size() ; x++)
        {
            if(routes.get(x).getName().equals(route))
            {
                return routes.get(x).getNextStop(lastStop);
            }
        }



        return null;
    }
}
