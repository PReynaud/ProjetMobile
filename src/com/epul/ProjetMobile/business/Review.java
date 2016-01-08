package com.epul.ProjetMobile.business;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Pierre on 08/01/2016.
 */
public class Review {
    private String authorName;
    private String authorUrl;
    private String language;
    private String rating;
    private String text;
    private String time;

    public Review() {
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorUrl() {
        return authorUrl;
    }

    public void setAuthorUrl(String authorUrl) {
        this.authorUrl = authorUrl;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public static Review jsonToObject(JSONObject JSONResult) {
        try {
            Review result = new Review();
            result.setAuthorName(JSONResult.getString("author_name"));
            result.setAuthorUrl(JSONResult.getString("author_url"));
            result.setLanguage(JSONResult.getString("language"));
            result.setRating(JSONResult.getString("rating"));
            result.setText(JSONResult.getString("text"));
            result.setTime(JSONResult.getString("time"));

            return result;
        } catch (JSONException ex) {
            Logger.getLogger(Place.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
