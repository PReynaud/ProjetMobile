package com.epul.ProjetMobile.business;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Pierre on 08/01/2016.
 */
public class DetailledPlace extends Place {
    private String address;
    private String phoneNumber;
    private Boolean openNow;
    private String openingHours;
    private List<String> photoUrls;
    private List<Review> reviews;
    private String website;

    public DetailledPlace(){

    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Boolean getOpenNow() {
        return openNow;
    }

    public void setOpenNow(Boolean openNow) {
        this.openNow = openNow;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }

    public List<String> getPhotoUrls() {
        return photoUrls;
    }

    public void setPhotoUrls(List<String> photoUrls) {
        this.photoUrls = photoUrls;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public static DetailledPlace jsonToObject(JSONObject JSONResult) {
        try {
            DetailledPlace result = new DetailledPlace();
            JSONObject geometry = (JSONObject) JSONResult.get("geometry");
            JSONObject location = (JSONObject) geometry.get("location");
            result.setLatitude((Double) location.get("lat"));
            result.setLongitude((Double) location.get("lng"));
            result.setIcon(JSONResult.getString("icon"));
            result.setName(JSONResult.getString("name"));
            result.setVicinity(JSONResult.getString("vicinity"));
            result.setId(JSONResult.getString("id"));
            result.setAddress(JSONResult.getString("formatted_address"));
            result.setPhoneNumber(JSONResult.getString("formatted_phone_number"));
            result.setOpenNow(JSONResult.getString("open_now") == "true");
            result.setOpeningHours(JSONResult.getString("weekday_text"));

            //TODO: rajouter récupération des urls et des reviews

            result.setWebsite(JSONResult.getString("website"));

            return result;
        } catch (JSONException ex) {
            Logger.getLogger(Place.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}