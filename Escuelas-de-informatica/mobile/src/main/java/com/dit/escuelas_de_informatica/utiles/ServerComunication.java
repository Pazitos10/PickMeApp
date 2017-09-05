package com.dit.escuelas_de_informatica.utiles;


import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

import io.socket.client.IO;
import io.socket.emitter.Emitter;

import static java.lang.Thread.sleep;

public class ServerComunication {


    private static ServerComunication instance = null;
    private io.socket.client.Socket mSocket;
    private String mURL;

    private HashMap<String, ArrayList<Object>> listeners;

    private ServerComunication(String url) {
        try {
            mURL = url;
            mSocket = IO.socket(mURL);
            mSocket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
    protected ServerComunication() {
        // Exists only to defeat instantiation.
    }

    public static ServerComunication getInstance(String url) {
        if(instance == null) {
            instance = new ServerComunication(url);
        }
        return instance;
    }

    public static ServerComunication getInstance() {
        return instance;
    }

    public void emit(String eventName, String[] args) throws ServerComunicationException {
        try{
            sleep(1000);
            if (!mSocket.connected()) throw new ServerComunicationException();
            if (args != null)
                mSocket.emit(eventName, args);
            else
                mSocket.emit(eventName);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void emit(String eventName) throws ServerComunicationException {
        emit(eventName, null);
    }

    public void on(final String eventName, final SocketListener listener){
        mSocket.on(eventName, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                listener.call(eventName, args);
            }
        });
    }

    public void on(final String[] eventsNames, final SocketListener listener){
        for (final String eventName: eventsNames) {
            mSocket.on(eventName, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    listener.call(eventName, args);
                }
            });
        }
    }

    public void register(Object listener){}

    public void unregister(Object listener){}

    public void nofifyListeners(String eventName){}

    public String getURL() {
        return mURL;
    }

}