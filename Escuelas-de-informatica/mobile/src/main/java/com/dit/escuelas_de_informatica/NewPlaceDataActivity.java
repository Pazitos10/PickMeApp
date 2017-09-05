package com.dit.escuelas_de_informatica;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.dit.escuelas_de_informatica.utiles.GeofenceTransitionsIntentService;
import com.dit.escuelas_de_informatica.utiles.ServerComunication;
import com.dit.escuelas_de_informatica.utiles.ServerComunicationException;
import com.dit.escuelas_de_informatica.utiles.SocketListener;
import com.dit.escuelas_de_informatica.utiles.Utils;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import static com.dit.escuelas_de_informatica.utiles.Utils.showToast;


public class NewPlaceDataActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks {

    private String TAG = "NewPlaceDataActivity";
    private Toolbar mToolbar;
    private EditText mEditTextNombre, mEditTextDescripcion;
    private Button mButtonGuardar;
    private LatLng nuevoPunto;
    private GoogleApiClient mGoogleApiClient;
    private Geofence mGeofence;
    private GeofencingRequest mGeofencingRequest;
    private PendingIntent mGeofencePendingIntent;
    private ServerComunication mServer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        inicializar();
    }

    private void initUI() {
        //Configuramos aspectos de UI
        setContentView(R.layout.activity_new_place_data);
        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true); //Para volver hacia atrás si es posible
        ab.setTitle(R.string.title_activity_places);

        mToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                setResult(RESULT_CANCELED, new Intent());
                finish();
            }
        });

        mEditTextNombre = (EditText) findViewById(R.id.editTextNombre);
        mEditTextDescripcion = (EditText) findViewById(R.id.editTextDescripcion);
        mButtonGuardar = (Button) findViewById(R.id.buttonGuardar);
    }

    private void inicializar() {

        //Recuperamos datos del punto registrado
        Intent invokerIntent = getIntent();
        if (invokerIntent.hasExtra("nuevoPunto")) {
            nuevoPunto = (LatLng) invokerIntent.getExtras().get("nuevoPunto");
            Log.d(TAG, "onCreate: "+nuevoPunto.toString());
        }
        mServer = ServerComunication.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        //Creamos un GoogleApiClient
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }


    private void guardarLugar() {
        try {

            if( mEditTextNombre.getText().toString().trim().equals("")){
                mEditTextNombre.setError(getString(R.string.invalid_place_name));
            } else {
                String nombre = mEditTextNombre.getText().toString();
                String descripcion = mEditTextDescripcion.getText().toString();
                String[] params = new String[]{
                        nombre,
                        Utils.getLatLngString(nuevoPunto),
                        descripcion,
                        "pazosbruno" //TODO: obtener el usuario como corresponde
                };

                mServer.emit("guardarlugar", params);
                createGeofence();
                setResult(RESULT_OK);
                finish();
            }
        } catch (ServerComunicationException e) {
            e.printStackTrace();
        }


    }

    private void createGeofence() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        //Creamos un Geofence en el punto indicado
        mGeofence = new Geofence.Builder()
                .setRequestId("1")
                .setCircularRegion(nuevoPunto.latitude, nuevoPunto.longitude, 100)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setLoiteringDelay(10000)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL
                        | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();

        showToast(this, "Geofence creado");

        //Configuramos el trigger inicial
        mGeofencingRequest = new GeofencingRequest.Builder()
                .addGeofence(mGeofence)
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER
                        | GeofencingRequest.INITIAL_TRIGGER_DWELL)
                .build();

        //Pedimos a LocationServices que maneje los eventos del Geofence
        LocationServices.GeofencingApi
                .addGeofences(mGoogleApiClient,
                        mGeofencingRequest,
                        getGeofencePendingIntent());
    }

    /* Crea un PendingIntent para utilizar cuando ocurra un evento de Geofence */
    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back
        mGeofencePendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;
    }


    /* Location API callbacks */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //Una vez que estamos conectados a la API de Google configuramos el listener
        mButtonGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarLugar();
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

}
