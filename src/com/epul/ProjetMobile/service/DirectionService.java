package com.epul.ProjetMobile.service;


import android.location.Location;
import android.util.Log;
import com.epul.ProjetMobile.business.Place;
import com.epul.ProjetMobile.business.Route;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @Author Dimitri on 07/01/2016.
 * @Version 1.0
 */
public class DirectionService extends GoogleService {
    private DirectionServiceDelegate delegate;
    private boolean getPublicTransport;
    private List<Place> places;
    private ArrayList<Route> travels;

    public DirectionService(String apiKey, boolean getPublicTransport, List<Place> places) {
        super(apiKey);
        this.getPublicTransport = getPublicTransport;
        this.places = places;
    }

    public void init(Location location, DirectionServiceDelegate delegate) {
        this.location = location;
        this.delegate = delegate;
    }

    private String makeUrl() {
        StringBuilder urlString = new StringBuilder(
                "https://maps.googleapis.com/maps/api/directions/json?");
        urlString.append("origin=")
                .append(location.getLatitude()).append(",")
                .append(location.getLongitude())
                .append("&destination=")
                .append(places.get(places.size() - 1).getLatitude()).append(",")
                .append(places.get(places.size() - 1).getLongitude())
                .append("&avoid=highways&sensor=false")
                .append(places.size() > 0 ? "&waypoints=" : "");
        for (int i = 0; i < places.size() - 1; i++) {
            urlString.append(places.get(i).getLatitude()).append(",")
                    .append(places.get(i).getLongitude()).append("|");
        }
        urlString.append(getPublicTransport ?
                "&mode=transit&transit_mode=tram|bus|subway"
                : "&mode=walking")
                .append("&key=" + this.apiKey);

        return urlString.toString();
    }


    protected void findBestWay(String json) {
        travels = new ArrayList<>();
        try {
            JSONArray routes = new JSONObject(json).getJSONArray("routes");
            for (int i = 0; i < routes.length(); i++) {
                travels.add(new Route(routes.getJSONObject(i)));
            }
        } catch (JSONException e) {
            Logger.getLogger(PlacesService.class.getName()).log(Level.SEVERE,
                    null, e);
        }
    }

    @Override
    protected String doInBackground(String... params) {
        if (this.location != null) {
            String urlString = makeUrl();
            Log.d("com.epul.ProjetMobile", "Url : " + urlString);
            String json = getJSON(urlString);
            Log.d("com.epul.ProjetMobile", "Result : " + json);
            findBestWay(json);
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        this.delegate.displayWay(travels);
    }
}
