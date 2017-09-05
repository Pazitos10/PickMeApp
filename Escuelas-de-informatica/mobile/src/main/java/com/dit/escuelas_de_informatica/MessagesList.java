package com.dit.escuelas_de_informatica;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

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

        mAdapter = new SimpleAdapter(
                context,
                mList,
                android.R.layout.simple_list_item_2,
                mElementsField,
                new int[] {android.R.id.text1, android.R.id.text2 }){

                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        TextView text = (TextView) view.findViewById(android.R.id.text1);
                        if (!mList.get(position).get("idLugar").equals(""))
                            text.setTextColor(Color.RED);
                        else
                            text.setTextColor(Color.BLACK);
                        return view;
                    }
        };




        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mListView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public void call(String eventName, Object[] args) {
        if (("get" + this.mEventName).equals(eventName)){
            this.mMessages = new ArrayList<Message>();
            this.mList.clear();
            this.fillList(args);

        }else{
            this.refreshItem((JSONObject) args[0]);
        }
    }

    public void fillList(final Object[] args){

                try {
                    JSONArray data = new JSONArray((String)args[0]);
                    for(int i=data.length() -1 ; i>=0 ;i--){
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
            message = new Message(l.get("origen").toString(),l.get("destino").toString(),l.get("mensaje").toString(),l.get("tiempo").toString(),l.get("id_lugar").toString());
            mMessages.add(message);
            Map<String, String> itemMessage = new HashMap<String, String>();
            itemMessage.put("timeUser",message.getmTime()+"  "+message.getSource());
            itemMessage.put("message", message.getMessageText());
            itemMessage.put("idLugar",message.getIdLugar());
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

    @Override
    public void onClick(int i) {
        Map<String, String> lst = mList.get(i);
        String dst = lst.get("timeUser").split("  ")[1];
        Log.d("MESSEGERLIST","onclick");
        ((MainActivity) mContext).newMessageForm(dst,"");
    }

}
