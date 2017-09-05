package com.dit.escuelas_de_informatica.modelo;

import com.dit.escuelas_de_informatica.utiles.ServerComunication;
import com.dit.escuelas_de_informatica.utiles.SocketListener;

/**
 * Created by bruno on 22/06/17.
 */

public class Message {

    String mSource;
    String mDestination;
    String mMessageText;
    String mTime;

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

    public Message(String source,String destination,String messageText,String time) {
        mSource = source;
        mDestination = destination;
        mMessageText = messageText;
        mTime = time;

    }
    public Message(String destination,String messageText) {
        mDestination = destination;
        mMessageText = messageText;
    }

    public void send(){
        ServerComunication socket = ServerComunication.getInstance();
        socket.emit("act-mensajes",new String[]{this.mDestination,this.mMessageText});
    }


}
