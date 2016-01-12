package com.epul.ProjetMobile.business;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.maps.model.LatLng;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author Dimitri on 09/01/2016.
 * @Version 1.0
 */
public class Route implements Parcelable {
    public static final Creator<Route> CREATOR = new Creator<Route>() {
        @Override
        public Route createFromParcel(Parcel in) {
            return new Route(in);
        }

        @Override
        public Route[] newArray(int size) {
            return new Route[size];
        }
    };
    public int distance;
    public int duree;
    public Leg[] legs;

    public Route(JSONObject jsonObject) {
        try {
            JSONArray legs = jsonObject.getJSONArray("legs");
            this.legs = new Leg[legs.length()];
            for (int i = 0; i < this.legs.length; i++) {
                this.legs[i] = new Leg(legs.getJSONObject(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected Route(Parcel in) {
        legs = in.createTypedArray(Leg.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeArray(legs);
    }
}
