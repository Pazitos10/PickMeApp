package com.dit.escuelas_de_informatica.utiles;

/**
 * Created by bruno on 04/09/17.
 */

public class ServerComunicationException extends Exception {
    private String msg;
    public ServerComunicationException(String msg) {
        this.msg = msg;
    }

    public ServerComunicationException() {}
}
