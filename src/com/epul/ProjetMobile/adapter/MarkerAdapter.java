package com.epul.ProjetMobile.adapter;

import com.epul.ProjetMobile.business.Place;
import com.google.android.gms.maps.model.Marker;

/**
 * @Author Dimitri on 04/01/2016.
 * @Version 1.0
 */
public class MarkerAdapter {
    private Place place;
    private Marker marker;

    public MarkerAdapter(Place place) {
        this.place = place;
    }

    public Place getPlace() {
        return place;
    }

    public Marker getMarker() {
        return marker;
    }
}
