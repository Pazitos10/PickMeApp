package com.dit.escuelas_de_informatica;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
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

import com.dit.escuelas_de_informatica.utiles.HttpResponseListener;
import com.dit.escuelas_de_informatica.utiles.ServerComunication;
import com.dit.escuelas_de_informatica.utiles.ServerComunicationException;
import com.dit.escuelas_de_informatica.utiles.SocketListener;
import com.dit.escuelas_de_informatica.utiles.Utils;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity implements SocketListener, HttpResponseListener {
    private static final int ACCOUNTS_REQUEST_CODE = 33465;
    private String API_URL = "http://192.168.0.107:5000";
    private static final int MESSAGES_REQUEST_CODE = 1;
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
    private ProgressDialog mLoadingDialog;
    private BottomNavigationView mNavigation;
    private View.OnClickListener mSnackbarActionClickListener;

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
        mNavigation = (BottomNavigationView) findViewById(R.id.navigation);
        mBotonFlotante = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        mSelectedOption = R.id.navigation_places;
        mBotonFlotante.setOnClickListener(this.onBotonCliqueado());
        mOnNavigationItemSelectedListener = getOnNavigationItemSelectedListener();
        mNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mSnackbarActionClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initServerCommunication();
            }
        };
    }

    private BottomNavigationView.OnNavigationItemSelectedListener getOnNavigationItemSelectedListener() {
        return new BottomNavigationView.OnNavigationItemSelectedListener() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.navigation_places:
                    mPlacesList.mAdapter.notifyDataSetChanged();
                    mMessagesList.mListView.setVisibility(View.GONE);
                    mPlacesList.mListView.setVisibility(View.VISIBLE);
                    mSelectedOption = R.id.navigation_places;
                    return true;

                case R.id.navigation_contacts:
                    return true;

                case R.id.navigation_messages:
                    mMessagesList.mAdapter.notifyDataSetChanged();
                    mPlacesList.mListView.setVisibility(View.GONE);
                    mMessagesList.mListView.setVisibility(View.VISIBLE);
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
                switch (mSelectedOption) {
                    case R.id.navigation_places:
                        enviarAlMapa();
                        break;
                    case R.id.navigation_contacts:
                        break;
                    case R.id.navigation_messages:
                        newMessageForm("","");
                        break;
                }
            }
        };
    }


    public void newMessageForm(String username, String idPlace){
        Intent intent = new Intent(this, MessagesActivity.class);
        intent.putExtra("username",username);
        intent.putExtra("id",idPlace);
        startActivityForResult(intent, MESSAGES_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MESSAGES_REQUEST_CODE
                && resultCode == RESULT_CANCELED
                && data.getBooleanExtra("HasConnectionError", false)) {
                    mNavigation.setSelectedItemId(R.id.navigation_places);
                    mSelectedOption = R.id.navigation_places;
                    Utils.showSnackbarServerDisconnected(this, mSnackbarActionClickListener);
                }
    }


    private void enviarAlMapa() {
        Intent intent = new Intent(this, PlacesActivity.class);
        startActivity(intent);
    }

    private void initServerCommunication() {
        mDeviceId = getDeviceId();
        try {
            mServer = ServerComunication.getInstance(API_URL);
            mServer.emit("conectar", new String[]{mDeviceId});
            mServer.on(new String[]{"conectar", "no_registrado"}, this);
        } catch (ServerComunicationException e) {
            Utils.showSnackbarServerDisconnected(this, mSnackbarActionClickListener);
        }

    }

    private void requestPermission(String permission,int requestCode){
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    permission)) {

            } else {
                ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
            }
        } else {

            registrar_usuario();
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case ACCOUNTS_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    registrar_usuario();
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
                dismissLoadingDialog();
                conectar(args);
                return;
            case "no_registrado":
                requestPermission(Manifest.permission.GET_ACCOUNTS, ACCOUNTS_REQUEST_CODE);
                return;
        }
    }

    public void showLoadingDialog(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mLoadingDialog == null){
                    mLoadingDialog = new ProgressDialog(MainActivity.this);
                    mLoadingDialog.setIndeterminate(true);
                }
                mLoadingDialog.setMessage(msg);
                mLoadingDialog.show();

            }
        });
    }

    public void dismissLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    private void registrar_usuario() {
        try{
            AccountManager manager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
            Account list = manager.getAccountsByType("com.google")[0]; //primera cuenta gmail encontrada
            mUsername =  list.name.split("@")[0]; //nos quedamos con la primer parte (antes del @)
        } catch (IndexOutOfBoundsException e){
            mUsername = "pepe";
        }
        showLoadingDialog(getString(R.string.registering_user_message));
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
            showLoadingDialog(getString(R.string.connecting_user_message));
            Log.d(TAG, "Bienvenido " + data.getString("usuario"));
            mUsername = data.getString("usuario");
            mPlacesList = new PlacesList(MainActivity.this, "lugares", R.id.placesList,new String[] {"name", "description"});
            mMessagesList = new MessagesList(MainActivity.this,"mensajes",R.id.messageList,new String[] {"timeUser", "message"});
            dismissLoadingDialog();
            return;
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "error al conectar: " + data.toString());
        }
    }

    @Override
    public void onHttpResponse(String responseCode) {
        try {
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
        } catch (ServerComunicationException e) {
            Utils.showSnackbarServerDisconnected(this, mSnackbarActionClickListener);
        }
    }

}
