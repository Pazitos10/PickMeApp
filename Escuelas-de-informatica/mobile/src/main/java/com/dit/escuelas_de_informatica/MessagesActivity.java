package com.dit.escuelas_de_informatica;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.dit.escuelas_de_informatica.modelo.Message;
import com.dit.escuelas_de_informatica.utiles.ServerComunicationException;
import com.dit.escuelas_de_informatica.utiles.Utils;

/**
 * Created by developer on 9/4/17.
 */

public class MessagesActivity  extends AppCompatActivity {


    private EditText mEditDestination, mEditMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_new_message_data);
        mEditDestination = (EditText) findViewById(R.id.editTextDestination);
        mEditMessage = (EditText) findViewById(R.id.editTextMessage);


    }

    public void sendMessage(View view)
    {
        Log.d("SendMessage", "Registrando Usuario");
        //mEditDestination.getText()
        String message = mEditMessage.getText().toString();
        String destination = mEditDestination.getText().toString();
        if(message != "" & destination != "" ){
            Message newMessage = new Message(destination,message);
            try {
                newMessage.send();
            } catch (ServerComunicationException e) {
                showSnackbarServerDisconnected(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setResult(RESULT_CANCELED);
                        finish();
                    }
                });
            }
        }else{
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }


    }

    private void showSnackbarServerDisconnected(View.OnClickListener onClickListener) {
        Utils.showSnackbar(this, "Error al conectar", "Reintentar", onClickListener);
    }


}
