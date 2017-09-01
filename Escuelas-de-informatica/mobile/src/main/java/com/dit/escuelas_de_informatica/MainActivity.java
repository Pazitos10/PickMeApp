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
    private String API_URL = "http://166.82.15.78:5000";
    private String TAG = "MainActivity";
    private String mDeviceId;
    private FloatingActionButton mBotonFlotante;
    private int mSelectedOption;
    private Toolbar mToolbar;
    private ServerComunication mServer;
    private PlacesList mPlacesList;
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
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mSelectedOption = R.id.navigation_places;
        mBotonFlotante.setOnClickListener(this.onBotonCliqueado());
        mOnNavigationItemSelectedListener = getOnNavigationItemSelectedListener();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener getOnNavigationItemSelectedListener() {
        return new BottomNavigationView.OnNavigationItemSelectedListener() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            //ListView lista;
            //lista = (ListView)findViewById(R.id.lista);
            //ArrayAdapter<String> adaptador;
            switch (item.getItemId()) {
                case R.id.navigation_places:
                    
                    mPlacesList.mAdapter.notifyDataSetChanged();

                    return true;

                case R.id.navigation_contacts:

//                    ArrayList<Map<String, String>> listaContactos = mServer.getListaUsuarios();
//                    adapter = new SimpleAdapter(MainActivity.this, listaContactos,
//                            android.R.layout.simple_list_item_1,
//                            new String[] {"encabezado", "cuerpo"},
//                            new int[] {android.R.id.text1,
//                                    android.R.id.text2,
//                            });
//                    lista.setAdapter(adapter);
                    return true;

                case R.id.navigation_messages:
                    //String mListaMensajes2 = "[{'encabezado':'msj 1', 'cuerpo':'Hola', 'idImagen':'0'}, {'encabezado':'Msj 2', 'cuerpo':'Chau', 'idImagen':'0'}]";
                    //mSelectedOption = R.id.navigation_messages;
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

        mPlacesList = new PlacesList(MainActivity.this, "lugares", R.id.lista,new String[] {"name", "description"});
    }


    private void requestPermission(String permission,int requestCode){
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    permission)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.


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

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    AccountManager manager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
                    Account list = manager.getAccountsByType("com.google")[0]; //primera cuenta gmail encontrada
                    mUsername =  list.name.split("@")[0]; //nos quedamos con la primer parte (antes del @)
                    registrar_usuario();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
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
