package com.dit.escuelas_de_informatica;

import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.dit.escuelas_de_informatica.utiles.SocketListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by developer on 8/31/17.
 */

public class ListElements implements SocketListener{


    private String mEventName;
    private ArrayList<Map<String, String>> mlist;
    private ListView mlistView;
    private SimpleAdapter mAdapter;
    private String[] mElementsField;


    public ListElements(MainActivity context,String eventName,int idView, String[] elementsField) {
        this.mlist = new ArrayList<>();
        this.mEventName = eventName;
        this.mElementsField = elementsField;
        //TODO: aca necesito el server de una manera comoda.
        this.mlistView = (ListView)context.findViewById(idView);
        this.mAdapter = new SimpleAdapter(context,mlist,
                android.R.layout.simple_list_item_2,
                mElementsField,
                new int[] {android.R.id.text1,
                        android.R.id.text2,
        });
        this.mlistView.setAdapter(mAdapter);
    }

    @Override
    public void call(String eventName, Object[] args) {
        if ("get" + this.mEventName == eventName){ //TODO: capaz que habria que cambiar esto. No me deja usar un SWITCH CASE
            //this.fillList(args);
        }
    }

    //public void fillList(Object[] args){
    public void fillList(){
        Map<String, String> map = new HashMap<String, String>();
        map.put("encabezado", "1");
        map.put("cuerpo", "a");
        this.mlist.add(map);
        mAdapter.notifyDataSetChanged();
    }
}
