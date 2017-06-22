package com.dit.escuelas_de_informatica;

/**
 * Created by bruno on 22/06/17.
 */

public class Mensaje {

    public String cuerpo;
    public String telefono;

    public Mensaje() {
    }

    public Mensaje(String cuerpo, String telefono) {
        this.cuerpo = cuerpo;
        this.telefono = telefono;
    }

    public String getCuerpo() {
        return cuerpo;
    }

    public void setCuerpo(String cuerpo) {
        this.cuerpo = cuerpo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    @Override
    public String toString() {
        return "Mensaje{" +
                "cuerpo='" + cuerpo + '\'' +
                ", telefono='" + telefono + '\'' +
                '}';
    }
}
