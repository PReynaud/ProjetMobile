package com.epul.ProjetMobile;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;
import android.widget.Toolbar;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends Activity implements OnMapReadyCallback {
    private GoogleMap googleMap;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setActionBar(mToolbar);

        // Inflate a menu to be displayed in the toolbar
        //toolbar.inflateMenu(R.menu.your_toolbar_menu);

        try {
            initilizeMap();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Fonction qui va charger la map
     * */
    private void initilizeMap() {
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.map)).getMap();

            MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

            //Met la carte en hybride
            googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

            // Vérifie si la carte a bien été créée correctement sinon affiche un message
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.ErrorWhenLoadingMap), Toast.LENGTH_SHORT)
                        .show();
            }

            //Active la localisation
            googleMap.setMyLocationEnabled(true);

            // Active les bouttons de zoom
            googleMap.getUiSettings().setZoomControlsEnabled(true);

            //Active le boutton de location
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);

            //Active le compas
            googleMap.getUiSettings().setCompassEnabled(true);

            //Désactive la rotation
            googleMap.getUiSettings().setRotateGesturesEnabled(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initilizeMap();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(0, 0))
                .title("Pouet"));

        this.googleMap = googleMap;
        this.centerMapOnUserLocation();
    }

    private void centerMapOnUserLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        if (location != null)
        {
            this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(location.getLatitude(), location.getLongitude()), 13));
        }
        else{
            Toast.makeText(getApplicationContext(),
                    getString(R.string.ErrorWhenGettingLocation), Toast.LENGTH_SHORT)
                    .show();
        }
    }
}
