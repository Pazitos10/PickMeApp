package com.dit.escuelas_de_informatica.modelo;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by bruno on 22/06/17.
 */

public class Place {

    private int mId;

    public String getName() {
        return mName;
    }

    private String mName;

    public String getDescription() {
        return mDescription;
    }

    private String mDescription;

    public LatLng getLatLng() {
        return mLatLng;
    }

    public String getId() {
        return mIdLugar;
    }

    public void setLatLng(LatLng mLatLng) {
        this.mLatLng = mLatLng;
    }

    private LatLng mLatLng;
    private String mIdLugar;

    public Place() {

    }

    public Place(String name, String description, LatLng latLng, String id) {
        this.mName = name;
        this.mDescription = description;
        this.mLatLng = latLng;
        this.mIdLugar = id;
    }


}
