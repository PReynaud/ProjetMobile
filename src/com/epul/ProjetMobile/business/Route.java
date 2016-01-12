package com.epul.ProjetMobile.business;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.maps.model.LatLng;
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
    public List<LatLng> overview;
    public Waypoint[] waypoints;

    public Route(JSONObject jsonObject) {
        try {
            JSONObject leg = jsonObject.getJSONArray("legs").getJSONObject(0);
            this.distance = leg.getJSONObject("distance").getInt("value");
            this.duree = leg.getJSONObject("duration").getInt("value");
            this.overview = decodePoly(jsonObject.getJSONObject("overview_polyline").getString("points"));
            this.waypoints = new Waypoint[leg.getJSONArray("steps").length()];
            for (int i = 0; i < waypoints.length; i++)
                waypoints[i] = new Waypoint(leg.getJSONArray("steps").getJSONObject(i));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected Route(Parcel in) {
        distance = in.readInt();
        duree = in.readInt();
        waypoints = in.createTypedArray(Waypoint.CREATOR);
    }

    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(distance);
        dest.writeInt(duree);
        dest.writeArray(waypoints);
    }
}
