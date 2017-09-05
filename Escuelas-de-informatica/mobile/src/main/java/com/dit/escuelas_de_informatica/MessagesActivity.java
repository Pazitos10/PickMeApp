package com.dit.escuelas_de_informatica;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.dit.escuelas_de_informatica.modelo.Message;

import com.dit.escuelas_de_informatica.modelo.User;
import com.dit.escuelas_de_informatica.utiles.ServerComunication;
import com.dit.escuelas_de_informatica.utiles.SocketListener;
import com.dit.escuelas_de_informatica.utiles.ServerComunicationException;
import com.dit.escuelas_de_informatica.utiles.Utils;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by developer on 9/4/17.
 */

public class MessagesActivity  extends AppCompatActivity implements SocketListener {


    private EditText mEditMessage;
    private AutoCompleteTextView mEditDestination;
    private ArrayList<User> mUsers;
    private ArrayList<String>mList;
    private ArrayAdapter<String> mAdapter;
    private Toolbar mToolbar;
    private String mIdLugar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message_data);

        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        this.setSupportActionBar(mToolbar);
        ActionBar ab = this.getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true); //Para volver hacia atrás si es posible
        ab.setTitle(R.string.title_activity_messages);

        mToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                setResult(RESULT_CANCELED, new Intent().putExtra("HasConnectionError", false));
                finish();
            }
        });


        //mEditDestination = (EditText) findViewById(R.id.editTextDestination);
        mEditMessage = (EditText) findViewById(R.id.editTextMessage);

        mUsers = new ArrayList<User>();
        mList = new ArrayList<String>();

        mAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, mList);
        mEditDestination= (AutoCompleteTextView)
                findViewById(R.id.editTextDestination);
        mEditDestination.setAdapter(mAdapter);

        ServerComunication serverComunication = ServerComunication.getInstance();
        String[] events = new String[]{("getusuarios"),("act-usuarios")};
        serverComunication.on(events,this);
        try {
            serverComunication.emit(("getusuarios"));
        } catch (ServerComunicationException e) {
            e.printStackTrace();
        }

        Intent intent = getIntent();
        mEditDestination.setText(intent.getStringExtra("username"));
        mIdLugar = intent.getStringExtra("id");
    }

    public void sendMessage(View view)
    {
        Log.d("SendMessage", "Registrando Usuario");
        //mEditDestination.getText()
        String message = mEditMessage.getText().toString().trim();
        String destination = mEditDestination.getText().toString().trim();
        if(!message.equals("") && !destination.equals("") && mList.contains(destination.toString()) ){
            Message newMessage = new Message(destination,message,mIdLugar);
            try {
                newMessage.send();
                Toast.makeText(this, "Mensaje Enviado", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                this.finish();
            } catch (ServerComunicationException e) {
                showSnackbarServerDisconnected(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setResult(RESULT_CANCELED, new Intent().putExtra("HasConnectionError", true));
                        finish();
                    }
                });
            }
        }else{
            if (message.equals("")){
                mEditMessage.setError("Error - Campo vacio");
            }
            else if (destination.equals("") || !mList.contains(destination.toString())){
                mEditDestination.setError("Error - Campo vacio, o usuario no existe");
            }else{
                mEditMessage.setError("Error - Campo vacio");
                mEditDestination.setError("Error - Campo vacio, o usuario no existe");
            }
           // TODO : usar @String!
        }


    }

    private void showSnackbarServerDisconnected(View.OnClickListener onClickListener) {
        Utils.showSnackbar(this, "Error al conectar", "Reintentar", onClickListener);
    }


    @Override
    public void call(String eventName, Object[] args) {
        if (("getusuarios").equals(eventName)){
            this.fillList(args);
        }else{
            this.refreshItem((JSONObject) args[0]);

        }
    }


    public void fillList(final Object[] args){

        try {
            JSONArray data = new JSONArray((String)args[0]);
            for(int i=0; i<data.length(); i++){
                JSONObject l = data.getJSONObject(i);
                refreshItem(l);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void refreshItem(JSONObject l) {
        User user = null;
        try {
            user = new User(l.get("id_usuario").toString(),l.get("nombre").toString());
            mUsers.add(user);
            String itemPlace = new String(l.get("nombre").toString());
            mList.add(itemPlace);
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.notifyDataSetChanged();
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
