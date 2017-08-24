package com.dit.escuelas_de_informatica.utiles;

/**
 * Created by root on 17/07/17.
 */

import java.util.ArrayList;
import java.util.Collection;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

import com.dit.escuelas_de_informatica.MainActivity;
import com.dit.escuelas_de_informatica.R;

import org.json.JSONArray;
import org.json.JSONException;

/** Adaptador de ListView universal, para www.jarroba.com
 * @author Ramon Invarato Menéndez
 */
public abstract class Adaptador_lista extends BaseAdapter {
//public abstract class Adaptador_lista extends ArrayAdapter{

    private ArrayList<Elemento_lista> entradas = new ArrayList<Elemento_lista>();
    private int R_layout_IdView;
    private Context contexto;
//    private ViewGroup contexto;
    private static LayoutInflater inflater=null;

//    public Adaptador_lista(ViewGroup contexto, int R_layout_IdView, ArrayList<?> entradas) {
//    public Adaptador_lista(MainActivity contexto, int R_layout_IdView, ArrayList<?> entradas) {

    public Adaptador_lista(Context contexto, int R_layout_IdView, ArrayList<Elemento_lista> entradas) {
        super();
        this.contexto = contexto;
        this.entradas = entradas;
        this.R_layout_IdView = R_layout_IdView;
        inflater = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int posicion, View view, ViewGroup pariente) {
        if (view == null) {
            LayoutInflater vi = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R_layout_IdView, null);

        }
        onEntrada (entradas.get(posicion), view);
        return view;
    }

    @Override
    public int getCount() {
        return entradas.size();
    }

    @Override
    public Object getItem(int posicion) {
        return entradas.get(posicion);
    }

    @Override
    public long getItemId(int posicion) {
        return posicion;
    }

    public void clearData() {
        // clear the data
        entradas.clear();
    }

    public void addAll(ArrayList<Elemento_lista> elementos) throws JSONException {
//        for (int i=0;i < elementos.size(); i++){
//            onEntrada(elementos.get(i), R.layout.elemento_lista);
//        }
        entradas.addAll((Collection) elementos);
    }

    /** Devuelve cada una de las entradas con cada una de las vistas a la que debe de ser asociada
     * @param entrada La entrada que será la asociada a la view. La entrada es del tipo del paquete/handler
     * @param view View particular que contendrá los datos del paquete/handler
     */
    public abstract void onEntrada (Object entrada, View view);

}