package edu.uco.captainplanet.myapplication;


public class ListRowItem {
    private String bus;
    private String route;
    private String busStops;

    public ListRowItem(String bus, String route, String busStops) {
        this.bus = bus;
        this.route = route;
        this.busStops = busStops;
    }

    public String getBus() {
        return bus;
    }

    public void setBus(String bus) {
        this.bus = bus;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getBusStops() {
        return busStops;
    }

    public void setBusStops(String busStops) {
        this.busStops = busStops;
    }

    @Override
    public String toString() {
        return bus + "\n" + route + "\n" + busStops;
    }
}
