package com.dit.escuelas_de_informatica;

import android.view.View;

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

public class PlacesList extends ElementsList {

    ArrayList<Place> mPlaces;

    public PlacesList(MainActivity context, String eventName, int idView, String[] elementsField) {
        super(context, eventName, idView, elementsField);
        this.mPlaces = new ArrayList<Place>();
        this.mListView.setVisibility(View.VISIBLE);
    }

     @Override
    public void call(String eventName, Object[] args) {
        if (("get" + this.mEventName).equals("getlugares")){ //TODO: capaz que habria que cambiar esto. No me deja usar un SWITCH CASE
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
        Place place = null;
        try {
            place = new Place(l.get("nombre").toString(),l.get("descripcion").toString(),new LatLng(0.1,0.5));
            mPlaces.add(place);
            Map<String, String> itemPlace = new HashMap<String, String>();
            itemPlace.put("name",place.getName());
            itemPlace.put("description", place.getDescription());
            mList.add(itemPlace);
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
