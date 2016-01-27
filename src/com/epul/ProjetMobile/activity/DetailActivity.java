package com.epul.ProjetMobile.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.epul.ProjetMobile.R;
import com.epul.ProjetMobile.adapter.InfoPopup;
import com.epul.ProjetMobile.business.DetailledPlace;
import com.epul.ProjetMobile.service.PlaceDetailService;
import com.epul.ProjetMobile.service.PlaceDetailServiceDelegate;
import com.epul.ProjetMobile.service.PlacePhotoService;
import com.epul.ProjetMobile.service.PlacePhotoServiceDelegate;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Created by Pierre on 27/12/2015.
 */
public class DetailActivity extends AppCompatActivity implements PlaceDetailServiceDelegate, PlacePhotoServiceDelegate {
    private TextView tName;
    private TextView tDistance;
    private TextView tAdresse;
    private TextView tOpeningHours;
    private TextView tDescription;
    private ImageView tImage;
    private Space spaceImage;
    private ProgressDialog progressDialog;
    private int width;
    private double userLocationLatitude;
    private double userLocationLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);

        RelativeLayout mToolbar = (RelativeLayout) findViewById(R.id.detail_toolbar);
        ImageView returnIcon = (ImageView) findViewById(R.id.return_icon);
        returnIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tDistance = (TextView) findViewById(R.id.distanceDetail);
        tAdresse = (TextView) findViewById(R.id.adresseDetail);
        tDescription = (TextView) findViewById(R.id.descriptionDetail);
        tName = (TextView) findViewById(R.id.nameDetail);
        tImage = (ImageView) findViewById(R.id.placeImage);
        tOpeningHours = (TextView) findViewById(R.id.openingHours);
        spaceImage = (Space) findViewById(R.id.photoSpace);

        Intent i = getIntent();
        String placeID = i.getStringExtra(InfoPopup.PLACE_ID);

        this.userLocationLatitude = i.getDoubleExtra(InfoPopup.USER_LOCATION_LATITUDE, 0.0);
        this.userLocationLongitude = i.getDoubleExtra(InfoPopup.USER_LOCATION_LONGITUDE, 0.0);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.LoadInProgress));
        progressDialog.show();

        PlaceDetailService service = new PlaceDetailService(getResources().getString(R.string.google_places_key), placeID, this);
        service.execute();
    }

    @Override
    public void loadDetails(DetailledPlace place) {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        this.width = size.x;

        if (place.hasPhotos() && place.getPhotoUrls().size() > 0) {
            PlacePhotoService service = new PlacePhotoService(this, getResources().getString(R.string.google_places_key), place.getPhotoUrls().get(0), width);
            service.execute();
        }
        else{
            //TODO : enlever l'espace des photos
        }


        tName.setText(place.getName());
        tAdresse.setText(place.getAddress());
        this.setUserLocation(place);
        this.setOpeningHours(place);

        //TODO: Charger d'autres choses ici
    }

    @Override
    public void loadPhoto(Drawable photo) {
        Bitmap bitmap = ((BitmapDrawable) photo).getBitmap();
        Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, width, width, true));

        ViewGroup.LayoutParams layout = spaceImage.getLayoutParams();
        layout.height = d.getMinimumHeight();
        layout.width = d.getMinimumWidth();
        spaceImage.setLayoutParams(layout);

        ViewGroup.LayoutParams layoutImage = tImage.getLayoutParams();
        layoutImage.height = d.getMinimumHeight();
        layoutImage.width = d.getMinimumWidth();
        tImage.setLayoutParams(layoutImage);
        tImage.setImageDrawable(d);

        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    //Show the distance between the user location and the location of the place
    private void setUserLocation(DetailledPlace place) {
        Location userLocation = new Location("userLocation");
        userLocation.setLatitude(this.userLocationLatitude);
        userLocation.setLongitude(this.userLocationLongitude);

        Location placeLocation = new Location("placeLocation");
        placeLocation.setLatitude(place.getLatitude());
        placeLocation.setLongitude(place.getLongitude());

        double result = userLocation.distanceTo(placeLocation);
        DecimalFormat df = new DecimalFormat("#");
        df.setRoundingMode(RoundingMode.CEILING);
        tDistance.setText(df.format(result) + "m");
    }

    //Show the opening hour if they exist
    private void setOpeningHours(DetailledPlace place) {
        if(place.hasOpeningHours()){
            String openingHoursText = place.getOpeningHours();
            openingHoursText = openingHoursText.replace("[", "");
            openingHoursText = openingHoursText.replace("]", "");
            openingHoursText = openingHoursText.replace("\"", "");

            String[] openingHoursTexts = openingHoursText.split(",");
            openingHoursText = "";
            for (String text : openingHoursTexts) {
                openingHoursText += text + "\n";
            }

            tOpeningHours.setText(openingHoursText);
        }
        else{
            View layoutOpeningHours = findViewById(R.id.openingHoursLayout);
            layoutOpeningHours.setVisibility(LinearLayout.GONE);
        }
    }
}
