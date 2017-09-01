package com.dit.escuelas_de_informatica;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.dit.escuelas_de_informatica.utiles.HttpResponseListener;
import com.dit.escuelas_de_informatica.utiles.ServerComunication;
import com.dit.escuelas_de_informatica.utiles.SocketListener;
import com.dit.escuelas_de_informatica.utiles.Utils;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity implements SocketListener, HttpResponseListener {
    private String API_URL = "http://192.168.0.19:5000";
    private String TAG = "MainActivity";
    private String mDeviceId;
    private FloatingActionButton mBotonFlotante;
    private int mSelectedOption;
    private Toolbar mToolbar;
    private ServerComunication mServer;
    private Intent wearCommunicationIntent;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener;
    private String mUsername;
    private ProgressDialog mLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        initServerCommunication();
        initWearCommunicationService();
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

                ListView lista;
                lista = (ListView) findViewById(R.id.lista);
                ArrayAdapter<String> adaptador;
                switch (item.getItemId()) {
                    case R.id.navigation_places:

                   /* Map<String, String> map = new HashMap<String, String>();
                    map.put("encabezado", "1");
                    map.put("cuerpo", "a");

                    listaLugares.add(map);*/

                        ListElements listElements = new ListElements(MainActivity.this, "lugares", R.id.lista, new String[]{"encabezado", "cuerpo"});
                        listElements.fillList();
                    /*

                    ArrayList<Map<String, String>> listaLugares = new ArrayList<>();
                    SimpleAdapter adapter = new SimpleAdapter(MainActivity.this, new ArrayList<Map<String, String>>(),
                            android.R.layout.simple_list_item_2,
                            new String[] {"encabezado", "cuerpo"},
                            new int[] {android.R.id.text1,
                                    android.R.id.text2,
                            });
                    lista.setAdapter(adapter);*/
                        return true;

                    case R.id.navigation_contacts:

                        return true;

                    case R.id.navigation_messages:
                        String mListaMensajes2 = "[{'encabezado':'msj 1', 'cuerpo':'Hola', 'idImagen':'0'}, {'encabezado':'Msj 2', 'cuerpo':'Chau', 'idImagen':'0'}]";
                        mSelectedOption = R.id.navigation_messages;
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
        mUsername = getUsername();
        mDeviceId = getDeviceId();
        mServer = ServerComunication.getInstance(API_URL);
        mServer.emit("conectar", new String[]{mDeviceId});
        mServer.on(new String[]{"conectar", "no_registrado"}, this);
    }

    private String getUsername() {
        AccountManager manager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
        Account list = manager.getAccountsByType("com.google")[0]; //primera cuenta gmail encontrada
        return list.name.split("@")[0]; //nos quedamos con la primer parte (antes del @)
    }

    private String getDeviceId() {
        return Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
    }


    @Override
    public void call(String eventName, Object[] args) {
        switch (eventName) {
            case "conectar":
                dismissLoadingDialog();
                conectar(args);
                return;
            case "no_registrado":
                registrar_usuario(args);
                showLoadingDialog("Registrando usuario");
                Log.d(TAG, "call: mostrando loading dialog");
                return;
        }
    }

    public void showLoadingDialog(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLoadingDialog = new ProgressDialog(MainActivity.this);
                mLoadingDialog.setMessage(msg);
                mLoadingDialog.setIndeterminate(true);
                mLoadingDialog.show();
            }
        });
    }

    public void dismissLoadingDialog() {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
        }
    }

    private void registrar_usuario(Object[] args) {
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
