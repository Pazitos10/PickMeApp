package com.dit.escuelas_de_informatica;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.app.ListFragment;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class MainFragment extends ListFragment {
    private String[] mContenido = {};
    ArrayAdapter<String> mAdapter;

    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater,container,savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_lista, container, false);

        mContenido = (String[]) getArguments().get("contenido");
        ArrayList<String> lst = new ArrayList<>(Arrays.asList(mContenido));

        ListView listView = (ListView) rootView.findViewById(android.R.id.list);
        mAdapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1, lst);
        listView.setAdapter(mAdapter);

        return rootView;
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Toast.makeText(getActivity(), "Ha pulsado " + mAdapter.getItem(position),
                Toast.LENGTH_SHORT).show();
    }

    public void updateListAdapter(String[] contenido){
        /* Permite cambiar el contenido de la lista de modo dinámico*/
        mAdapter.clear();
        mAdapter.addAll(contenido);
        mAdapter.notifyDataSetChanged();
    }

}
