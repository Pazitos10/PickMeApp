package com.dit.escuelas_de_informatica;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by bruno on 22/06/17.
 */

public class Lugar {

    private String nombre;
    private String descripcion;
    private LatLng latlng;

    public Lugar() {

    }

    public Lugar(String nombre, String descripcion, LatLng latLng) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.latlng = latLng;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getLatitud() {
        return latlng.latitude;
    }

    public double getLongitud() {
        return this.latlng.longitude;
    }


    @Override
    public String toString() {
        return this.nombre;
    }

    public LatLng getLatLng() {
        return this.latlng;
    }
}
