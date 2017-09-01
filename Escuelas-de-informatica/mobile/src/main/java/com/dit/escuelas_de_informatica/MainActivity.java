package com.dit.escuelas_de_informatica;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import android.widget.Toast;

import com.dit.escuelas_de_informatica.utiles.HttpResponseListener;
import com.dit.escuelas_de_informatica.utiles.ServerComunication;
import com.dit.escuelas_de_informatica.utiles.SocketListener;
import com.dit.escuelas_de_informatica.utiles.Utils;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity implements SocketListener, HttpResponseListener {
    private static final int REQUEST_ACCOUNTS_CODE = 33465;
    private String API_URL = "http://192.168.2.31:5000";
    private String TAG = "MainActivity";
    private String mDeviceId;
    private FloatingActionButton mBotonFlotante;
    private int mSelectedOption;
    private Toolbar mToolbar;
    private ServerComunication mServer;
    private PlacesList mPlacesList;
    private MessagesList mMessagesList;
    private Intent wearCommunicationIntent;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener;
    private String mUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        //initPermissions();
        initServerCommunication();
        initWearCommunicationService();
    }

    private void initPermissions() {
        requestPermission(Manifest.permission.GET_ACCOUNTS, REQUEST_ACCOUNTS_CODE);
    }

    private void initWearCommunicationService() {
        wearCommunicationIntent = new Intent(this, WearCommunicationService.class);
        startService(wearCommunicationIntent);
    }

    private void initUI() {
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        mBotonFlotante = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        mSelectedOption = R.id.navigation_places;
        mBotonFlotante.setOnClickListener(this.onBotonCliqueado());
        mOnNavigationItemSelectedListener = getOnNavigationItemSelectedListener();
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener getOnNavigationItemSelectedListener() {
        return new BottomNavigationView.OnNavigationItemSelectedListener() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.navigation_places:
                    mMessagesList.mListView.setVisibility(View.GONE);
                    mPlacesList.mListView.setVisibility(View.VISIBLE);
                    return true;

                case R.id.navigation_contacts:
                    return true;

                case R.id.navigation_messages:
                    mPlacesList.mListView.setVisibility(View.GONE);
                    mMessagesList.mListView.setVisibility(View.VISIBLE);
                    return true;
            }
            return false;
        }

        };
    }

    private View.OnClickListener onBotonCliqueado() {
        return new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                String msg = "default";
                switch (mSelectedOption) {
                    case R.id.navigation_places:
                        msg = "Crear nuevo lugar";
                        enviarAlMapa();
                        break;
                    case R.id.navigation_contacts:
                        msg = "Agregar nuevo contacto";
                        break;
                    case R.id.navigation_messages:
                        msg = "Crear nuevo mensaje";
                        break;
                }
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        };
    }

    private void enviarAlMapa() {
        Intent intent = new Intent(this, PlacesActivity.class);
        startActivity(intent);
    }

    private void initServerCommunication() {
        mDeviceId = getDeviceId();
        mServer = ServerComunication.getInstance(API_URL);
        mServer.emit("conectar", new String[]{mDeviceId});
        mServer.on(new String[]{"conectar", "no_registrado"}, this);
        mPlacesList = new PlacesList(MainActivity.this, "lugares", R.id.placesList,new String[] {"name", "description"});
        mMessagesList = new MessagesList(MainActivity.this,"mensajes",R.id.messageList,new String[] {"timeUser", "message"});
    }


    private void requestPermission(String permission,int requestCode){
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    permission)) {


            } else {
                ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
            }


        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ACCOUNTS_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    AccountManager manager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
                    Account list = manager.getAccountsByType("com.google")[0]; //primera cuenta gmail encontrada
                    mUsername =  list.name.split("@")[0]; //nos quedamos con la primer parte (antes del @)
                    registrar_usuario();
                } else {

                }
                return;
            }
        }
    }


    private String getDeviceId() {
        return Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
    }


    @Override
    public void call(String eventName, Object[] args) {
        switch (eventName) {
            case "conectar":
                conectar(args);
                return;
            case "no_registrado":
                //registrar_usuario();
                requestPermission(Manifest.permission.GET_ACCOUNTS, REQUEST_ACCOUNTS_CODE);
                return;
        }
    }


    private void registrar_usuario() {
        Log.d(TAG, "Registrando Usuario");
        JSONObject params = new JSONObject();
        try {
            params.put("nombre", mUsername);
            params.put("id_usuario", mDeviceId);
            Utils.PostTask post = new Utils.PostTask();
            post.delegate = this; //Registro a MainActivity como delegado para escuchar el responseCode
            post.execute(new String[]{mServer.getURL() + "/guardarusuario", params.toString()});
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void conectar(Object[] args) {
        JSONObject data = (JSONObject) args[0];
        Log.d(TAG, "Conectando");
        try {
            Log.d(TAG, "Bienvenido " + data.getString("usuario"));
            mUsername = data.getString("usuario");
            return;
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "error al conectar: " + data.toString());
        }
    }

    @Override
    public void onHttpResponse(String responseCode) {
        switch (responseCode) {
            case "200":
                Log.d(TAG, "onHttpResponse: Success!");
                mServer.emit("conectar", new String[]{mDeviceId});
                break;
            case "404":
                Log.d(TAG, "onHttpResponse: Not Found!");
                break;
            case "500":
                Log.d(TAG, "onHttpResponse: Server Error!");
                break;
            default:
                Log.d(TAG, "onHttpResponse: [Code]: " + responseCode);
                break;
        }
    }

}
