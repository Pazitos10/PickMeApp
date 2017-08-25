package com.dit.escuelas_de_informatica.utiles;

import com.dit.escuelas_de_informatica.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by root on 17/07/17.
 */

public class Elemento_lista {
    private int id_imagen;
    private String encabezado;
    private String cuerpo;

    public Elemento_lista (String unEncabezado, String unCuerpo, Integer unIdImagen) { //int idImagen, String un_titulo, String una_descripcion) {
        if(unEncabezado != null) {
            this.encabezado = unEncabezado;
        }
        if(unCuerpo != null) {
            this.cuerpo = unCuerpo;
        }
        if(unIdImagen != 0) {
            this.id_imagen = unIdImagen;
        }
    }

    public String get_encabezado() {
        return encabezado;
    }

    public String get_cuerpo() {
        return cuerpo;
    }

    public int get_idImagen() {
        return id_imagen;
    }

   @Override
    public String toString() {
        return "{'encabezado':'" +this.encabezado+ "', 'cuerpo':'"+this.cuerpo+"', 'idImagen':0}";
    }

    public Map<String, String> jsonizar(){
        Map<String, String> map = new HashMap<String, String>();
        map.put("encabezado", this.encabezado);
        map.put("cuerpo", this.cuerpo);
        map.put("idImagen", "0");
        return map;
    }
}