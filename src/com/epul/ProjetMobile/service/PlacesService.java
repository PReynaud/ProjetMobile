package com.epul.ProjetMobile.service;

import android.location.Location;
import android.util.Log;
import com.epul.ProjetMobile.business.Place;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PlacesService extends GoogleService {
    private PlacesServiceDelegate delegate;
    private Location location;
    private ArrayList<String> placeTypes;
    private int radius = 5000;

    public PlacesService(String apiKey, PlacesServiceDelegate delegate) {
        super(apiKey);
        this.delegate = delegate;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setPlaceTypes(ArrayList<String> placeTypes) {
        this.placeTypes = placeTypes;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    private ArrayList<Place> createPlaces(String jsonResult) {
        JSONArray array;
        try {
            JSONObject object = new JSONObject(jsonResult);
            array = object.getJSONArray("results");
        } catch (JSONException ex) {
            Logger.getLogger(PlacesService.class.getName()).log(Level.SEVERE,
                    null, ex);
            return null;
        }
        ArrayList<Place> arrayList = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            try {
                Place place = Place.jsonToObject((JSONObject) array.get(i));
                arrayList.add(place);
            } catch (Exception ignored) {
            }
        }
        return arrayList;
    }

    // https://maps.googleapis.com/maps/api/place/search/json?location=28.632808,77.218276&radius=500&types=atm&sensor=false&key=apikey
    private String makeUrl(double latitude, double longitude, int radius) {
        StringBuilder urlString = new StringBuilder(
                "https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        urlString.append("location=");
        urlString.append(Double.toString(latitude));
        urlString.append(",");
        urlString.append(Double.toString(longitude));
        urlString.append("&radius=");
        urlString.append(radius);
        urlString.append("&rankBy=distance");

        if (placeTypes == null || placeTypes.size() == 0) {
            urlString.append("&types=aquarium|art_gallery|city_hall|museum|park|place_of_worship|zoo|establishment|premise");
        } else {
            urlString.append("&types=");
            for (int i = 0; i < placeTypes.size(); i++) {
                if (i > 0) {
                    urlString.append("|");
                }
                urlString.append(placeTypes.get(i));
            }
        }
        //urlString.append("style=feature:poi.attraction");
        urlString.append("&sensor=true&key=" + this.apiKey);
        return urlString.toString();
    }

    @Override
    protected String doInBackground(String... params) {
        if(this.location != null) {
            String urlString = this.makeUrl(this.location.getLatitude(), this.location.getLongitude(), radius);
            Log.d("com.epul.ProjetMobile", "Url : " + urlString);
            String json = getJSON(urlString);
            Log.d("com.epul.ProjetMobile", "Result : " + json);
            this.places = this.createPlaces(json);
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (this.places != null) {
            this.delegate.placeMarkers(this.places);
            this.delegate.enableSearch();
        }
    }
}
