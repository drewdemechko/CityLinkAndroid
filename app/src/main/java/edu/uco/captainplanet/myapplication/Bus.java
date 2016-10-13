package edu.uco.captainplanet.myapplication;

import java.util.Date;

/**
 * Created by drenf on 10/11/2016.
 */

public class Bus {
    private String name;
    private String driver;
    private String route;
    private boolean active;
    private String lastStop;
    private Date lastActive;
    private double lat;
    private double longi;

    public Bus(String name,String route, String driver, String lastStop, boolean active, Date lastActive, double lat, double longi) {
        this.name = name;
        this.route = route;
        this.driver = driver;
        this.lastStop = lastStop;
        this.active = active;
        this.lastActive = lastActive;
        this.lat = lat;
        this.longi = longi;
    }

    public Bus() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getLastStop() {
        return lastStop;
    }

    public void setLastStop(String lastStop) {
        this.lastStop = lastStop;
    }

    public Date getLastActive() {
        return lastActive;
    }

    public void setLastActive(Date lastActive) {
        this.lastActive = lastActive;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLongi() {
        return longi;
    }

    public void setLongi(double longi) {
        this.longi = longi;
    }
}
