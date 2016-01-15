package com.epul.ProjetMobile.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import com.epul.ProjetMobile.R;
import com.epul.ProjetMobile.business.Place;
import com.epul.ProjetMobile.tools.MapLayout;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.Map;

/**
 * Created by Pierre on 26/12/2015.
 */
public class InfoPopup implements GoogleMap.InfoWindowAdapter{
    private Context context;
    private View view;
    private MapLayout layout;
    private Map<Marker, Map<Place, Boolean>> places;

    public InfoPopup(Context context, View view, MapLayout layout, Map<Marker, Map<Place, Boolean>> places) {
        this.context = context;
        this.view = view;
        this.layout = layout;
        this.places = places;
    }

    public Context getContext() {
        return context;
    }

    public void actionAjouter(Marker marker) {
        boolean test = places.get(marker).values().iterator().next();
        places.get(marker).entrySet().iterator().next().setValue(!test);
    }

    public void actionDetail(Marker marker) {

    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        final Marker clonedMarker = marker;
        Place place = places.get(marker).entrySet().iterator().next().getKey();
        float rating = place.getRating();

        if (rating < 0) {
            view.findViewById(R.id.ratingBarPopup).setVisibility(View.GONE);
            ((TextView) view.findViewById(R.id.ratingText)).setText("Pas encore de note");
        } else {
            view.findViewById(R.id.ratingBarPopup).setVisibility(View.VISIBLE);
            ((TextView) view.findViewById(R.id.ratingText)).setText(Float.toString(rating));
            ((RatingBar) view.findViewById(R.id.ratingBarPopup)).setRating(rating);
        }


        ((TextView) view.findViewById(R.id.place_name)).setText(marker.getTitle());
        layout.setMarkerWithInfoWindow(marker, view);
        Button buttonDetail = (Button) view.findViewById(R.id.buttonDetail),
                buttonAjouter = (Button) view.findViewById(R.id.buttonAdd);
        buttonDetail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_UP:
                        actionDetail(clonedMarker);
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
        boolean isAdded = places.get(marker).values().iterator().next();
        buttonAjouter.setText(view.getResources().getString(isAdded ? R.string.AddedButton : R.string.AddButton));
        buttonAjouter.getBackground().setColorFilter(ContextCompat.getColor(context, isAdded ? R.color.warning : R.color.primary), PorterDuff.Mode.MULTIPLY);
        buttonAjouter.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_UP:
                        actionAjouter(clonedMarker);
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
