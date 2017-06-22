package com.dit.escuelas_de_informatica;

/**
 * Created by bruno on 22/06/17.
 */

public class Lugar {

    private String nombre;
    private String descripcion;
    private String latitud;
    private String longitud;

    public Lugar() {

    }

    public Lugar(String nombre, String descripcion, String latitud, String longitud) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.latitud = latitud;
        this.longitud = longitud;
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

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    @Override
    public String toString() {
        return "Lugar{" +
                "nombre='" + nombre + '\'' +
                '}';
    }
}
