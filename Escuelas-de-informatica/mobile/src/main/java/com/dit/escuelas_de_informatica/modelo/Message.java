package com.dit.escuelas_de_informatica.modelo;

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



}
