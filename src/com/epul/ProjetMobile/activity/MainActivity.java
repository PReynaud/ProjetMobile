package com.epul.ProjetMobile.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.epul.ProjetMobile.R;
import com.epul.ProjetMobile.adapter.InfoPopup;
import com.epul.ProjetMobile.adapter.InstructionAdapter;
import com.epul.ProjetMobile.adapter.PlaceAdapter;
import com.epul.ProjetMobile.business.Leg;
import com.epul.ProjetMobile.business.Place;
import com.epul.ProjetMobile.business.Route;
import com.epul.ProjetMobile.business.Waypoint;
import com.epul.ProjetMobile.service.DirectionService;
import com.epul.ProjetMobile.service.DirectionServiceDelegate;
import com.epul.ProjetMobile.service.PlacesService;
import com.epul.ProjetMobile.service.PlacesServiceDelegate;
import com.epul.ProjetMobile.tools.ListManager;
import com.epul.ProjetMobile.tools.MapLayout;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.*;

import java.util.*;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, PlacesServiceDelegate, DirectionServiceDelegate {
    public static final String wayResource = "Way";
    public static final int ListResult = 1;
    public static final int Param = 2;
    private final ArrayList<Place> way = new ArrayList<>();
    private final Map<Marker, Place> markers = new HashMap<>();
    private GoogleMap googleMap;
    private Location userLocation;
    private PlaceAdapter adapter;
    private AutoCompleteTextView autoCompleteTextView;
    private PlacesService placesService;
    private ListManager listManager;
    private Map<Route, List<Polyline>> parcours;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //Set the default language
        Locale.setDefault(new Locale("fr_FR"));
        userLocation = null;
        parcours = new HashMap<>();
        initializeSearchBar();
        initializeMonumentList();
        try {
            initializeMap();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialisation de la searchBar et de tous ses paramètres
     */
    private void initializeMonumentList() {
        ListView monumentList = (ListView) findViewById(R.id.monumentListView);
        listManager = new ListManager(monumentList, way, markers) {
            @Override
            public boolean removeMonument(Place place) {
                boolean res = super.removeMonument(place);
                resetPopUps();
                return res;
            }
        };
        listManager.createListView();
        ImageView settingsButton = (ImageView) findViewById(R.id.settings_icon);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),
                        "Bouton Settings", Toast.LENGTH_SHORT)
                        .show();
                Intent myIntent = new Intent(MainActivity.this, SettingsActivity.class);
                MainActivity.this.startActivityForResult(myIntent, Param);
            }
        });
        Button parcoursSimpleButton = (Button) findViewById(R.id.ParcoursSimpleButton);
        parcoursSimpleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (way.size() >= 1) launchDirectionService();
            }
        });
    }

    /**
     * Initialisation de la searchBar et de tous ses paramètres
     */
    private void initializeSearchBar() {
        ImageView localisationButton = (ImageView) findViewById(R.id.localisationicon);
        localisationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                centerMapOnUserLocation(15);
            }
        });
        AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Place item = adapter.getItem(position);
                Marker marker = null;
                for (Map.Entry<Marker, Place> placeEntry : markers.entrySet()) {
                    if (placeEntry.getValue().equals(item)) {
                        marker = placeEntry.getKey();
                        break;
                    }
                }
                //Vide le textView
                autoCompleteTextView.clearListSelection();
                autoCompleteTextView.setText("");
                //Cache le clavier
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(autoCompleteTextView.getWindowToken(), 0);
                centerMapOnMarker(marker);
            }
        };
        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.autocomplete_places);
        autoCompleteTextView.setOnItemClickListener(mAutocompleteClickListener);
    }

    /**
     * Initialisation de la map et de tous ses paramètres
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

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
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
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        centerMapOnUserLocation(15);
        launchPlaceService();
        final MapLayout layout = ((MapLayout) findViewById(R.id.map_layout));
        layout.init(this.googleMap, (int) (59 * this.getResources().getDisplayMetrics().density + 0.5f));

        //Décale la vue si on clique sur un marker (permet de voir l'infobulle, normalement cachée en haut de l'écran)
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                centerMapOnMarker(marker);
                return true;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ListResult) {
            way.clear();
            way.addAll((ArrayList<Place>) data.getExtras().getSerializable(wayResource));
            //A la fin de l'activité secondaire on réaffiche la toolbar
        }
        //On met à jour la position et les markers
        googleMap.clear();
        launchPlaceService();
        if (!parcours.isEmpty()) displayWay(new ArrayList<>(parcours.keySet()));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void launchPlaceService() {
        placesService = new PlacesService(getResources().getString(R.string.google_places_key));
        placesService.setLocation(this.userLocation, this);
        placesService.execute();
    }

    public void launchDirectionService() {
        DirectionService directionService = new DirectionService(getResources().getString(R.string.google_direction_key), false, way, this);
        directionService.init(this.userLocation, this);
        directionService.execute();
    }

    /**
     * Active la recherche après le placement des markers
     */
    public void enableSearch() {
        ArrayList<Place> list_places = new ArrayList<>();
        list_places.addAll(placesService.getPlaces());
        adapter = new PlaceAdapter(this, list_places);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setThreshold(1);
    }

    /**
     * Vérifie l'autorisation concernant la position de l'utilisateur et centre la map sur celle-ci
     */
    private void centerMapOnUserLocation(int zoom) {
        Location location = getLastKnownLocation();
        if (location != null) {
            this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(location.getLatitude(), location.getLongitude()), zoom));
            this.userLocation = location;
        } else {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.ErrorWhenGettingLocation), Toast.LENGTH_SHORT)
                    .show();

        }
    }

    /**
     * Anime la caméra sur un marker, avec un décalage pour pouvoir aussi afficher l'infobulle
     *
     * @param marker marker sur lequel centrer la map
     */
    private void centerMapOnMarker(Marker marker) {
        LatLng markerLocation = marker.getPosition();
        Point mappoint = googleMap.getProjection().toScreenLocation(new LatLng(markerLocation.latitude, markerLocation.longitude));
        mappoint.set(mappoint.x, mappoint.y - 150);
        marker.showInfoWindow();
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(googleMap.getProjection().fromScreenLocation(mappoint)));
    }

    /**
     * Met à jour et récupère la dernière location
     *
     * @return dernière location
     */
    private Location getLastKnownLocation() {
        LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        final List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.ErrorPermissionWhenGettingLocation), Toast.LENGTH_SHORT)
                    .show();
        } else {
            // Règle les bugs de location dus aux roms customs de type TouchWiz, etc
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
                            consumeParcours();
                        }
                    });
            for (String provider : providers) {
                Location l = locationManager.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }
                if (bestLocation == null || l.getAccuracy() > bestLocation.getAccuracy()) {
                    bestLocation = l;
                }
            }
        }
        return bestLocation;
    }

    private void consumeParcours() {
    }

    public void resetPopUps() {
        MapLayout layout = ((MapLayout) findViewById(R.id.map_layout));
        ViewGroup view = (ViewGroup) getLayoutInflater().inflate(R.layout.info_popup, null);
        Map<Marker, Map<Place, Boolean>> placeAdapter = new HashMap<>();

        for (Map.Entry<Marker, Place> placeEntry : markers.entrySet()) {
            Map<Place, Boolean> temp = new HashMap<>();
            temp.put(placeEntry.getValue(), false);
            placeAdapter.put(placeEntry.getKey(), temp);
        }
        googleMap.setInfoWindowAdapter(new InfoPopup(getApplicationContext(), view, layout, placeAdapter) {
            @Override
            public void actionAjouter(Marker marker) {
                super.actionAjouter(marker);
                if (!way.contains(markers.get(marker))) {
                    way.add(markers.get(marker));
                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.monument_selectionne));
                } else {
                    way.remove(markers.get(marker));
                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.monument_normal));
                }
                listManager.createListView();
            }

            @Override
            public void actionDetail(Marker marker) {
                super.actionDetail(marker);
            }
        });
    }

    @Override
    public void placeMarkers(List<Place> listOfPlaces) {
        markers.clear();
        for (Place place : listOfPlaces) {
            Marker marker = googleMap.addMarker(new MarkerOptions().position(new LatLng(place.getLatitude(), place.getLongitude()))
                    .title(place.getName()).anchor(0.5f, 0.5f)
                    .icon(way.contains(place) ?
                            BitmapDescriptorFactory.fromResource(R.drawable.monument_selectionne) :
                            BitmapDescriptorFactory.fromResource(R.drawable.monument_normal)));
            markers.put(marker, place);
        }
        resetPopUps();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void displayWay(List<Route> routes) {
        //Clear la map
        if (parcours.size() > 0)
            for (Polyline line : parcours.values().iterator().next())
                line.remove();
        parcours = new HashMap<>();
        //Display the route
        Route bestRoute = routes.size() > 0 ? routes.get(0) : null;
        if (bestRoute != null) {
            ArrayList<String> instructions = new ArrayList<>();
            findViewById(R.id.directionListContainer).setVisibility(View.VISIBLE);
            ListView listView = (ListView) findViewById(R.id.directionListView);
            ArrayList<Polyline> polylines = new ArrayList<>();
            for (int i = 0; i < bestRoute.legs.length; i++) {
                Leg leg = bestRoute.legs[i];
                for (int k = 0; k < leg.waypoints.length; k++) {
                    Waypoint waypoint = leg.waypoints[k];
                    for (int j = 0; j < waypoint.polyline.length - 1; j++) {
                        LatLng src = waypoint.polyline[j];
                        LatLng dest = waypoint.polyline[j + 1];
                        polylines.add(googleMap.addPolyline(new PolylineOptions()
                                .add(new LatLng(src.latitude, src.longitude),
                                        new LatLng(dest.latitude, dest.longitude))
                                .width(8).color(Color.RED).geodesic(true)));
                    }
                    instructions.add(waypoint.instruction);
                }
            }
            parcours.clear();
            parcours.put(bestRoute, polylines);
            listView.setAdapter(new InstructionAdapter(instructions, this));
        }
        centerMapOnUserLocation(18);
    }
}
