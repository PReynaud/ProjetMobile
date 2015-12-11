package com.epul.ProjetMobile;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends Activity implements OnMapReadyCallback {
    // Google Map
    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        try {
            // Loading map
            initilizeMap();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * function to load map. If map is not created it will create it for you
     * */
    private void initilizeMap() {
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.map)).getMap();

            MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

            // check if map is created successfully or not
            /*if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.ErrorWhenLoadingMap), Toast.LENGTH_SHORT)
                        .show();
            }*/
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
        Toast.makeText(getApplicationContext(), "BOUDIBOUBABADOU", Toast.LENGTH_SHORT).show();

        this.googleMap = googleMap;
    }
}
