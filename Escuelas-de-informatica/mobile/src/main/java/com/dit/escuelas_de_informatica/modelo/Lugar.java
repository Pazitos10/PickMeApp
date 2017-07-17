package com.dit.escuelas_de_informatica.modelo;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by bruno on 22/06/17.
 */

public class Lugar {

    private String nombre;
    private String descripcion;
    private LatLng latLng;

    public Lugar() {

    }

    public Lugar(String nombre, String descripcion, LatLng latLng) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.latLng = latLng;
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
        return latLng.latitude;
    }

    public double getLongitud() {
        return this.latLng.longitude;
    }


    @Override
    public String toString() {
        return this.nombre;
    }

    public LatLng getLatLng() {
        return this.latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }
}
