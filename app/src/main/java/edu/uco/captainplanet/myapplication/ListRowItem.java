package edu.uco.captainplanet.myapplication;


public class ListRowItem {
    private String bus;
    private String route;

    public ListRowItem(String bus, String route) {
        this.bus = bus;
        this.route = route;
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

    @Override
    public String toString() {
        return bus + "\n" + route;
    }
}
