package com.dit.escuelas_de_informatica.utiles;


import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.dit.escuelas_de_informatica.NewPlaceDataActivity;
import com.dit.escuelas_de_informatica.R;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

public class GeofenceTransitionsIntentService extends IntentService {

    private String TAG = "GeofenceTransitionsIS";

    public GeofenceTransitionsIntentService() {
        super("GeofenceTransitionsIS");
    }

    /* Manejador de los eventos del Geofence */
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = String.valueOf(geofencingEvent.getErrorCode());
            Log.e(TAG, errorMessage);
            return;
        }

        // Obtenemos el tipo de transición
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Evaluamos si la transición ocurrida es de interés
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            // Obtenemos los geofences que se activaron con el evento.
            // Un unico evento puede estar relacionado a multiples geofences
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            // Utilizamos un metodo especial para obtener detalles de la transición
            String geofenceTransitionDetails = getGeofenceTransitionDetails(
                    geofenceTransition,
                    triggeringGeofences
            );

            // Mostramos una notificación con los detalles
            sendNotification(geofenceTransitionDetails);
            Log.i(TAG, geofenceTransitionDetails);
        } else {
            Log.e(TAG, "Transición invalida");
        }
    }


    /* Permite crear una notificación con los detalles de una transición */
    private void sendNotification(String notificationDetails) {
        // Creamos un Intent explicito que iniciará un activity en particular
        // cuando el usuario interactúe con la notificacion
        Intent notificationIntent = new Intent(getApplicationContext(), NewPlaceDataActivity.class);

        // Construimos un task stack para organizar las vistas de la app cuando esto suceda.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        // Agregamos nuestro activity al task stack como parent.
        stackBuilder.addParentStack(NewPlaceDataActivity.class);

        // Agregamos el Intent al stack.
        stackBuilder.addNextIntent(notificationIntent);

        // Utilizamos un PendingIntent para contener el back stack completo.
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Obtenemos un notification builder compatibles con versiones de Android >= 4
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        // Definimos las propiedades de la notificacion a través del builder.
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.mipmap.ic_launcher))
                .setContentTitle(notificationDetails)
                .setContentText("Transicionando")
                .setContentIntent(notificationPendingIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        // Permitimos la eliminación automática de la notificacion una vez que el usuario la toca
        builder.setAutoCancel(true);

        // Obtenemos una instancia del Notification manager
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Le pedimos que maneje nuestra notificación
        mNotificationManager.notify(0, builder.build());
    }

    /* Permite obtener detalles de una transición para uno o más geofences
    * Devolviendo una cadena personalizada con los datos
    */
    private String getGeofenceTransitionDetails(
            int geofenceTransition,
            List<Geofence> triggeringGeofences) {

        String geofenceTransitionString = getTransitionString(geofenceTransition);

        ArrayList<String> triggeringGeofencesIdsList = new ArrayList<>();
        for (Geofence geofence : triggeringGeofences) {
            triggeringGeofencesIdsList.add(geofence.getRequestId());
        }
        String triggeringGeofencesIdsString = TextUtils.join(", ",  triggeringGeofencesIdsList);

        return geofenceTransitionString + ": " + triggeringGeofencesIdsString;
    }

    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return getString(R.string.geofence_transition_entered);
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return getString(R.string.geofence_transition_exited);
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                return getString(R.string.geofence_transition_dwell);
            default:
                return getString(R.string.unknown_geofence_transition);
        }
    }

}