package com.epul.ProjetMobile.service;

import android.location.Location;
import android.os.AsyncTask;
import com.epul.ProjetMobile.business.Place;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * @Author Dimitri on 07/01/2016.
 * @Version 1.0
 */
public abstract class GoogleService extends AsyncTask<String, Void, String> {
    protected Location location;
    protected String apiKey;
    protected List<Place> places;

    public GoogleService(String apiKey) {
        this.apiKey = apiKey;
    }

    public List<Place> getPlaces() {
        return places;
    }

    protected String getJSON(String urlToGet) {
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

}
