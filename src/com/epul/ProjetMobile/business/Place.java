package com.epul.ProjetMobile.business;

import android.os.Parcel;
import android.os.Parcelable;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Place implements Parcelable {
    public static final Parcelable.Creator<Place> CREATOR = new Parcelable.Creator<Place>() {

        @Override
        public Place createFromParcel(Parcel source) {
            return new Place(source);
        }

        @Override
        public Place[] newArray(int size) {
            return new Place[size];
        }
    };
    private String id;
    private String icon;
    private String name;
    private String vicinity;
    private Double latitude;
    private Double longitude;

    public Place() {
    }

    public Place(Parcel in) {
        this.id = in.readString();
        this.icon = in.readString();
        this.name = in.readString();
        this.vicinity = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
    }

    public static Place jsonToObject(JSONObject JSONResult) {
        try {
            Place result = new Place();
            JSONObject geometry = (JSONObject) JSONResult.get("geometry");
            JSONObject location = (JSONObject) geometry.get("location");
            result.setLatitude((Double) location.get("lat"));
            result.setLongitude((Double) location.get("lng"));
            result.setIcon(JSONResult.getString("icon"));
            result.setName(JSONResult.getString("name"));
            result.setVicinity(JSONResult.getString("vicinity"));
            result.setId(JSONResult.getString("id"));
            return result;
        } catch (JSONException ex) {
            Logger.getLogger(Place.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    @Override
    public String toString() {
        return "Place{" + "id=" + id + ", icon=" + icon + ", name=" + name + ", latitude=" + latitude + ", longitude=" + longitude + '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(icon);
        dest.writeString(name);
        dest.writeString(vicinity);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Place place = (Place) o;

        return getId() != null ? getId().equals(place.getId()) :
                place.getId() == null && (getIcon() != null ? getIcon().equals(place.getIcon()) :
                        place.getIcon() == null && (getName() != null ? getName().equals(place.getName()) :
                                place.getName() == null && (getVicinity() != null ? getVicinity().equals(place.getVicinity()) :
                                        place.getVicinity() == null && (getLatitude() != null ? getLatitude().equals(place.getLatitude()) :
                                                place.getLatitude() == null && (getLongitude() != null ? getLongitude().equals(place.getLongitude()) :
                                                        place.getLongitude() == null)))));

    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getIcon() != null ? getIcon().hashCode() : 0);
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getVicinity() != null ? getVicinity().hashCode() : 0);
        result = 31 * result + (getLatitude() != null ? getLatitude().hashCode() : 0);
        result = 31 * result + (getLongitude() != null ? getLongitude().hashCode() : 0);
        return result;
    }
}
