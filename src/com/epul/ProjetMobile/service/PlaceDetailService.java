package com.epul.ProjetMobile.service;

import android.util.Log;
import com.epul.ProjetMobile.business.DetailledPlace;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Pierre on 15/01/2016.
 */
public class PlaceDetailService extends GoogleService {
    private String placeId;
    private DetailledPlace detailledPlace;
    private PlaceDetailServiceDelegate delegate;

    public PlaceDetailService(String apiKey, String placeId, PlaceDetailServiceDelegate delegate) {
        super(apiKey);
        this.placeId = placeId;
        this.delegate = delegate;
    }

    @Override
    protected String doInBackground(String... params) {
        String urlString = this.makeUrl(this.placeId);
        Log.d("com.epul.ProjetMobile", "Url : " + urlString);
        String json = getJSON(urlString);

        JSONObject object = null;
        try {
            object = new JSONObject(json);
            detailledPlace = DetailledPlace.jsonToObject(object);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String makeUrl(String placeId) {
        StringBuilder urlString = new StringBuilder(
                "https://maps.googleapis.com/maps/api/place/details/json?placeid=");
        urlString.append(placeId);
        urlString.append("&key=" + this.apiKey);
        return urlString.toString();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        this.delegate.loadDetails(detailledPlace);
    }
}
