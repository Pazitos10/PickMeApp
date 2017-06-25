package com.dit.escuelas_de_informatica;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;

import java.util.ArrayList;

public class PlacesActivity extends FragmentActivity
        implements OnMapReadyCallback{

    private GoogleMap mMap;
    private ArrayList<Lugar> lugares;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        lugares = solicitar_lugares();

    }

    private ArrayList<Lugar> solicitar_lugares() {
        /**
         * Recupera de un servidor los lugares que ya fueron creados
         * */
        lugares = new ArrayList<Lugar>();
        // Añade el elemento al ArrayList
        lugares.add(new Lugar("India", "Arriba de Nueva Delhi", new LatLng(29.56, 77.49)));
        lugares.add(new Lugar("Francia", "Arriba de Paris", new LatLng(49.06, 2.12)));
        return lugares;
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        // Con el mapa listo, seteamos los marcadores de los lugares que tenemos...
        insertar_marcadores();
        // Setting a click event handler for the map
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {

                // Creating a marker
                MarkerOptions markerOptions = new MarkerOptions();

                // Setting the position for the marker
                markerOptions.position(latLng);

                // Setting the title for the marker.
                // This will be displayed on taping the marker
                markerOptions.title(latLng.latitude + " : " + latLng.longitude);

                // Clears the previously touched position
                mMap.clear();
                //TODO: llamar a insertar_marcadores()

                // Animating to the touched position
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                // Placing a marker on the touched position
                mMap.addMarker(markerOptions);
            }
        });
    }

    private void insertar_marcadores() {
        // your code
        for (Lugar lugar: lugares) {
            LatLng centro = lugar.getLatLng();
            mMap.addMarker(new MarkerOptions().position(centro).title(lugar.toString()));
        }

    }


}
