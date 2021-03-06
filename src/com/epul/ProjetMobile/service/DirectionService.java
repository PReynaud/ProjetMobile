package com.epul.ProjetMobile.service;


import android.content.Context;
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
    private String transportMode;
    private List<Place> places;
    private ArrayList<Route> travels;
    private Context context;

    public DirectionService(String apiKey, String transportMode, List<Place> places, Context context) {
        super(apiKey);
        this.transportMode = transportMode;
        this.places = places;
        this.context = context;
    }

    public void init(Location location, DirectionServiceDelegate delegate) {
        this.location = location;
        this.delegate = delegate;
    }

    private String makeUrl() {
        float distance = -1;
        float[] result = new float[1];
        Place destination = null;
        StringBuilder urlString = new StringBuilder(
                "https://maps.googleapis.com/maps/api/directions/json?");
        urlString.append("origin=")
                .append(location.getLatitude()).append(",")
                .append(location.getLongitude())
                .append("&avoid=highways&sensor=false");
        if (places == null || places.size() <= 0) {
            throw new NullPointerException();
        }
        if (places.size() > 1) {
            urlString.append("&waypoints=optimize:true");
            for (int i = 0; i < places.size(); i++) {
                Location.distanceBetween(location.getLatitude(), location.getLongitude(), places.get(i).getLatitude(), places.get(i).getLongitude(), result);
                if (result[0] > distance) {
                    if (destination != null)
                        urlString.append(destination.getLatitude())
                                .append(",")
                                .append(destination.getLongitude());
                    destination = places.get(i);
                    distance = result[0];
                } else {
                    urlString.append(places.get(i).getLatitude()).append(",")
                            .append(places.get(i).getLongitude());
                }
                urlString.append("|");
            }
            urlString.append("&destination=")
                    .append(destination.getLatitude())
                    .append(",")
                    .append(destination.getLongitude());
        } else if (places.size() > 0) {
            urlString.append("&destination=")
                    .append(places.get(0).getLatitude())
                    .append(",")
                    .append(places.get(0).getLongitude());
        }

        if (transportMode != null && transportMode != "") {
            urlString.append(transportMode);
        } else {
            urlString.append("&mode=walking");
        }
        urlString.append("&language=fr&key=" + this.apiKey);

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
    protected void onPreExecute() {
        super.onPreExecute();
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
