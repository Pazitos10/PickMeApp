package com.dit.escuelas_de_informatica;

import com.dit.escuelas_de_informatica.modelo.Message;
import com.dit.escuelas_de_informatica.modelo.Place;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by developer on 8/31/17.
 */

public class MessagesList extends ElementsList {

    ArrayList<Message> mMessages;

    public MessagesList(MainActivity context, String eventName, int idView, String[] elementsField) {
        super(context, eventName, idView, elementsField);
        this.mMessages = new ArrayList<Message>();
    }

     @Override
    public void call(String eventName, Object[] args) {
        if (("get" + this.mEventName).equals("getmensajes")){ //TODO: capaz que habria que cambiar esto. No me deja usar un SWITCH CASE
            this.fillList(args);
        }else{
            try {
                this.refreshItem(new JSONObject((String)args[0]));
            } catch (JSONException e) {
                e.printStackTrace();
            }
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

    @Override
    public void refreshItem(JSONObject l) {
        Message message = null;
        try {
            message = new Message(l.get("origen").toString(),l.get("destino").toString(),l.get("mensaje").toString(),l.get("tiempo").toString());
            mMessages.add(message);
            Map<String, String> itemMessage = new HashMap<String, String>();
            itemMessage.put("timeUser",message.getmTime()+"  "+message.getSource());
            itemMessage.put("message", message.getMessageText());
            mList.add(itemMessage);
            mContext.runOnUiThread(new Runnable() {
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
