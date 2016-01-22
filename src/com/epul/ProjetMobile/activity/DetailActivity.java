package com.epul.ProjetMobile.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Space;
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
    private Space spaceImage;
    private ProgressDialog progressDialog;
    private int width;

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
        spaceImage = (Space) findViewById(R.id.photoSpace);

        Intent i = getIntent();
        String placeID = i.getStringExtra(InfoPopup.PLACE_ID);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Chargement en cours...");
        progressDialog.show();

        PlaceDetailService service = new PlaceDetailService(getResources().getString(R.string.google_places_key), placeID, this);
        service.execute();
    }

    @Override
    public void loadDetails(DetailledPlace place) {
        tName.setText(place.getName());
        tAdresse.setText(place.getAddress());

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        this.width = size.x;

        if (place.getPhotoUrls().size() > 0) {
            PlacePhotoService service = new PlacePhotoService(this, getResources().getString(R.string.google_places_key), place.getPhotoUrls().get(0), width);
            service.execute();
        }
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
}
