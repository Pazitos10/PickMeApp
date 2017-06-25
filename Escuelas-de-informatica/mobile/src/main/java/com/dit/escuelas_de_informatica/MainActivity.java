package com.dit.escuelas_de_informatica;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    MainFragment mMainFragment;
    private FloatingActionButton mBotonFlotante;
    private String[] mListaContenido = new String[]{"Lugar 1", "Lugar 2"};
    private int mFragmentActivo;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.navigation_places:
                    mListaContenido = new String[]{"Lugar 1", "Lugar 2"};
                    mMainFragment.updateListAdapter(mListaContenido);
                    mFragmentActivo = R.id.navigation_places;
                    return true;
                case R.id.navigation_contacts:
                    mListaContenido = new String[]{"Contacto 1", "Contacto 2"};
                    mMainFragment.updateListAdapter(mListaContenido);
                    mFragmentActivo = R.id.navigation_contacts;
                    return true;
                case R.id.navigation_messages:
                    mListaContenido = new String[]{"Mensaje 1", "Mensaje 2"};
                    mMainFragment.updateListAdapter(mListaContenido);
                    mFragmentActivo = R.id.navigation_messages;
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        mBotonFlotante = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mFragmentActivo = R.id.navigation_places;
        mBotonFlotante.setOnClickListener(this.onBotonCliqueado());
        inicializar();

    }

    private View.OnClickListener onBotonCliqueado () {
        return new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                String msg = "default";
                switch (mFragmentActivo){
                    case R.id.navigation_places:
                        msg = "Crear nuevo lugar";
                        enviarAlMapa();
                        break;
                    case R.id.navigation_contacts:
                        msg = "Agregar nuevo contacto";
                        break;
                    case R.id.navigation_messages:
                        msg = "Crear nuevo mensaje";
                        break;
                }
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        };
    }

    private void enviarAlMapa() {
        Intent intent = new Intent(this, PlacesActivity.class);
        startActivity(intent);
    }


    private void inicializar() {
        Bundle bundle = new Bundle();
        bundle.putStringArray("contenido", mListaContenido);
        mMainFragment = new MainFragment();
        mMainFragment.setArguments(bundle);
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.frame_content, mMainFragment, "mainFragment");
        transaction.commit();

    }


}
