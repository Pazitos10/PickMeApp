# PickMeApp

##### Descripción breve:
El usuario configura una serie de puntos de interés (lugar de trabajo, club de deportes, entre otros).
Elige uno de los mensajes predefinidos (o redacta el/los propio/s) para avisar que lo pasen a buscar en la dirección en la que se encuentra.
Al mismo tiempo, selecciona uno de sus contactos para enviar tal mensaje.
El teléfono utiliza integración con Google Maps y Geofencing para determinar si el usuario se alejó mas de 5 o 10 mts de uno de los puntos de interés y envía un estimulo vibrotáctil al reloj para que cuando el usuario active la pantalla de este, se muestre una interfaz de confirmación para enviar el mensaje previamente mencionado.

Por otro lado, el contacto seleccionado tiene la app corriendo en su móvil y respectivo reloj. Al recibir el pedido del usuario, la app muestra en su reloj "<nombre del usuario> necesita que lo pases a buscar" y un botón para abrir la ubicación en su dispositivo móvil con el correspondiente mapa.
Una vez que el mapa esta abierto, la interfaz del teléfono/tablet mostrará además una interfaz para avisar que se encuentra en camino o que no llegará o algo por el estilo.

Este mensaje será enviado desde el teléfono móvil del usuario 2, al reloj del usuario 1 al que se notifica utilizando alguna de las técnicas de interacción.

![PickMeAppDiagram](https://k60.kn3.net/2/0/B/9/4/6/FEA.png)

##### Screenshots:

Si bien la app se encuentra en desarrollo los avances son los siguientes:

La aplicacion cuenta con un menu inferior con el cual el usuario puede acceder a diferentes secciones de la app.

![ss1](https://k60.kn3.net/1/2/6/6/1/0/EB5.png)

* La sección "Lugares", contendrá un listado de lugares favoritos creados por el usuario utilizando el boton flotante.
* La sección "Contactos", le permitirá al usuario buscar y seleccionar el/los usuarios destinatarios de los mensajes.
* La sección "Mensajes", alojará la bandeja de mensajes recibidos y una sección de configuracion para definir el mensaje a enviar.

*Disclaimer: Esta aplicacion esta desarrollada con fines didácticos por lo que no tendrá ningun uso comercial.*
