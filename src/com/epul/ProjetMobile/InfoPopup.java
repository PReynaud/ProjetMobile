package com.epul.ProjetMobile;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by Pierre on 26/12/2015.
 */
public class InfoPopup implements GoogleMap.InfoWindowAdapter{
    Context context;
    View view;
    MapLayout layout;

    public InfoPopup(Context context, View view, MapLayout layout) {
        this.context = context;
        this.view = view;
        this.layout = layout;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        ((TextView) view.findViewById(R.id.place_name)).setText(marker.getTitle());
        layout.setMarkerWithInfoWindow(marker, view);
        view.findViewById(R.id.buttonDetail).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_UP:
                        Intent intent = new Intent(context, DetailActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
        view.findViewById(R.id.buttonAdd).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_UP:
                        Toast.makeText(context,
                                "Button Ajouter", Toast.LENGTH_SHORT)
                                .show();
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
        return view;
    }
}
