package com.dit.escuelas_de_informatica.utiles;


import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.ProtocolException;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//import javax.net.ssl.HttpsURLConnection;

import io.socket.client.IO;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.Socket;

/**
 * Created by root on 20/07/17.
 */

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

    public void emit(String eventName, String[] args){
        mSocket.emit(eventName, args);
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