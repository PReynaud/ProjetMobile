package com.epul.ProjetMobile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, PlacesServiceDelegate, GoogleMap.OnInfoWindowClickListener {
    public static final String wayResource = "Way";
    private GoogleMap googleMap;
    private Location userLocation;
    private ArrayList<Place> way;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                Toast.makeText(MainActivity.this, "Setting clic :)", Toast.LENGTH_LONG).show();
                break;
            case R.id.action_search:
                Toast.makeText(MainActivity.this, "Search clic :)", Toast.LENGTH_LONG).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //A la fin de l'activité secondaire on réaffiche la toolbar
        findViewById(R.id.list_top).setVisibility(View.VISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        way = new ArrayList<>();

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        userLocation = null;
        try {
            initilizeMap();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialisation de la map et de tous ces paramètres
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

            googleMap.setOnInfoWindowClickListener(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.centerMapOnUserLocation();
        PlacesService service = new PlacesService(getResources().getString(R.string.google_places_key));
        googleMap.setInfoWindowAdapter(new InfoPopup(this));
        service.setLocation(this.userLocation, this);
        service.execute();

        //Ajout du détecteur de swipe une fois la map chargée
        final GestureDetectorCompat detector = new GestureDetectorCompat(this, new SwipeDetector() {
            @Override
            public void onTouch() {
                launchListActivity();
            }

            @Override
            public void onSwipeToUp() {
                launchListActivity();
            }
        });

        Toolbar topToolbar = (Toolbar) findViewById(R.id.list_top);
        topToolbar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detector.onTouchEvent(event);
                return false;
            }
        });
    }

    public void launchListActivity() {
        Intent intent = new Intent(MainActivity.this, ListActivity.class);
        intent.putParcelableArrayListExtra(wayResource, way);
        startActivityForResult(intent, Activity.RESULT_OK);
        //findViewById(R.id.list_top).setVisibility(View.GONE);
    }

    /**
     * Vérifie l'autorisation concernant la position de l'utilisateur et centre la map sur celle-ci
     */
    private void centerMapOnUserLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.ErrorPermissionWhenGettingLocation), Toast.LENGTH_SHORT)
                    .show();
        }
        else{
            Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
            if (location != null)
            {
                this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(location.getLatitude(), location.getLongitude()), 13));
                this.userLocation = location;
            }
            else{
                Toast.makeText(getApplicationContext(),
                        getString(R.string.ErrorWhenGettingLocation), Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
    @Override
    public void placeMarkers(List<Place> listOfPlaces) {
        for (Place place : listOfPlaces) {
            googleMap.addMarker(new MarkerOptions().position(new LatLng(place.getLatitude(), place.getLongitude()))
                    .title(place.getName())
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.monument)));
        }
        Toast.makeText(getApplicationContext(),
                "Placement des marqueurs terminé", Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
            Toast.makeText(getApplicationContext(),
                    marker.getTitle(), Toast.LENGTH_SHORT)
                    .show();

    }
}
