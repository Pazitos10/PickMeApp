package com.dit.escuelas_de_informatica.modelo;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.dit.escuelas_de_informatica.utiles.ServerComunication;
import com.dit.escuelas_de_informatica.utiles.ServerComunicationException;
import com.dit.escuelas_de_informatica.utiles.SocketListener;

/**
 * Created by bruno on 22/06/17.
 */

public class Message {

    String mSource;
    String mDestination;
    String mMessageText;
    String mTime;
    String mIdLugar;

    public String getSource() {
        return mSource;
    }

    public String getDestination() {
        return mDestination;
    }

    public String getMessageText() {
        return mMessageText;
    }

    public String getmTime() {
        return mTime;
    }

    public String getIdLugar(){return mIdLugar;}

    public Message(String source,String destination,String messageText,String time, String idLugar) {
        mSource = source;
        mDestination = destination;
        mMessageText = messageText;
        mTime = time;
        mIdLugar = idLugar;

    }
    public Message(String destination,String messageText) {
        mDestination = destination;
        mMessageText = messageText;
    }

    public Message(String destination,String messageText, String idLugar) {
        mDestination = destination;
        mMessageText = messageText;
        mIdLugar = idLugar;
    }

    public void send() throws ServerComunicationException{
        ServerComunication socket = ServerComunication.getInstance();
        socket.emit("act-mensajes",new String[]{this.mDestination,this.mMessageText,this.mIdLugar});
    }


}
