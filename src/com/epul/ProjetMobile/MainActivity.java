package com.epul.ProjetMobile;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.widget.TextView;
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

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, PlacesServiceDelegate {
    public static final String wayResource = "Way";
    public static final int ListResult = 1;
    private final ArrayList<Place> way = new ArrayList<>();
    private GoogleMap googleMap;
    private Location userLocation;

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
        if (requestCode == ListResult) {
            way.clear();
            way.addAll((ArrayList<Place>) data.getExtras().getSerializable(wayResource));
            //A la fin de l'activité secondaire on réaffiche la toolbar
            findViewById(R.id.list_top).setVisibility(View.VISIBLE);
        }
        //On met à jour la position et les markers
        launchService();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        userLocation = null;
        try {
            initializeMap();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialisation de la map et de tous ces paramètres
     */
    private void initializeMap() {
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
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        launchService();
        final MapLayout layout = ((MapLayout) findViewById(R.id.map_layout));
        layout.init(this.googleMap, (int) (59 * this.getResources().getDisplayMetrics().density + 0.5f));

        final ViewGroup view = (ViewGroup) getLayoutInflater().inflate(R.layout.info_popup, null);

        googleMap.setInfoWindowAdapter(new InfoPopup(getApplicationContext(), view, layout));

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
        startActivityForResult(intent, ListResult);
        findViewById(R.id.list_top).setVisibility(View.GONE);
    }

    public void launchService() {
        googleMap.clear();
        centerMapOnUserLocation();
        PlacesService service = new PlacesService(getResources().getString(R.string.google_places_key));
        service.setLocation(this.userLocation, this);
        service.execute();
    }

    /**
     * Vérifie l'autorisation concernant la position de l'utilisateur et centre la map sur celle-ci
     */
    private void centerMapOnUserLocation() {
        Location location = getLastKnownLocation();
        if (location != null) {
            this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(location.getLatitude(), location.getLongitude()), 15));
            this.userLocation = location;
        } else {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.ErrorWhenGettingLocation), Toast.LENGTH_SHORT)
                    .show();

        }
    }

    private Location getLastKnownLocation() {
        LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.ErrorPermissionWhenGettingLocation), Toast.LENGTH_SHORT)
                        .show();
            } else {
                Location l = locationManager.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }
                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                    // Found best last known location: %s", l);
                    bestLocation = l;
                }
            }
        }
        return bestLocation;
    }

    @Override
    public void placeMarkers(List<Place> listOfPlaces) {
        if (listOfPlaces != null) {
            for (Place place : listOfPlaces) {
                googleMap.addMarker(new MarkerOptions().position(new LatLng(place.getLatitude(), place.getLongitude()))
                        .title(place.getName())
                        .icon(way.contains(place) ?
                                BitmapDescriptorFactory.fromResource(R.drawable.selected_monument) :
                                BitmapDescriptorFactory.fromResource(R.drawable.monument)));
            }
            Toast.makeText(getApplicationContext(),
                    "Placement des marqueurs terminé", Toast.LENGTH_SHORT)
                    .show();

        }
    }
}
