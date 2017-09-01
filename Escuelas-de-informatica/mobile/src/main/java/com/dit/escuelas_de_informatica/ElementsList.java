package com.dit.escuelas_de_informatica;

import android.app.Activity;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.dit.escuelas_de_informatica.utiles.ServerComunication;
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
        //TODO: aca necesito el server de una manera comoda.
        ServerComunication serverComunication = ServerComunication.getInstance();
        //serverComunication.on(("get" + this.mEventName),this);
        String[] events = new String[]{("get" + this.mEventName),("act-" + this.mEventName)};
        serverComunication.on(events,this);
        serverComunication.emit(("get" + this.mEventName));
        mListView = (ListView)context.findViewById(idView);
        mAdapter = new SimpleAdapter(context, mList,
                android.R.layout.simple_list_item_2,
                mElementsField,
                new int[] {android.R.id.text1,
                        android.R.id.text2,
                });
        mListView.setAdapter(mAdapter);


    }

    @Override
    public void call(String eventName, Object[] args) {}

    abstract public void fillList(Object[] args);
    abstract public void refreshItem(JSONObject obj);

}
