package com.dit.escuelas_de_informatica;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.app.ListFragment;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dit.escuelas_de_informatica.utiles.Adaptador_lista;
import com.dit.escuelas_de_informatica.utiles.Elemento_lista;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.Inflater;

public class MainFragment extends ListFragment {
    private String mContenido;
    //    ArrayAdapter<String> mAdapter;
    private Adaptador_lista mAdapter = null;
    private LayoutInflater inf = null;
    private ViewGroup cont = null;


    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
//**********************************************************
        this.inf = inflater;
        this.cont = container;
//**********************************************************
        View rootView = inflater.inflate(R.layout.fragment_lista, container, false);

        mContenido = (String) getArguments().get("contenido");
        ArrayList<Elemento_lista> listaContenido = new ArrayList<Elemento_lista>();
        JSONArray contenidoJson = null;
        try {
            contenidoJson = new JSONArray(mContenido);

            for (int i = 0; i < contenidoJson.length(); i++) {
                JSONObject c = contenidoJson.getJSONObject(i);
                listaContenido.add(
                        new Elemento_lista(c.getString("encabezado"), c.getString("cuerpo"), c.getInt("idImagen") )
                );
            }
        } catch (JSONException e) {
            e.printStackTrace();
//            return rootView;
        }

        ListView listView = (ListView) rootView.findViewById(android.R.id.list);
        this.mAdapter = new Adaptador_lista(getActivity(), R.layout.elemento_lista, listaContenido){

            @Override
            public void onEntrada(Object entrada, View view) {
                if (entrada != null) {
                    TextView encabezado = (TextView) view.findViewById(R.id.textView_encabezado);
                    if (encabezado != null)
                        encabezado.setText(((Elemento_lista) entrada).get_encabezado());

                    TextView cuerpo = (TextView) view.findViewById(R.id.textView_cuerpo);
                    if (cuerpo != null)
                        cuerpo.setText(((Elemento_lista) entrada).get_cuerpo());

                    ImageView imagen = (ImageView) view.findViewById(R.id.imageView_imagen);
                    if (imagen != null)
                        imagen.setImageResource(((Elemento_lista) entrada).get_idImagen());
                }
            }
        };
        listView.setAdapter(this.mAdapter);
        return rootView; // listView;
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Toast.makeText(getActivity(), "Ha pulsado " + this.mAdapter.getItem(position),
                Toast.LENGTH_SHORT).show();
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void updateListAdapter(String contenido, Context context) throws JSONException {
        /* Permite cambiar el contenido de la lista de modo dinámico */
        this.mAdapter.clearData();
        JSONArray contenidoJson = new JSONArray(contenido);
        ArrayList<Elemento_lista> listaContenido = new ArrayList<Elemento_lista>();
        for (int i = 0; i < contenidoJson.length(); i++) {
            JSONObject c = contenidoJson.getJSONObject(i);
            listaContenido.add(
                    new Elemento_lista(c.getString("encabezado"), c.getString("cuerpo"), c.getInt("idImagen"))
            );
        }
        this.mAdapter.addAll(listaContenido);
        this.mAdapter.notifyDataSetChanged();
    }

}
