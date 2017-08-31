package com.dit.escuelas_de_informatica;

import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.dit.escuelas_de_informatica.utiles.ServerComunication;
import com.dit.escuelas_de_informatica.utiles.SocketListener;

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


    public ElementsList(final MainActivity context, String eventName, final int idView, String[] elementsField) {
        this.mList = new ArrayList<>();
        this.mEventName = eventName;
        this.mElementsField = elementsField;
        //TODO: aca necesito el server de una manera comoda.
        ServerComunication serverComunication = ServerComunication.getInstance();
        serverComunication.on(("get" + this.mEventName),this);
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
    abstract public void refreshItem(Object[] args);


    /*@Override
    public void call(String eventName, Object[] args) {
        if (("get" + this.mEventName).equals("getlugares")){ //TODO: capaz que habria que cambiar esto. No me deja usar un SWITCH CASE
            this.fillList(args);
        }
    }

    public void fillList(Object[] args){
    //public void fillList(){
        Map<String, String> map = new HashMap<String, String>();
        map.put("encabezado", "1");
        map.put("cuerpo", "a");
        this.mList.add(map);
        mAdapter.notifyDataSetChanged();
    }*/
}
