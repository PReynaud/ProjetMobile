package com.epul.ProjetMobile.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;
import com.epul.ProjetMobile.R;
import com.epul.ProjetMobile.adapter.InfoPopup;
import com.epul.ProjetMobile.adapter.PlaceAdapter;
import com.epul.ProjetMobile.business.Place;
import com.epul.ProjetMobile.service.PlacesService;
import com.epul.ProjetMobile.service.PlacesServiceDelegate;
import com.epul.ProjetMobile.tools.MapLayout;
import com.epul.ProjetMobile.tools.SwipeDetector;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, PlacesServiceDelegate, GoogleApiClient.OnConnectionFailedListener {
    public static final String wayResource = "Way";
    public static final int ListResult = 1;
    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
            new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));
    private final ArrayList<Place> way = new ArrayList<>();
    private final Map<Marker, Place> markers = new HashMap<>();
    private GoogleMap googleMap;
    private Location userLocation;
    private PlaceAdapter adapter;
    private AutoCompleteTextView mAutocompleteView;
    private Button localisationButton;
    private PlacesService service;
    /**
     * Listener that handles selections from suggestions from the AutoCompleteTextView that
     * displays Place suggestions.
     * Gets the place id of the selected item and issues a request to the Places Geo Data API
     * to retrieve more details about the place.
     *
     * @see com.google.android.gms.location.places.GeoDataApi#getPlaceById(com.google.android.gms.common.api.GoogleApiClient,
     * String...)
     */
    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
            final Place item = adapter.getItem(position);
            final String placeId = item.getId();
            final CharSequence primaryText = item.getName();

            Toast.makeText(getApplicationContext(), "Clicked: " + primaryText,
                    Toast.LENGTH_SHORT).show();
        }
    };
    /**
     * Callback for results from a Places Geo Data API query that shows the first place result in
     * the details view on screen.
     */
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                places.release();
                return;
            }
            // Get the Place object from the buffer.
            final com.google.android.gms.location.places.Place place = places.get(0);
            places.release();
        }
    };

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

        localisationButton = (Button) findViewById(R.id.localisationbutton);
        localisationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                centerMapOnUserLocation();
            }
        });

        // Retrieve the AutoCompleteTextView that will display Place suggestions.
        mAutocompleteView = (AutoCompleteTextView)
                findViewById(R.id.autocomplete_places);

        // Register a listener that receives callbacks when a suggestion has been selected
        mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);

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

            // Désactive les boutons de navigation
            googleMap.getUiSettings().setMapToolbarEnabled(false);

            //Désactive le boutton de location
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);

            //Désactive la rotation
            googleMap.getUiSettings().setRotateGesturesEnabled(true);
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

        googleMap.setInfoWindowAdapter(new InfoPopup(getApplicationContext(), view, layout) {
            @Override
            public void actionAjouter(Marker marker) {
                super.actionAjouter(marker);
                if (!way.contains(markers.get(marker))) {
                    way.add(markers.get(marker));
                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.selected_monument));
                }
            }
        });

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
        service = new PlacesService(getResources().getString(R.string.google_places_key));
        service.setLocation(this.userLocation, this);
        service.execute();
    }

    public void enableSearch() {
        ArrayList<Place> list_places = new ArrayList<>();
        list_places.addAll(service.getPlaces());
        adapter = new PlaceAdapter(this, list_places);
        mAutocompleteView.setAdapter(adapter);
        mAutocompleteView.setThreshold(1);
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
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.ErrorPermissionWhenGettingLocation), Toast.LENGTH_SHORT)
                    .show();
        } else {
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {
                        }

                        @Override
                        public void onProviderEnabled(String provider) {
                        }

                        @Override
                        public void onProviderDisabled(String provider) {
                        }

                        @Override
                        public void onLocationChanged(final Location location) {
                        }
                    });
            for (String provider : providers) {
                Location l = locationManager.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }
                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy() || !provider.equals("passive")) {
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
            markers.clear();
            for (Place place : listOfPlaces) {
                Marker marker = googleMap.addMarker(new MarkerOptions().position(new LatLng(place.getLatitude(), place.getLongitude()))
                        .title(place.getName())
                        .icon(way.contains(place) ?
                                BitmapDescriptorFactory.fromResource(R.drawable.selected_monument) :
                                BitmapDescriptorFactory.fromResource(R.drawable.monument)));
                markers.put(marker, place);
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
