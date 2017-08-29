package com.dit.escuelas_de_informatica;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.dit.escuelas_de_informatica.utiles.Adaptador_lista;
import com.dit.escuelas_de_informatica.utiles.Elemento_lista;
import com.dit.escuelas_de_informatica.utiles.ServerComunication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton mBotonFlotante;
    private JSONArray mListaLugares;
    private JSONArray mListaMensajes;
    private JSONArray mListaUsuarios;
    //    private String[] mListaContenido = new String[]{"Lugar 1", "Lugar 2"};
    ArrayList<Elemento_lista> mListaContenido = new ArrayList<Elemento_lista>();
    private int mFragmentActivo;
    private Toolbar mToolbar;
    private ServerComunication mConeccion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        mBotonFlotante = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mFragmentActivo = R.id.navigation_places;
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
            ArrayAdapter<String> adaptador;
            lista = (ListView)findViewById(R.id.lista);
            switch (item.getItemId()) {
                case R.id.navigation_places:
                    ArrayList<Map<String, String>> listaLugares = mConeccion.getListaLugares();
                    SimpleAdapter adapter = new SimpleAdapter(MainActivity.this, listaLugares,
                            android.R.layout.simple_list_item_2,
                            new String[] {"encabezado", "cuerpo"},
                            new int[] {android.R.id.text1,
                                    android.R.id.text2,
                            });
                    lista.setAdapter(adapter);

                    return true;

                case R.id.navigation_contacts:

                    ArrayList<Map<String, String>> listaContactos = mConeccion.getListaUsuarios();
                    adapter = new SimpleAdapter(MainActivity.this, listaContactos,
                            android.R.layout.simple_list_item_1,
                            new String[] {"encabezado", "cuerpo"},
                            new int[] {android.R.id.text1,
                                    android.R.id.text2,
                            });
                    lista.setAdapter(adapter);
                    return true;

                case R.id.navigation_messages:
                    String mListaMensajes2 = "[{'encabezado':'msj 1', 'cuerpo':'Hola', 'idImagen':'0'}, {'encabezado':'Msj 2', 'cuerpo':'Chau', 'idImagen':'0'}]";
                                        mFragmentActivo = R.id.navigation_messages;
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
                switch (mFragmentActivo){
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
            mConeccion = new ServerComunication(MainActivity.this, "http:pickmeupserver.com");
        } catch (URISyntaxException e) {
            Toast.makeText(MainActivity.this, "URI", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (SocketException e) {
            Toast.makeText(MainActivity.this, "SOCKET!!!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
//        mConeccion.refresh();
        //String lugares = "[{'encabezado':'Lugar 1', 'cuerpo':'muchos arboles', 'idImagen':0}, {'encabezado':'Lugar 2', 'cuerpo':'pocos arboles', 'idImagen':0}]";
        String lugares = "[ {'idImagen':0, 'encabezado':'Casita', 'cuerpo':'es mi casita'} ]";
        //mListaLugares = new JSONArray(lugares);
        //Bundle bundle = new Bundle();
        //bundle.putString("contenido", mConeccion.getListaLugares().toString());
       // String lis = (String) mConeccion.getListaLugares().toString().replace('"',' ').replace("=",":");
        //bundle.putString("contenido", lis);
        //bundle.putString("contenido", lugares);
//        bundle.putString("contenido", mListaLugares.toString());
       /* this.mMainFragment = new MainFragment();
        this.mMainFragment.setArguments(bundle);
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.frame_content, this.mMainFragment, "mainFragment");
        transaction.commit();*/
    }


}
