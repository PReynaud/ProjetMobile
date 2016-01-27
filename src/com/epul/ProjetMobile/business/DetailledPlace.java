package com.epul.ProjetMobile.business;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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
        this.photoUrls = new ArrayList<>();
        this.reviews = new ArrayList<>();
    }

    public static DetailledPlace jsonToObject(JSONObject jsonResult) {
        try {
            //TODO: rajouter vérification de la présence de certains objets
            DetailledPlace result = new DetailledPlace();
            JSONObject resultNode = (JSONObject) jsonResult.get("result");
            JSONObject geometry = (JSONObject) resultNode.get("geometry");
            JSONObject location = (JSONObject) geometry.get("location");
            result.setLatitude((Double) location.get("lat"));
            result.setLongitude((Double) location.get("lng"));
            result.setIcon(resultNode.getString("icon"));
            result.setName(resultNode.getString("name"));
            result.setVicinity(resultNode.getString("vicinity"));
            result.setId(resultNode.getString("id"));
            result.setAddress(resultNode.getString("formatted_address"));

            if(resultNode.has("formatted_phone_number")){
                result.setPhoneNumber(resultNode.getString("formatted_phone_number"));
            }

            if (resultNode.has("opening_hours")) {
                JSONObject openingHoursNode = (JSONObject) resultNode.get("opening_hours");
                result.setOpenNow(openingHoursNode.getString("open_now") == "true");
                result.setOpeningHours(openingHoursNode.getString("weekday_text"));
            }
            //TODO: rajouter récupération des reviews

            if(resultNode.has("photos")){
                JSONArray photosNode = (JSONArray) resultNode.get("photos");
                for (int i = 0; i < photosNode.length(); i++) {
                    result.getPhotoUrls().add(photosNode.getJSONObject(i).getString("photo_reference"));
                }
            }

            if(resultNode.has("website")){
                result.setWebsite(resultNode.getString("website"));
            }

            return result;
        } catch (JSONException ex) {
            Logger.getLogger(Place.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
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

    public boolean hasOpeningHours(){
        return this.openingHours != null;
    }

    public boolean hasWebsite(){
        return this.website != null;
    }

    public boolean hasPhotos(){
        return this.photoUrls != null && this.photoUrls.size() != 0;
    }

    public boolean hasPhoneNumber(){
        return this.phoneNumber != null;
    }
}
