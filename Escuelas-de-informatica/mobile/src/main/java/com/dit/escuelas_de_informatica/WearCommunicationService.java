package com.dit.escuelas_de_informatica;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.List;

public class WearCommunicationService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private final static String TAG = "LeafLabWearService";
    private static GoogleApiClient mGoogleApiClient;
    private static List<Node> mConnectedNodes;
    private Thread mServiceThread;
    private String GOOGLE_TAG = "GoogleApiClient";
    /* Listener para todos los mensajes provenientes de Wear */
    private MessageApi.MessageListener mMessageListener;

    public static void sendMessageToWear(String message, String path) {
        Node mNode = null;
        for (Node node : mConnectedNodes) {
            if (node != null && node.isNearby()) {
                mNode = node;
                Log.d(TAG, " Enviar mensaje a: " + mNode.getDisplayName());
                Wearable.MessageApi
                        .sendMessage(mGoogleApiClient,
                                node.getId(),
                                path,
                                message.getBytes()).await();
            }
        }
        if (mNode == null) {
            Log.d(TAG, " Not connected! :(");
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        mServiceThread = new Thread(new Runnable() {
            @Override
            public void run() {
                mConnectedNodes = new ArrayList<>();
                mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                        .addConnectionCallbacks(WearCommunicationService.this)
                        .addOnConnectionFailedListener(WearCommunicationService.this)
                        .addApi(Wearable.API)
                        .build();
                mGoogleApiClient.connect();
                mMessageListener = getMessageListener();
            }
        });
        mServiceThread.start();
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mConnectedNodes.clear();
        stopSelf();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void updateNodes() {
        Log.d(GOOGLE_TAG, "updateNodes: nodes connected -> " + mConnectedNodes.size());
        for (Node node : mConnectedNodes) {
            if (node != null && node.isNearby()) {
                Wearable.MessageApi
                        .addListener(mGoogleApiClient, mMessageListener);
            }
        }
    }

    /* Google Api Connection Callbacks */
    @Override
    public void onConnected(Bundle connectionHint) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(GOOGLE_TAG, "onConnected: mGoogleApiClient is connected? " + mGoogleApiClient.isConnected());
                Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
                    @Override
                    public void onResult(@NonNull NodeApi.GetConnectedNodesResult nodesResult) {
                        mConnectedNodes = nodesResult.getNodes();
                        updateNodes();
                    }
                });
            }
        }).start();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.d(GOOGLE_TAG, "onConnectionSuspended: " + cause);
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.d(GOOGLE_TAG, "onConnectionFailed: " + result);
    }

    public void initGoogleApi(Context context) {
        Log.d(TAG, "initGoogleApi ");
        if (context != null) {
            mGoogleApiClient.connect();
        } else {
            Log.d(TAG, "initGoogleApi: app context is not defined");
        }
    }

    private MessageApi.MessageListener getMessageListener(){
        return new MessageApi.MessageListener() {
            @Override
            public void onMessageReceived(MessageEvent messageEvent) {
                String path = messageEvent.getPath();
                String data = new String(messageEvent.getData());
                Log.d(TAG, "onMessageReceived: "+ path + " -> " + data);
                //TODO: hacer algo con el mensaje recibido
            }
        };
    }

}
