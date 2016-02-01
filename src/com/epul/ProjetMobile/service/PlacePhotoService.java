package com.epul.ProjetMobile.service;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by Pierre on 15/01/2016.
 */
public class PlacePhotoService extends AsyncTask<String, Void, String> {
    private PlacePhotoServiceDelegate delegate;
    private Drawable result;
    private String apiKey;
    private String photoId;
    private int width;

    public PlacePhotoService(PlacePhotoServiceDelegate delegate, String apiKey, String photoId, int width) {
        this.delegate = delegate;
        this.apiKey = apiKey;
        this.photoId = photoId;
        this.width = width;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            String url = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=";
            url += this.width;
            url += "&photoreference=";
            url += photoId;
            url += "&key=";
            url += apiKey;
            Log.d("com.epul.ProjetMobile", "Url : " + url);

            InputStream is = (InputStream) new URL(url).getContent();
            this.result = Drawable.createFromStream(is, "place image");
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (result != null) {
            delegate.loadPhoto(result);
        }
    }
}
