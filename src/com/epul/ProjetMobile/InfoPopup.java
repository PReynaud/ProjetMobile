package com.epul.ProjetMobile;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by Pierre on 26/12/2015.
 */
public class InfoPopup implements GoogleMap.InfoWindowAdapter {
    Context context;

    public InfoPopup(Context context) {
        this.context = context;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.info_popup, null);

        TextView placeName = (TextView) v.findViewById(R.id.place_name);
        placeName.setText(marker.getTitle());

        TextView placeDistance = (TextView) v.findViewById(R.id.place_distance);
        placeDistance.setText("10m");

        ImageView imagePlaceRating = (ImageView) v.findViewById(R.id.image_place_rating);
        imagePlaceRating.setImageResource(R.drawable.stars_4_5);

        TextView placeRating = (TextView) v.findViewById(R.id.place_rating);
        placeRating.setText("4.3");

        Button addButton = (Button) v.findViewById(R.id.buttonAdd);
        Button detailButton = (Button) v.findViewById(R.id.buttonDetail);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        detailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("InfoPopup", "onClick ");
                Intent intentMain = new Intent(InfoPopup.this.context ,
                        DetailActivity.class);
                InfoPopup.this.context.startActivity(intentMain);
            }
        });

        return v;
    }
}
