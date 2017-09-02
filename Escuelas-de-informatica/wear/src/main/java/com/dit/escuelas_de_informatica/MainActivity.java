package com.dit.escuelas_de_informatica;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends Activity {

    private static final String TAG = "WearActivity";
    private TextView mTextView;
    private static final int SPEECH_REQUEST_CODE = 0;
    private String new_place_name;
    private String new_place_description;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
            }
        });
        new_place_name = "";
        new_place_description = "";
    }

    /**
     * Crea un nuevo lugar de interes en la posición actual del usuario.
     * Solicita al usuario el nombre que le quiere poner al nuevo lugar
     * de interes y su descripcion
     *
     * @param  button_pressed el boton que fue presionado
     * @return
     *
     */
    public void save_current_place_location(View button_pressed){
        Log.d(TAG, "save_current_place_location: GUARDAR EL LUGAR CON LA UBICACION ACTUAL");
        start_voice_recognizer("Ingrese el nombre del nuevo lugar");
    }

    private void start_voice_recognizer(String encabezado) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                encabezado);

        // Start the activity, the intent will be populated with the speech text
        startActivityForResult(intent, SPEECH_REQUEST_CODE);

    }

    // This callback is invoked when the Speech Recognizer returns.
    // This is where you process the intent and extract the speech text from the intent.
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            if (new_place_name.length() == 0){
                new_place_name = spokenText;
            }else{
                new_place_description = spokenText;
            }
            // Do something with spokenText
            Log.d(TAG, "save_current_place_location: "+spokenText);
        }
        super.onActivityResult(requestCode, resultCode, data);
        if (new_place_description.length() == 0){
            start_voice_recognizer("Ingrese la descripcion del nuevo lugar");
        }else {
            Log.d(TAG, "save_current_place_location: GUARDAR EL LUGAR: "+new_place_name + ": "+ new_place_description);
        }
    }
}
