package com.dit.escuelas_de_informatica.utiles;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.dit.escuelas_de_informatica.NewPlaceDataActivity;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;


public class Utils {
    private static String TAG = "Utils";
    public static String responseCode;


    public static void showToast(final Activity activity, final String msg) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static String getLatLngString(LatLng newPoint) {
        return String.valueOf(newPoint.latitude)+"/"+String.valueOf(newPoint.longitude);
    }




    public static class PostTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... data) {
            try{
                URL url=new URL(data[0]);
                Log.d(TAG, "doInBackground: en bloque try"+ data.toString());
                HttpURLConnection conn = setupConnection(url, "POST", "application/json", "UTF-8");
                OutputStreamWriter dStream = new OutputStreamWriter(conn.getOutputStream());
                dStream.write(data[1]); //data[1] es un JSONObject con los parametros
                dStream.flush();
                dStream.close();
                responseCode = String.valueOf(conn.getResponseCode());
            } catch(MalformedURLException ex){
                Log.d(TAG, "doInBackground: MalformedURLException");
            } catch(IOException ex){
                Log.d(TAG, "doInBackground: IOException");
                Log.d(TAG, "doInBackground: "+ex.getMessage());
            }
            return responseCode;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d(TAG, "onPostExecute: "+s);
        }
    }

    private static HttpURLConnection setupConnection(URL url, String method, String contentType, String encoding) {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod(method);
            conn.addRequestProperty("Accept", contentType);
            conn.setRequestProperty("Content-Type", contentType+"; charset="+encoding);
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return conn;

    }




}
