package com.epul.ProjetMobile.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.epul.ProjetMobile.R;
import com.epul.ProjetMobile.service.PlaceDetailService;
import com.epul.ProjetMobile.service.PlaceDetailServiceDelegate;

/**
 * Created by Pierre on 27/12/2015.
 */
public class DetailActivity extends AppCompatActivity implements PlaceDetailServiceDelegate {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);

        RelativeLayout mToolbar = (RelativeLayout) findViewById(R.id.detail_toolbar);

        TextView tName = (TextView) findViewById(R.id.nameDetail);
        TextView tDistance = (TextView) findViewById(R.id.distanceDetail);
        TextView tAdresse = (TextView) findViewById(R.id.adresseDetail);
        TextView tDescription = (TextView) findViewById(R.id.descriptionDetail);

        //TODO: récupérer l'id depuis l'intent
        String placeID = "";

        PlaceDetailService service = new PlaceDetailService(getResources().getString(R.string.google_places_key), placeID, this);
        service.execute();
    }

    @Override
    public void loadDetails() {

    }
}
