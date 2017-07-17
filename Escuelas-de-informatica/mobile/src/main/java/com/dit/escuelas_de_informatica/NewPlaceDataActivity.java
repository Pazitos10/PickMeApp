package com.dit.escuelas_de_informatica;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.google.android.gms.maps.model.LatLng;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import static com.dit.escuelas_de_informatica.utiles.Utiles.mostrarToast;

public class NewPlaceDataActivity extends AppCompatActivity {

    private String TAG = "NewPlaceDataActivity";
    private String API_URL = "http://pickmeapp-pablo1n7.rhcloud.com";
    private Toolbar mToolbar;
    private EditText mEditTextNombre, mEditTextDescripcion;
    private Button mButtonGuardar;
    private LatLng latLng;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_place_data);

        Intent invokerIntent = getIntent();
        if (invokerIntent.hasExtra("latLng")) {
            latLng = (LatLng) invokerIntent.getExtras().get("latLng");
            Log.d(TAG, "onCreate: "+latLng.toString());
        }


        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true); //Para volver hacia atrás si es posible
        ab.setTitle(R.string.title_activity_places);

        mToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                setResult(RESULT_CANCELED, new Intent());
                finish();
            }
        });


        mEditTextNombre = (EditText) findViewById(R.id.editTextNombre);
        mEditTextDescripcion = (EditText) findViewById(R.id.editTextDescripcion);
        mButtonGuardar = (Button) findViewById(R.id.buttonGuardar);
        mButtonGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarLugar();
            }
        });


    }


    private void guardarLugar() {
        //Juntar la info, armar un json y hacer un post a la API
        if( mEditTextNombre.getText().toString().trim().equals("")){
            mEditTextNombre.setError(getString(R.string.invalid_place_name));
        } else {
            String nombre = mEditTextNombre.getText().toString();
            String descripcion = mEditTextDescripcion.getText().toString();
            PostTask post = new PostTask();
            post.execute(API_URL + "/guardarlugar", null, nombre, descripcion, getLatLngString(latLng));
        }

    }

    private String getLatLngString(LatLng latLng) {
        return String.valueOf(latLng.latitude)+"/"+String.valueOf(latLng.longitude);
    }


    private class PostTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... data) {
            try{
                URL url=new URL(data[0]);
                Log.d(TAG, "doInBackground: en bloque try"+ data.toString());

                //{ "nombre": "CASA", "latlng": "-42.22/-65.26", "descripcion": "es mi lugar", "usuario":"mi_nombre_de_usuario" }

                JSONObject params = new JSONObject();
                try {
                    params.put("nombre", data[2]);
                    params.put("descripcion",data[3]);
                    params.put("latlng", data[4]);
                    params.put("usuario","pepe");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                HttpURLConnection conn=(HttpURLConnection)url.openConnection();
                byte[] bytes = params.toString().getBytes("UTF-8");
                //set the output to true, indicating you are outputting(uploading) POST data
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setFixedLengthStreamingMode(bytes.length);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setRequestMethod("POST");
                OutputStream os = conn.getOutputStream();
                os.write(bytes);
                os.close();
                Log.w("RESPONSE CODE", "code " + conn.getResponseCode());
                Log.w("this is connection ",""+conn);
                InputStream errorstream = conn.getErrorStream();
                Log.w("GetErrorStream  ", ""+errorstream);
                InputStream _is;
                if (conn.getResponseCode() / 100 == 2) { // 2xx code means success
                    _is = conn.getInputStream();
                    String result = _is.toString();
                    Log.i("Success!", result);
                    mostrarToast(NewPlaceDataActivity.this, "¡Lugar guardado con éxito!");
                    Intent returnIntent = new Intent();
                    setResult(RESULT_OK, returnIntent);
                    finish();
                } else {
                    _is = conn.getErrorStream();
                    String result = _is.toString();
                    Log.i("Error != 2xx", result);
                    mostrarToast(NewPlaceDataActivity.this, "Ha ocurrido un error, intente nuevamente");
                }

            } catch(MalformedURLException ex){
                Log.d(TAG, "doInBackground: MalformedURLException");
            } catch(IOException ex){
                Log.d(TAG, "doInBackground: IOException");
                Log.d(TAG, "doInBackground: "+ex.getMessage());
            }
            return "All Done!";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

}
