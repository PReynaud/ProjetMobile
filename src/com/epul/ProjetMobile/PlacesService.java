package com.epul.ProjetMobile;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.IOException;
import java.net.HttpURLConnection;

public class PlacesService extends AsyncTask<String, Void, String> {
    private String apiKey;
    private List<Place> places;
    private Location location;
    private PlacesServiceDelegate delegate;

    public PlacesService(String apiKey) {
        this.apiKey = apiKey;
    }

    public void setLocation(Location location, PlacesServiceDelegate delegate) {
        this.location = location;
        this.delegate = delegate;
    }

    public List<Place> getPlaces() {
        return places;
    }

    private ArrayList<Place> createPlaces(String jsonResult) {
        try {
            JSONObject object = new JSONObject(jsonResult);
            JSONArray array = object.getJSONArray("results");

            ArrayList<Place> arrayList = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                try {
                    Place place = Place.jsonToObject((JSONObject) array.get(i));
                    arrayList.add(place);
                } catch (Exception e) {
                }
            }
            return arrayList;
        } catch (JSONException ex) {
            Logger.getLogger(PlacesService.class.getName()).log(Level.SEVERE,
                    null, ex);
        }
        return null;
    }

    // https://maps.googleapis.com/maps/api/place/search/json?location=28.632808,77.218276&radius=500&types=atm&sensor=false&key=apikey
    private String makeUrl(double latitude, double longitude) {
        StringBuilder urlString = new StringBuilder(
                "https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        urlString.append("location=");
        urlString.append(Double.toString(latitude));
        urlString.append(",");
        urlString.append(Double.toString(longitude));
        urlString.append("&radius=5000");
        urlString.append("&types=museum");
        urlString.append("&sensor=true&key=" + this.apiKey);

        return urlString.toString();
    }

    private String getJSON(String urlToGet) {
        StringBuilder builder = new StringBuilder();
        try {
            URL url = new URL(urlToGet);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            int statusCode = connection.getResponseCode();
            if (statusCode == 200) {
                InputStream content = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            } else {
                throw new Exception("" + statusCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    @Override
    protected String doInBackground(String... params) {
        if(this.location != null) {
            String urlString = this.makeUrl(this.location.getLatitude(), this.location.getLongitude());
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
        this.delegate.placeMarkers(this.places);
    }
}
