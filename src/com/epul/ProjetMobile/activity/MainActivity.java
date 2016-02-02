package com.epul.ProjetMobile.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
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
import com.epul.ProjetMobile.tools.ListSlideListener;
import com.epul.ProjetMobile.tools.MapLayout;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.*;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.*;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, PlacesServiceDelegate, DirectionServiceDelegate {
    public static final String wayResource = "Way";
    public static final int ListResult = 1;
    public static final int Param = 2;
    public static final int resultFromDetailActivity = 3;
    private final ArrayList<Place> way = new ArrayList<>();
    private final Map<Marker, Place> markers = new HashMap<>();
    private GoogleMap googleMap;
    private Location userLocation;
    private PlaceAdapter adapter;
    private AutoCompleteTextView autoCompleteTextView;
    private PlacesService placesService;
    private ListManager listManager;
    private Map<Route, List<Polyline>> parcours;
    private SharedPreferences preferences;
    private SlidingUpPanelLayout slidePanel;
    private ProgressDialog progressDialog;
    private TextView listTopTextView;
    private ImageView expandImage;

    private String transportType;
    private ArrayList<String> placeType;

    public static boolean isValidInteger(String s) {
        try {
            int i = Integer.parseInt(s);
            if (i < 0) {
                return false;
            }
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //Set the default language
        Locale.setDefault(new Locale("fr_FR"));

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.LoadInProgress));
        progressDialog.show();

        listTopTextView = (TextView) findViewById(R.id.list_top_text_view);

        userLocation = null;
        parcours = new HashMap<>();
        slidePanel = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        expandImage = (ImageView) findViewById(R.id.expand_icon);
        slidePanel.setPanelSlideListener(new ListSlideListener((ImageView) findViewById(R.id.expand_icon)));

        preferences = this.getSharedPreferences(
                SettingsActivity.SETTINGS_SHARED_PREFERENCES_FILE_NAME,
                Context.MODE_PRIVATE);


        initializeSearchBar();
        initializeMonumentList();

        if (preferences != null && preferences.getAll() != null
                && preferences.getAll().get("types_monuments") != null && preferences.getAll().get("transports") != null) {
            placeType = getPlaceTypeFromSettings();
            transportType = (String) preferences.getAll().get("transports");
        } else {
            transportType = "&mode=walking";
            ArrayList<String> placeTypes = new ArrayList<>();
            placeTypes.add("aquarium");
            placeTypes.add("art_gallery");
            placeTypes.add("city_hall");
            placeTypes.add("museum");
            placeTypes.add("park");
            placeTypes.add("place_of_worship");
            placeTypes.add("zoo");
            placeTypes.add("establishment|premise");
        }
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
                Intent myIntent = new Intent(MainActivity.this, SettingsActivity.class);
                MainActivity.this.startActivityForResult(myIntent, Param);
            }
        });
        Button parcoursButton = (Button) findViewById(R.id.ParcoursButton);
        parcoursButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (way.size() >= 1) {
                    launchDirectionService();
                    listTopTextView.setText(R.string.toolbarListParcours);
                }
                slidePanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });

        ImageView stopButton = (ImageView) findViewById(R.id.stop_icon);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listTopTextView.setText(R.string.toolbarList);
                removeWay();
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

            //On met à jour la position et les markers
            googleMap.clear();
            launchPlaceService();
            if (!parcours.isEmpty()) displayWay(new ArrayList<>(parcours.keySet()));
        }

        //On influence l'application en fonction des paramètres retournés
        if (requestCode == Param) {
            if (resultCode == RESULT_OK) {
                progressDialog.show();
                String new_transport = (String) preferences.getAll().get("transports");
                if (getPlaceTypeFromSettings().equals(this.placeType) || new_transport.equals(this.transportType)) {
                    launchPlaceService();
                    launchDirectionService();
                }
            }
        }

        //On ajoute la place dont l'id est stocké dans result
        if (requestCode == resultFromDetailActivity) {
            if (resultCode == RESULT_OK) {
                String resultPlaceId = data.getStringExtra("result");
                Iterator it = markers.entrySet().iterator();
                Place placeToAdd = null;
                Marker markerToAdd = null;
                while (it.hasNext() && placeToAdd == null) {
                    Map.Entry pair = (Map.Entry) it.next();
                    Place currentPlace = (Place) pair.getValue();
                    if (currentPlace.getId().equals(resultPlaceId)) {
                        placeToAdd = (Place) pair.getValue();
                        markerToAdd = (Marker) pair.getKey();
                    }
                }

                if (!way.contains(markerToAdd)) {
                    way.add(placeToAdd);
                    markerToAdd.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.monument_selectionne));
                    listManager.createListView();
                }

            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void launchPlaceService() {
        googleMap.clear();
        placesService = new PlacesService(getResources().getString(R.string.google_places_key), this);
        placesService.setLocation(this.userLocation);
        if (preferences != null && preferences.getAll().get("radius_limit") != null) {
            String radiusLimit = (String) preferences.getAll().get("radius_limit");
            if (isValidInteger(radiusLimit)) {
                placesService.setRadius(Integer.parseInt(radiusLimit));
            } else {
                placesService.setRadius(5000);
                Toast.makeText(this, "Rayon de recherche invalide, remplacé par 5000m", Toast.LENGTH_LONG).show();
            }
        }
        if (preferences != null && preferences.getAll().get("types_monuments") != null) {
            placesService.setPlaceTypes(getPlaceTypeFromSettings());
        } else {
            ArrayList<String> placeTypes = new ArrayList<>();
            placeTypes.add("aquarium");
            placeTypes.add("art_gallery");
            placeTypes.add("city_hall");
            placeTypes.add("museum");
            placeTypes.add("park");
            placeTypes.add("place_of_worship");
            placeTypes.add("zoo");
            placeTypes.add("establishment|premise");
            placesService.setPlaceTypes(placeTypes);
        }
        placesService.execute();
    }

    public void launchDirectionService() {
        if (way != null & way.size() > 0) {
            progressDialog.show();
            DirectionService directionService = new DirectionService(getResources().getString(R.string.google_direction_key),
                    transportType, way, this);
            directionService.init(this.userLocation, this);
            directionService.execute();
        }
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
        Location location = getLocation();
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
    public Location getLocation() {
        Location location = null;
        try {
            LocationManager locationManager = (LocationManager) getApplicationContext()
                    .getSystemService(LOCATION_SERVICE);
            // getting GPS status
            boolean isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            boolean isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                if (isNetworkEnabled) {
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
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
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
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
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
        googleMap.setInfoWindowAdapter(new InfoPopup(this, view, layout, placeAdapter, userLocation) {
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
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
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
                                .width(8).color(getResources().getColor(R.color.settingsPrimary)).geodesic(true)));
                    }
                    instructions.add(waypoint.instruction);
                }
            }
            parcours.clear();
            parcours.put(bestRoute, polylines);
            listView.setAdapter(new InstructionAdapter(instructions, this));

            // Création et affichage de la liste pour les directions
            final ImageView removeDirectionListButton = (ImageView) findViewById(R.id.removeDirectionList);
            removeDirectionListButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LinearLayout listContainer = (LinearLayout) findViewById(R.id.directionListContainer);
                    ListView listDirection = (ListView) findViewById(R.id.directionListView);

                    if (listDirection.getVisibility() == View.VISIBLE) {
                        listDirection.setVisibility(View.GONE);

                        removeDirectionListButton.animate().rotation(180f);

                        //On convertit les 115 dp en pixels
                        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 115, getResources().getDisplayMetrics());
                        listContainer.animate().translationY(height);
                    } else {
                        listDirection.setVisibility(View.VISIBLE);
                        listContainer.animate().translationY(0);
                        removeDirectionListButton.animate().rotation(0f);
                    }
                }
            });


            // Apparition du bouton pour stopper le parcours
            findViewById(R.id.stop_icon).setVisibility(View.VISIBLE);
        }
        centerMapOnUserLocation(18);

        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void removeWay() {
        if (parcours.size() > 0)
            for (Polyline line : parcours.values().iterator().next())
                line.remove();
        parcours = new HashMap<>();


        // Disparition du bouton pour stopper le parcours
        findViewById(R.id.stop_icon).setVisibility(View.INVISIBLE);

        // Disparition du listview direction
        LinearLayout listContainer = (LinearLayout) findViewById(R.id.directionListContainer);
        listContainer.setVisibility(View.INVISIBLE);
    }

    private ArrayList<String> getPlaceTypeFromSettings() {
        HashSet<String> types = (HashSet<String>) preferences.getAll().get("types_monuments");
        ArrayList<String> listTypes = new ArrayList<>(types);
        return listTypes;
    }

    /*private ArrayList<Transport> getTransportFromSettings() {
        ArrayList<Transport> transportList = new ArrayList<>();
        HashSet<String> types = (HashSet<String>) preferences.getAll().get("transports");
        Iterator<String> iterator = types.iterator();
        while (iterator.hasNext()) {
            switch (iterator.next()) {
                case "foot":
                    transportList.add(Transport.foot);
                    break;
                case "publicTransport":
                    transportList.add(Transport.publicTransport);
                    break;
                case "car":
                    transportList.add(Transport.car);
                    break;
            }
        }
        return transportList;
    }*/
}
