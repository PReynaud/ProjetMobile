package com.epul.ProjetMobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.epul.ProjetMobile.R;
import com.epul.ProjetMobile.adapter.InfoPopup;
import com.epul.ProjetMobile.business.DetailledPlace;
import com.epul.ProjetMobile.service.PlaceDetailService;
import com.epul.ProjetMobile.service.PlaceDetailServiceDelegate;

/**
 * Created by Pierre on 27/12/2015.
 */
public class DetailActivity extends AppCompatActivity implements PlaceDetailServiceDelegate {
    private TextView tName;
    private TextView tDistance;
    private TextView tAdresse;
    private TextView tDescription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);

        RelativeLayout mToolbar = (RelativeLayout) findViewById(R.id.detail_toolbar);

        tDistance = (TextView) findViewById(R.id.distanceDetail);
        tAdresse = (TextView) findViewById(R.id.adresseDetail);
        tDescription = (TextView) findViewById(R.id.descriptionDetail);
        tName = (TextView) findViewById(R.id.nameDetail);

        Intent i = getIntent();
        String placeID = i.getStringExtra(InfoPopup.PLACE_ID);

        PlaceDetailService service = new PlaceDetailService(getResources().getString(R.string.google_places_key), placeID, this);
        service.execute();
    }

    @Override
    public void loadDetails(DetailledPlace place) {
        tName.setText(place.getName());
        tAdresse.setText(place.getAddress());
    }
}
