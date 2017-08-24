package com.dit.escuelas_de_informatica.utiles;


import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.dit.escuelas_de_informatica.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.ProtocolException;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Random;

//import javax.net.ssl.HttpsURLConnection;

import io.socket.client.IO;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.Socket;

import static android.R.attr.data;
import static android.R.attr.targetActivity;
import static com.dit.escuelas_de_informatica.utiles.Utiles.mostrarToast;

/**
 * Created by root on 20/07/17.
 */

public class ServerComunication {

    private Context mContexto;
    private io.socket.client.Socket mSocket;
    private String mIdDispositivo;
    private String mUser;
    private String mNick;
    private String mServer;
    private JSONArray mListaMensajeses;
    private JSONArray mListaUsuarios;
    private JSONArray mListaLugares;

    public ServerComunication(Context contexto, String server) throws URISyntaxException, SocketException, JSONException {
        mServer = server;
        mContexto = contexto;
        mServer = "http://192.168.0.105:5000/";
        mIdDispositivo = getMACUser();
        IO.Options sParams = new IO.Options();
        //sParams.query = "user_id="+mIdDispositivo;
        mSocket = IO.socket(mServer);

        mSocket.on("conectar", new Emitter.Listener(){
            @Override
            public void call(Object... args) {
                Log.d("ServerComunication", "call: "+ args.toString());
                String data = (String)args[0];
                JSONObject contenido = null;
                Log.d("ServerComunication", "Conectando");

                try {
                    contenido = new JSONObject(data);
                    mNick = contenido.getString("usuario");
                    Log.d("ServerComunication", "Bienvenido "+mNick);

                    return;
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("ServerComunication", "error al conectar: "+contenido.toString());
                }
            }
        });

        mSocket.on("no_registrado", new Emitter.Listener(){
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                registrarUsuario();
            }
        });

        mSocket.on("mensaje", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String data = (String)args[0];
                JSONObject contenido = null;
                try {
                    contenido = new JSONObject(data);
                    JSONArray mMensajesJSON = new JSONArray(contenido.get("contenido"));
                    for(int i=0; i<mMensajesJSON.length(); i++){
                        JSONObject m = mMensajesJSON.getJSONObject(i);
                        m.put("encabezado", m.getString("origen"));
                        m.put("cuerpo", m.getString("mensaje"));
                        m.put("idImagen", 0);
                        mListaMensajeses.put(m);
                    }

                } catch (JSONException e) { e.printStackTrace();}
            }
        });

        mSocket.on("getusuarios", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String data = (String)args[0];
                try {
                    JSONArray mUsuariosJSON = new JSONArray(data);
                    mListaUsuarios = new JSONArray();
                    for(int i=0; i<mUsuariosJSON.length(); i++){
                        Log.d("ServerComunication", "usuario -> "+mUsuariosJSON.getJSONObject(i).toString());
                        JSONObject u = mUsuariosJSON.getJSONObject(i);
                        u.put("encabezado", u.getString("nombre"));
                        u.put("cuerpo", u.getString("id_usuario"));
                        u.put("idImagen", 0);
                        mListaUsuarios.put(u);
                    }
                } catch (JSONException e) { e.printStackTrace();}
            }
        });

        mSocket.on("getlugares", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String data = (String)args[0];
                JSONObject contenido = null;
                try {
                    contenido = new JSONObject(data);
                    JSONArray mLugaresJSON = new JSONArray(contenido.get("contenido"));
                    mListaLugares = new JSONArray();
                    for(int i=0; i<mLugaresJSON.length(); i++){
                        JSONObject l = mLugaresJSON.getJSONObject(i);
                        l.put("encabezado", l.get("nombre"));
                        l.put("cuerpo", l.get("idUsuario"));
                        l.put("idImagen", 0);
                        mListaLugares.put(new Elemento_lista(
                                l.getString("encabezado"), l.getString("cuerpo"), l.getInt("idImagen"))
                        );
                    }
                } catch (JSONException e) { e.printStackTrace();}
            }
        });

        mSocket.on(Socket.EVENT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Exception err = (Exception)args[0];
            }
        });

        conectarse();
    }

    private String getMACUser() throws SocketException, JSONException {
        NetworkInterface netInf = NetworkInterface.getNetworkInterfaces().nextElement();
        byte[] mac = netInf.getHardwareAddress();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mac.length; i++) {
            sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
        }
        return sb.toString();
    }

    public void registrarUsuario() {
        Log.d("ServerComunication", "Registrando Usuario");
        try {
            URL url = new URL(mServer+"guardarusuario");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

//            TODO: The nick must be adquired via GUI
            String unNick = "mabeeeeeeeeeeel";//"Pepito_de_Tal";
            JSONObject data = new JSONObject();
            data.put("nombre", unNick);
            data.put("id_usuario", mIdDispositivo);

            OutputStreamWriter dStream = new OutputStreamWriter(conn.getOutputStream());
            dStream.write(data.toString());
            dStream.flush();
            dStream.close();

            int status = conn.getResponseCode();
            switch (status){
                case 200:
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line+"\n");
                    }
                    br.close();
                    JSONObject rta = new JSONObject(sb.toString());
                    if (rta.getInt("codigo") == 500){
//                        TODO: Make a Toast with the description stored in 'rta.getString("description")' and relunch Registration process
                        Log.d("ServerComunication", "Usuario no se pudo conectar: "+rta.getString("descripcion"));
                    }else{
                        mNick = rta.getString("nick");
                        Log.d("ServerComunication", "Exito! Usuario: "+mNick);
                        conectarse();
//                        TODO: Lunch Main Tasks once the user efectively connect with the server
                    }
            }

            mSocket.emit("getusuarios"); //TODO: testing if registered user is effectively registered.
        }catch (MalformedURLException e ){
            e.printStackTrace();
            Toast.makeText(mContexto, "MalformedURLException", Toast.LENGTH_SHORT).show();
        } catch (UnsupportedEncodingException e) {
            Toast.makeText(mContexto, "Encoding", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (ProtocolException e) {
            Toast.makeText(mContexto, "Protocol", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (IOException e) {
            Toast.makeText(mContexto, "IO", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (JSONException e) {
            Toast.makeText(mContexto, "Json", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    public void refresh(){
        obtenerUsuarios();
        obtenerLugares();
    }

    public void refresh(String lista){
        switch (lista) {
            case "usuarios":
                obtenerUsuarios();
                return;
            case "lugares":
                obtenerLugares();
        }
    }

    public void refresh(String lista, String usuario){
        obtenerLugares(usuario);
    }

    public void conectarse() throws JSONException {
        Log.d("ServerComunication", "Intentando Conectar");
        mSocket.connect();
        mSocket.emit("conectar", mIdDispositivo );
//        JSONObject usuario = new JSONObject();
//        usuario.put("user_id", mIdDispositivo);
//        mSocket.emit("connect", usuario.toString());
    }

    public void obtenerUsuarios(){
        mSocket.emit("getusuarios");
    }

    public void obtenerLugares(){
        mSocket.emit("getlugares");
    }


    public void obtenerLugares(String unUsuario){
        mSocket.emit("getlugares", unUsuario);
    }

    public JSONArray getListaMensajeses(){
        return mListaMensajeses;
    }

    public JSONArray getListaUsuarios(){
        return mListaUsuarios;
    }

    public JSONArray getListaLugares(){
        return mListaLugares;
    }
}