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
public class Waypoint implements Parcelable {
    public static final Creator<Waypoint> CREATOR = new Creator<Waypoint>() {
        @Override
        public Waypoint createFromParcel(Parcel in) {
            return new Waypoint(in);
        }

        @Override
        public Waypoint[] newArray(int size) {
            return new Waypoint[size];
        }
    };
    public int distance;
    public int duree;
    public String instruction;
    public TravelMode travelMode;
    public LatLng[] polyline;

    public Waypoint(JSONObject jsonObject) {
        try {
            this.distance = jsonObject.getJSONObject("distance").getInt("value");
            this.duree = jsonObject.getJSONObject("duration").getInt("value");
            this.instruction = jsonObject.getString("html_instructions");
            this.travelMode = TravelMode.getByName(jsonObject.getString("travel_mode"));
            List<LatLng> test = decodePoly(jsonObject.getJSONObject("polyline").getString("points"));
            this.polyline = new LatLng[test.size()];
            for (int i = 0; i < this.polyline.length; i++) {
                this.polyline[i]=test.get(i);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected Waypoint(Parcel in) {
        distance = in.readInt();
        duree = in.readInt();
        instruction = in.readString();
        travelMode = TravelMode.getByName(in.readString());
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Waypoint)) return false;

        Waypoint waypoint = (Waypoint) o;

        return distance == waypoint.distance &&
                duree == waypoint.duree &&
                (instruction != null ? instruction.equals(waypoint.instruction) :
                        waypoint.instruction == null && travelMode == waypoint.travelMode);

    }

    @Override
    public int hashCode() {
        int result = distance;
        result = 31 * result + duree;
        result = 31 * result + (instruction != null ? instruction.hashCode() : 0);
        result = 31 * result + travelMode.hashCode();
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(distance);
        dest.writeInt(duree);
        dest.writeString(instruction);
        dest.writeString(travelMode.mode);
    }
}
