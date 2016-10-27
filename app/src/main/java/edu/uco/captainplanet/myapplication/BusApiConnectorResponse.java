package edu.uco.captainplanet.myapplication;

import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;

/**
 * Created by drenf on 10/27/2016.
 */

public interface BusApiConnectorResponse {
    void processFinish(ArrayList<Bus> output);
}
