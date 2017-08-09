package com.dit.escuelas_de_informatica;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        MessageApi.MessageListener {

    private MainFragment mMainFragment;
    private GoogleApiClient mGoogleApiClient;
    private static final String TAG = "MainActivity";
    private boolean mResolvingError = false;
    //Request code for launching the Intent to resolve Google Play services errors.
    private static final int REQUEST_RESOLVE_ERROR = 1000;
    private static final String START_ACTIVITY_PATH = "/start-wearapp";

    private String[] mListaContenido = new String[]{"Lugar 1", "Lugar 2"};
    private int mFragmentActivo;
    private Toolbar mToolbar;

    /* views declarations: */
    private BottomNavigationView navigation;
    private FloatingActionButton mBotonFlotante;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupViews();
        inicializar();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


    }

    /**
     * Lifecycle
     *       onStart
     *       onResume
     *       onPause
     *       onStop
     **/
    @Override
    protected void onStart() {
        super.onStart();
        if (!mResolvingError) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        if (!mResolvingError && (mGoogleApiClient != null) && (mGoogleApiClient.isConnected())) {
            //Wearable.DataApi.removeListener(mGoogleApiClient, this);
            Wearable.MessageApi.removeListener(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    private void setupViews() {
        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        mBotonFlotante = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mFragmentActivo = R.id.navigation_places;
        mBotonFlotante.setOnClickListener(this.onBotonCliqueado());

    }


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

    /**
     * MessageApi.MessageListener callback
     */
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        LOGD(TAG, "onMessageReceived() A message from watch was received:"
                + messageEvent.getRequestId() + " " + messageEvent.getPath());

    }

    /**
     * Sends an RPC to start a fullscreen Activity on the wearable.
     */
    public void onStartWearableActivityClick(android.view.MenuItem ignore) {
        LOGD(TAG, "Generating RPC");
        Toast.makeText(MainActivity.this, "Up wear!", Toast.LENGTH_SHORT).show();

        // Trigger an AsyncTask that will query for a list of connected nodes and send a
        // "start-activity" message to each connected node.
        new StartWearableActivityTask().execute();
    }

    private void sendStartActivityMessage(String node) {
        Wearable.MessageApi.sendMessage(
                mGoogleApiClient, node, START_ACTIVITY_PATH, new byte[0]).setResultCallback(
                new ResultCallback<MessageApi.SendMessageResult>() {
                    @Override
                    public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                        if (!sendMessageResult.getStatus().isSuccess()) {
                            Log.e(TAG, "Failed to send message with status code: "
                                    + sendMessageResult.getStatus().getStatusCode());
                        }
                    }
                }
        );
    }



    private class StartWearableActivityTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... args) {
            Collection<String> nodes = getNodes();
            for (String node : nodes) {
                sendStartActivityMessage(node);
            }
            return null;
        }
    }

    private Collection<String> getNodes() {
        HashSet<String> results = new HashSet<>();
        NodeApi.GetConnectedNodesResult nodes =
                Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();

        for (Node node : nodes.getNodes()) {
            results.add(node.getId());
        }

        return results;
    }


    /**
     * GoogleApiClient buil callbacks:
     *      onConnected (ConnectionCallbacks)
     *      onConnectionSuspended (ConnectionCallbacks)
     *      onConnectionFailed (OnConnectionFailedListener)
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        LOGD(TAG, "Google API Client was connected");
        mResolvingError = false;
    }

    @Override
    public void onConnectionSuspended(int cause) {
        LOGD(TAG, "Connection to Google API client was suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!mResolvingError) {

            if (result.hasResolution()) {
                try {
                    mResolvingError = true;
                    result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
                } catch (IntentSender.SendIntentException e) {
                    // There was an error with the resolution intent. Try again.
                    mGoogleApiClient.connect();
                }
            } else {
                Log.e(TAG, "Connection to Google API client has failed");
                mResolvingError = false;
            }
        }
    }

    /**
     * As simple wrapper around Log.d
     */
    private static void LOGD(final String tag, String message) {
        if (Log.isLoggable(tag, Log.DEBUG)) {
            Log.d(tag, message);
        }
    }


}
