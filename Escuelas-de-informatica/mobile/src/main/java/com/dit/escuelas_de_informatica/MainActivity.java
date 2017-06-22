package com.dit.escuelas_de_informatica;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    MainFragment mMainFragment;
    private String[] mListaContenido = new String[]{"Lugar 1", "Lugar 2"};


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.navigation_places:
                    mListaContenido = new String[]{"Lugar 1", "Lugar 2"};
                    mMainFragment.updateListAdapter(mListaContenido);
                    return true;
                case R.id.navigation_contacts:
                    mListaContenido = new String[]{"Contacto 1", "Contacto 2"};
                    mMainFragment.updateListAdapter(mListaContenido);
                    return true;
                case R.id.navigation_messages:
                    mListaContenido = new String[]{"Mensaje 1", "Mensaje 2"};
                    mMainFragment.updateListAdapter(mListaContenido);
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
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        inicializar();

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
