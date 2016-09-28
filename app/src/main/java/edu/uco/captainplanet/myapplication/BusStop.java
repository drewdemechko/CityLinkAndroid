package edu.uco.captainplanet.myapplication;

/**
 * Created by drenf on 9/28/2016.
 */
public class BusStop {

    String name;
    String firstCrossStreet;
    String secondCrossStreet;
    double lat;
    double longi;
    boolean inactive;


    public BusStop(String name, String firstCrossStreet, String secondCrossStreet, double lat, double longi, boolean inactive) {
        this.name = name;
        this.firstCrossStreet = firstCrossStreet;
        this.secondCrossStreet = secondCrossStreet;
        this.lat = lat;
        this.longi = longi;
        this.inactive = inactive;
    }

    public BusStop()
    {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstCrossStreet() {
        return firstCrossStreet;
    }

    public void setFirstCrossStreet(String firstCrossStreet) {
        this.firstCrossStreet = firstCrossStreet;
    }

    public String getSecondCrossStreet() {
        return secondCrossStreet;
    }

    public void setSecondCrossStreet(String secondCrossStreet) {
        this.secondCrossStreet = secondCrossStreet;
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

    public boolean isInactive() {
        return inactive;
    }

    public void setInactive(boolean inactive) {
        this.inactive = inactive;
    }
}
