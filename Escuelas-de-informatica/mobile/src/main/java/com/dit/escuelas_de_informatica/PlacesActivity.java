package com.dit.escuelas_de_informatica;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.dit.escuelas_de_informatica.modelo.Lugar;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

import static com.dit.escuelas_de_informatica.utiles.Utiles.mostrarToast;

public class PlacesActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    private static final int REQUEST_CODE = 1;
    private static final int PERMISSION_REQUEST_CODE = 2;
    private GoogleMap mMap;
    private ArrayList<Lugar> mLugares;
    private LatLng mCurrentLatLng;
    private Lugar mLugarElegido;
    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mLugarElegido = new Lugar();
        mLugares = solicitar_lugares();

        //mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        // my_child_toolbar is defined in the layout file
        Toolbar myChildToolbar =
                (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(myChildToolbar);

        // Get a support ActionBar corresponding to this toolbar_map
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_map, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_next:
                if (mLugarElegido.getLatLng() != null) {
                    Intent i = new Intent(this, NewPlaceDataActivity.class);
                    i.putExtra("latLng", mLugarElegido.getLatLng());
                    startActivityForResult(i, REQUEST_CODE);
                } else {
                    mostrarToast(this, getString(R.string.invalid_place_latLng));
                }
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) { //Si vuelve de enviar los datos a la API exitosamente, cierra el activity
            if (resultCode == RESULT_OK)
                finish();
        }
    }


    private ArrayList<Lugar> solicitar_lugares() {
        /**
         * Recupera de un servidor los mLugares que ya fueron creados
         * */
        mLugares = new ArrayList<Lugar>();
        // Añade el elemento al ArrayList
        mLugares.add(new Lugar("India", "Arriba de Nueva Delhi", new LatLng(29.56, 77.49)));
        mLugares.add(new Lugar("Francia", "Arriba de Paris", new LatLng(49.06, 2.12)));
        return mLugares;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
/*        if (hasLocationPermission()) {
            mMap.setOnMyLocationButtonClickListener(myLocationButtonClickListener());
        }*/

        // Con el mapa listo, seteamos los marcadores de los mLugares que tenemos...
        insertar_marcadores();
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(latLng.latitude + " : " + latLng.longitude);
                mMap.clear();
                insertar_marcadores();
                mLugarElegido.setLatLng(latLng);
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.addMarker(markerOptions);
            }
        });
    }

    private boolean hasLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            mMap.setMyLocationEnabled(true);
            return true;
        }
        return false;
    }

    private void insertar_marcadores() {
        for (Lugar lugar : mLugares) {
            LatLng centro = lugar.getLatLng();
            mMap.addMarker(new MarkerOptions().position(centro).title(lugar.toString()));
        }

    }


    /*public GoogleMap.OnMyLocationButtonClickListener myLocationButtonClickListener(){
        return new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                if (ActivityCompat.checkSelfPermission(PlacesActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(PlacesActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    String mPerms[] = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
                    ActivityCompat.requestPermissions(PlacesActivity.this, mPerms, PERMISSION_REQUEST_CODE);

                }

                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener(PlacesActivity.this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    mLugarElegido.setLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
                                    CameraUpdate center =
                                            CameraUpdateFactory.newLatLng(mLugarElegido.getLatLng());
                                    CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
                                    mMap.moveCamera(center);
                                    mMap.animateCamera(zoom);
                                }
                            }
                        });
                return true;
            }
        };
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

    }*/

}
