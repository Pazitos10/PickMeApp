package com.dit.escuelas_de_informatica;

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
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.dit.escuelas_de_informatica.utiles.ServerComunication;
import com.dit.escuelas_de_informatica.utiles.SocketListener;
import com.dit.escuelas_de_informatica.utiles.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements SocketListener{
    private String mDeviceId;
    private FloatingActionButton mBotonFlotante;
    private int mSelectedOption;
    private Toolbar mToolbar;
    private ServerComunication mServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        mBotonFlotante = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mSelectedOption = R.id.navigation_places;
        mBotonFlotante.setOnClickListener(this.onBotonCliqueado());
        try {
            inicializar();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            ListView lista;
            lista = (ListView)findViewById(R.id.lista);
            ArrayAdapter<String> adaptador;
            switch (item.getItemId()) {
                case R.id.navigation_places:

                   /* Map<String, String> map = new HashMap<String, String>();
                    map.put("encabezado", "1");
                    map.put("cuerpo", "a");

                    listaLugares.add(map);*/

                    ListElements listElements = new ListElements(MainActivity.this,"lugares",R.id.lista,new String[] {"encabezado", "cuerpo"});
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
                    String mListaMensajes2 = "[{'encabezado':'msj 1', 'cuerpo':'Hola', 'idImagen':'0'}, {'encabezado':'Msj 2', 'cuerpo':'Chau', 'idImagen':'0'}]";
                    mSelectedOption = R.id.navigation_messages;
                    return true;
            }
            return false;
        }

    };


    private View.OnClickListener onBotonCliqueado () {
        return new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                String msg = "default";
                switch (mSelectedOption){
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

    private void inicializar() throws JSONException {
        try {
            mServer = new ServerComunication("http://192.168.43.123:5000");
            mDeviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
            mServer.emit("conectar", new String[]{ mDeviceId });
            mServer.on(new String[]{"conectar", "no_registrado"}, this);

        } catch (URISyntaxException e) {
            Toast.makeText(MainActivity.this, "URI", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (SocketException e) {
            Toast.makeText(MainActivity.this, "SOCKET!!!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    @Override
    public void call(String eventName, Object[] args) {
        switch (eventName){
            case "conectar":
                conectar(args);
                return;
            case "no_registrado":
                registrar_usuario(args);
                return;
        }
    }


    private void registrar_usuario(Object[] args) {

        Log.d("ServerComunication", "Registrando Usuario");
        Utils.PostTask post = new Utils.PostTask();
        JSONObject params = new JSONObject();
        try {
            params.put("nombre", "emu");
            params.put("id_usuario", mDeviceId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        post.execute(new String[]{ mServer.getURL() +"/guardarusuario", params.toString() });

        mServer.emit("conectar", new String[]{ mDeviceId });
    }

    public void conectar(Object[] args){
        JSONObject data = (JSONObject)args[0];
        Log.d("ServerComunication", "Conectando");
        try {
            Log.d("ServerComunication", "Bienvenido "+data.getString("usuario"));
            return;
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("ServerComunication", "error al conectar: "+data.toString());
        }
    }


    public ServerComunication getServer() {
        return mServer;
    }
}
