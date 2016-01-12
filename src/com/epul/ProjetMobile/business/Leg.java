package com.epul.ProjetMobile.business;

import android.os.Parcel;
import android.os.Parcelable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Dimitri RODARIE on 12/01/2016.
 * @version 1.0
 */
public class Leg implements Parcelable{
    public int distance;
    public int duree;
    public Waypoint[] waypoints;

    protected Leg(Parcel in) {
        distance = in.readInt();
        duree = in.readInt();
        waypoints = in.createTypedArray(Waypoint.CREATOR);
    }

    public Leg(JSONObject leg){
        try {
            JSONArray way = leg.getJSONArray("steps");
            waypoints = new Waypoint[way.length()];
            this.distance = leg.getJSONObject("distance").getInt("value");
            this.duree = leg.getJSONObject("duration").getInt("value");
            for (int i = 0; i < waypoints.length; i++)
                waypoints[i] = new Waypoint(way.getJSONObject(i));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static final Creator<Leg> CREATOR = new Creator<Leg>() {
        @Override
        public Leg createFromParcel(Parcel in) {
            return new Leg(in);
        }

        @Override
        public Leg[] newArray(int size) {
            return new Leg[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(distance);
        dest.writeInt(duree);
        dest.writeTypedArray(waypoints, flags);
    }
}
