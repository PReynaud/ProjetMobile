package com.epul.ProjetMobile.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.epul.ProjetMobile.R;
import com.epul.ProjetMobile.adapter.InfoPopup;
import com.epul.ProjetMobile.business.DetailledPlace;
import com.epul.ProjetMobile.service.PlaceDetailService;
import com.epul.ProjetMobile.service.PlaceDetailServiceDelegate;
import com.epul.ProjetMobile.service.PlacePhotoService;
import com.epul.ProjetMobile.service.PlacePhotoServiceDelegate;

/**
 * Created by Pierre on 27/12/2015.
 */
public class DetailActivity extends AppCompatActivity implements PlaceDetailServiceDelegate, PlacePhotoServiceDelegate {
    private TextView tName;
    private TextView tDistance;
    private TextView tAdresse;
    private TextView tDescription;
    private ImageView tImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);

        RelativeLayout mToolbar = (RelativeLayout) findViewById(R.id.detail_toolbar);

        tDistance = (TextView) findViewById(R.id.distanceDetail);
        tAdresse = (TextView) findViewById(R.id.adresseDetail);
        tDescription = (TextView) findViewById(R.id.descriptionDetail);
        tName = (TextView) findViewById(R.id.nameDetail);
        tImage = (ImageView) findViewById(R.id.placeImage);

        Intent i = getIntent();
        String placeID = i.getStringExtra(InfoPopup.PLACE_ID);

        PlaceDetailService service = new PlaceDetailService(getResources().getString(R.string.google_places_key), placeID, this);
        service.execute();
    }

    @Override
    public void loadDetails(DetailledPlace place) {
        tName.setText(place.getName());
        tAdresse.setText(place.getAddress());

        //tImage.setImageDrawable(loadImageFromId(place.getPhotoUrls().get(0)));
        if (place.getPhotoUrls().size() == 0) {
            PlacePhotoService service = new PlacePhotoService(this, getResources().getString(R.string.google_places_key), place.getPhotoUrls().get(0));
            service.execute();
        }
    }

    @Override
    public void loadPhoto(Drawable photo) {
        tImage.setImageDrawable(photo);
    }
}
