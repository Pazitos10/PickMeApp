package com.dit.escuelas_de_informatica;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.dit.escuelas_de_informatica.utiles.ServerComunication;
import com.dit.escuelas_de_informatica.utiles.ServerComunicationException;
import com.dit.escuelas_de_informatica.utiles.SocketListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by developer on 8/31/17.
 */

public abstract class ElementsList implements SocketListener{


    public String mEventName;
    public ListView mListView;
    public ArrayList<Map<String, String>> mList;
    public SimpleAdapter mAdapter;
    public String[] mElementsField;
    public Activity mContext;

    public ElementsList(final MainActivity context, String eventName, final int idView, String[] elementsField) {
        this.mList = new ArrayList<>();
        this.mEventName = eventName;
        this.mElementsField = elementsField;
        this.mContext = context;
        ServerComunication serverComunication = ServerComunication.getInstance();
        //serverComunication.on(("get" + this.mEventName),this);
        String[] events = new String[]{("get" + this.mEventName),("act-" + this.mEventName)};
        serverComunication.on(events,this);
        try {
            serverComunication.emit(("get" + this.mEventName));
        } catch (ServerComunicationException e) {
            e.printStackTrace();
        }
        mListView = (ListView)context.findViewById(idView);
        mAdapter = new SimpleAdapter(context, mList,
                android.R.layout.simple_list_item_2,
                mElementsField,
                new int[] {android.R.id.text1,
                        android.R.id.text2,
                });


        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mListView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                onClick(i);
            }
        });


    }

    @Override
    public void call(String eventName, Object[] args) {}

    abstract public void fillList(Object[] args);
    abstract public void refreshItem(JSONObject obj);
    abstract public void onClick(int i);

}
