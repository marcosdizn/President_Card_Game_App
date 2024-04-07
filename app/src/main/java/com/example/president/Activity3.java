package com.example.president;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorInflater;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.hardware.ConsumerIrManager;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.president.inteligencia.*;
import com.example.president.jugador.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

public class Activity3 extends AppCompatActivity {
    public static Context appContext;
    static Scanner scanner = new Scanner(System.in);
    static int RONDAS_TOTALES;

    private boolean Tirar = false;
    private boolean esTuTurno = false;

    private boolean pass = false;
    static TextView name;
    static TextView numberOfRounds;
    static TextView pasaJugador1;
    static TextView pasaJugador2;
    static TextView pasaJugador3;
    static TextView puestoJugador0;
    static TextView puestoJugador1;
    static TextView puestoJugador2;
    static TextView puestoJugador3;

    static TextView puntuacionUsuarioText, puntuacionPrimeroText, puntuacionSegundoText, puntuacionTerceroText;
    static LinearLayout linearLayout;

    private static ArrayList<View> vistasPulsadas = new ArrayList<>();

    static ArrayList<Jugador> jugadores = new ArrayList<Jugador>();

    /* Las imagenes de cartas son las cartas del jugador 0*/
    static ArrayList<ImageView> imagenesDeCartas = new ArrayList<ImageView>();
    static ArrayList<ImageView> imagenesDeCartas_aux = new ArrayList<ImageView>();
    static ArrayList<ImageView> imagenesCartasCentrales = new ArrayList<ImageView>();

    static ArrayList<ImageView> imagenesDeCartasBot1 = new ArrayList<ImageView>();
    static ArrayList<ImageView> imagenesDeCartasBot2 = new ArrayList<ImageView>();
    static ArrayList<ImageView> imagenesDeCartasBot3 = new ArrayList<ImageView>();

    //Para guardar las imagenes originales y volverlas a poner la siguiente ronda (falta implementarlo)
    static ArrayList<ImageView> imagenesDeCartasBot1_aux = new ArrayList<ImageView>();
    static ArrayList<ImageView> imagenesDeCartasBot2_aux = new ArrayList<ImageView>();
    static ArrayList<ImageView> imagenesDeCartasBot3_aux = new ArrayList<ImageView>();

    private static ArrayList<Carta> cartasEcharJugador = new ArrayList<Carta>();

    static Juego juego;

    static View botontirar;
    static View botonDar;
    static View botonPasar;

    private Handler handler;

    private static Modo modoDeJuego = Modo.Juego;

    static ArrayList<TextView> playerTexts = new ArrayList<TextView>();

    private static Activity3 instance;

    private Thread hiloDeJuego;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resetearVariables();
        appContext = getApplicationContext();
        instance = this;

        setContentView(R.layout.activity_3);

        linearLayout = findViewById(R.id.lay);

        handler = new Handler(Looper.getMainLooper());



        anadirImagenesCartas();

        name = (TextView) findViewById(R.id.userName);
        numberOfRounds = (TextView) findViewById(R.id.numberOfRounds);

        puntuacionUsuarioText= (TextView) findViewById(R.id.puntuacionUsuario);

        puntuacionPrimeroText= (TextView) findViewById(R.id.puntuacionPrimero);
        puntuacionSegundoText= (TextView) findViewById(R.id.puntuacionSegundo);
        puntuacionTerceroText= (TextView) findViewById(R.id.puntuacionTercero);





        String username = getIntent().getStringExtra("keyusername");

        RONDAS_TOTALES = Integer.parseInt(getIntent().getStringExtra("keyrounds"));

        name.setText(username);

        numberOfRounds.setText("Round 1/" +RONDAS_TOTALES);

        puestoJugador0 = (TextView) findViewById(R.id.puestoJugador0);
        puestoJugador1 = (TextView) findViewById(R.id.puestoJugador1);
        puestoJugador2 = (TextView) findViewById(R.id.puestoJugador2);
        puestoJugador3 = (TextView) findViewById(R.id.puestoJugador3);
        puestoJugador0.setText("");
        puestoJugador1.setText("");
        puestoJugador2.setText("");
        puestoJugador3.setText("");

        jugadores = new ArrayList<Jugador>();
        for (int k = 0; k < 4; k++) {
            Jugador jugador = new Jugador(new Mano(new ArrayList<Carta>(), null), Jugador.Role.Nada);
            jugador.setNombre("Jugador " + k);
            jugador.setRole(Jugador.Role.Nada);
            jugador.numero = k;
            jugadores.add(jugador);
        }

        for (int k = 1; k < 4; k++) {
            Resources resources = getResources();
            InputStream inputStream = resources.openRawResource(R.raw.red1);
            IA ia = new IA(jugadores.get(k), k, jugadores, null);
            ia.miRed.recuperarRedString(inputStream);
            jugadores.get(k).setIA(ia);
        }


        jugadores.get(0).setNombre(username);

        View view = findViewById(R.id.button3);
        view.setVisibility(View.INVISIBLE);

        CartasEnJuego cartasEnJuego = new CartasEnJuego();
        botonDar = findViewById(R.id.button3);
        botontirar = findViewById(R.id.button2);
        botonPasar = findViewById(R.id.pass);

        TextView userName = (TextView) findViewById(R.id.userName);
        TextView player1 = (TextView) findViewById(R.id.player1);
        TextView player2 = (TextView) findViewById(R.id.player2);
        TextView player3 = (TextView) findViewById(R.id.player3);

        playerTexts = new ArrayList<TextView>();
        playerTexts.add(userName);
        playerTexts.add(player1);
        playerTexts.add(player2);
        playerTexts.add(player3);

        juego = new Juego(handler,
                jugadores,
                imagenesDeCartas,
                imagenesDeCartasBot1,
                imagenesDeCartasBot2,
                imagenesDeCartasBot3,
                imagenesCartasCentrales,
                cartasEnJuego,
                RONDAS_TOTALES);
        hiloDeJuego = new Thread(juego);
        hiloDeJuego.start();

    }

    public static Activity3 getInstance() {
        return instance;
    }
    private void mostrarDialogo () {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Desea continuar?")
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss(); // Cierra el cuadro de diálogo

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        System.exit(0); // Salimos de la aplicacion
                    }
                });

        // Mostrar el cuadro de diálogo
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public enum  Modo {
        Juego("Juego"),
        DarCartas("Dar cartas");
        private final String stringValue;

        Modo(String stringValue) {
            this.stringValue = stringValue;
        }

        public String getStringValue() {
            return stringValue;
        }
    }

    public void setPass() {
        this.pass = true;
    }
    public void setPassOriginal(boolean pass) {
        this.pass = pass;
    }
    public void setTurno(boolean turno) {
        this.esTuTurno = turno;
    }

    /**
     * Esta función es a la se llama cuando se pulsa el boton tirar
     * */
    public void Tirar(View view) {
        if (this.modoDeJuego == Modo.Juego){
            int tamanoNecesario = 0;
            if (juego.getCartasEnJuego().ultiMano() != null){
                tamanoNecesario = juego.getCartasEnJuego().ultiMano().cartas.size();
            }
            if (
                    juego.getTurno() == 0
                    &&
                    (
                        cartasEcharJugador.size() == tamanoNecesario
                        ||
                        tamanoNecesario == 0
                        ||
                        cartasEcharJugador.get(0).getValor() == 13
                    )
                ){

                juego.setCartasLanzadas(cartasEcharJugador);

                mostrarCartaCentrales(this.cartasEcharJugador);

                quitarCartasMano();

                try {
                    juego.liberar();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                cartasEcharJugador = new ArrayList<Carta>();
            }
        }
    }
    public void pasar(View view) {
        if (juego.getTurno() == 0){
            this.jugadores.get(0).siPasa();
            try {
                juego.liberar();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Pasa");
        }
    }

    private Carta obtenerCartaByIdView(View view){
        Carta carta = new Carta(0,null,null);
        for (Carta carta1: jugadores.get(0).mano.cartas){
            if(carta1.getImagen().getId() == view.getId()){
                // Luego, elimina la imagen del LinearLayout
                carta = carta1;
                break;
            }
        }
        return carta;
    }
    public void seleccionarCarta(View view){
        if(this.modoDeJuego == Modo.Juego){
            this.seleccionarCartasJuego(view);
        } else if (this.modoDeJuego == Modo.DarCartas) {
            this.seleccionarCartasADar(view);
        }
    }


    private void seleccionarCartasJuego(View view){
        if(juego.getTurno() == 0){
            Carta carta = obtenerCartaByIdView(view);
            carta.setImagen((ImageView) view, 0);

            // Comprobamos que la carta que hemos seleccionado esta bien seleccionada
            boolean bienSeleccionada = true;
            int valor = 0;
            int tamano = 0;
            CartasEnJuego cartasEnJuego = juego.getCartasEnJuego();
            if (cartasEnJuego.ultiMano() != null && !cartasEnJuego.ultiMano().cartas.isEmpty()){
                valor = cartasEnJuego.ultiMano().cartas.get(0).getValor();
                tamano = cartasEnJuego.ultiMano().cartas.size();
            }

            int tamano2 = 0;
            for(Carta carta1: jugadores.get(0).mano.cartas ){
                if(carta1.getValor() == carta.getValor()){
                    tamano2++;
                }
            }
            if(
                carta.getValor() == 13
                ||
                (
                    carta.getValor() > valor
                    &&
                    (
                            cartasEcharJugador.size() <= tamano
                            &&
                            tamano2 >= tamano
                    )
                    ||
                    tamano == 0
                )
            ){
                for(Carta carta1: cartasEcharJugador){
                    if(carta1.getValor() != carta.getValor()){
                        bienSeleccionada = false;
                        break;
                    }
                }
            }else{
                bienSeleccionada = false;
            }

            if(bienSeleccionada){
                moverCarta(carta);

            }else{
                agitarCarta(view);
            }
        }
    }

    public void darCartas(View view){
        if(this.modoDeJuego == Modo.DarCartas){
            int cartasADar = 0;

            Jugador.Role roleJugador = juego.getJugadores().get(0).getRole();
            if(roleJugador == Jugador.Role.Presidente){
                cartasADar = 2;

            } else if (roleJugador == Jugador.Role.VicePresidente) {
                cartasADar = 1;
            }

            if(cartasEcharJugador.size() == cartasADar){
                try {
                    List<Carta> copiaLista = new ArrayList<>(cartasEcharJugador);
                    juego.setCartasLanzadas(new ArrayList<>(copiaLista));
                    cartasEcharJugador.clear();
                    juego.liberar();
                    this.modoJuego();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void seleccionarCartasADar(View view){
        Carta carta = obtenerCartaByIdView(view);
        carta.setImagen((ImageView) view, 0);
        int cartasADar = 0;
        Jugador.Role roleJugador = juego.getJugadores().get(0).getRole();
        if(roleJugador == Jugador.Role.Presidente){
            cartasADar = 2;
        } else if (roleJugador == Jugador.Role.VicePresidente) {
            cartasADar = 1;
        }

        if(cartasEcharJugador.size() + 1 <= cartasADar || carta.seleccionada){
            moverCarta(carta);
        }else{
            agitarCarta(view);
        }
    }

    public static void moverCarta(Carta carta){
        ObjectAnimator animator = null;
        if (carta.seleccionada){
            vistasPulsadas.remove(carta.getImagen());
            // Crea un objeto ObjectAnimator para animar la propiedad translationY
            carta.seleccionada = false;
            cartasEcharJugador.remove(carta);
            animator = ObjectAnimator.ofFloat(carta.getImagen(), "translationY", 0f, 0f);
        }else{
            vistasPulsadas.add(carta.getImagen());
            // Crea un objeto ObjectAnimator para animar la propiedad translationY
            animator = ObjectAnimator.ofFloat(carta.getImagen(), "translationY", 0f, -30f);
            carta.seleccionada = true;
            cartasEcharJugador.add(carta);
        }
        animator.setDuration(300);
        animator.start();
    }


    public static void modoDar(){
        botonDar.setVisibility(View.VISIBLE);

        botontirar.setVisibility(View.INVISIBLE);

        botonPasar.setVisibility(View.INVISIBLE);

        modoDeJuego = Modo.DarCartas;
    }

    public void modoJuego(){
        botonDar.setVisibility(View.INVISIBLE);

        View botontirar = findViewById(R.id.button2);
        botontirar.setVisibility(View.VISIBLE);

        View botonPasar = findViewById(R.id.pass);
        botonPasar.setVisibility(View.VISIBLE);

        this.modoDeJuego = Modo.Juego;
    }


    public void agitarCarta(View view){
        // Crea un objeto ObjectAnimator para animar la propiedad translationX
        ObjectAnimator shakeAnimator = ObjectAnimator.ofFloat(view, "translationX", -10f, 10f);
        shakeAnimator.setRepeatMode(ObjectAnimator.REVERSE);
        shakeAnimator.setRepeatCount(5);
        shakeAnimator.setDuration(50);

        // Inicia la animación
        shakeAnimator.start();
    }


    /**
     * Echa las cartas */
    public static void mostrarCartaCentrales(ArrayList<Carta> cartas_lanzadas) {
        int k = 0;
        for (Carta carta : cartas_lanzadas) {
            ImageView imagen = imagenesCartasCentrales.get(k);
            carta.setImagen(imagen,0);
            carta.getImagen().setVisibility(View.VISIBLE);
            k++;
        }
    }

    public  static  void mostrarTodasLasCartas(){
        ArrayList< ArrayList<ImageView> > imagenesCartas = new ArrayList< ArrayList<ImageView> >();
        actualizarImagenesCartasBots();

        imagenesCartas.add(imagenesDeCartas);
        imagenesCartas.add(imagenesDeCartasBot1);
        imagenesCartas.add(imagenesDeCartasBot2);
        imagenesCartas.add(imagenesDeCartasBot3);

        for (ArrayList<ImageView> imagenes : imagenesCartas){
            for (ImageView imagen: imagenes){
                imagen.setVisibility(View.VISIBLE);
            }
        }
    }
    public static void quitarCartasBots(int numeroCartas, int numeroJugador) {
        ArrayList< ArrayList<ImageView> > imagenesCartas = new ArrayList< ArrayList<ImageView> >();
        imagenesCartas.add(imagenesDeCartasBot1);
        imagenesCartas.add(imagenesDeCartasBot2);
        imagenesCartas.add(imagenesDeCartasBot3);

        ArrayList<ImageView> imagenDeCartasBot = imagenesCartas.get(numeroJugador-1);

        for (int i = 0; i < numeroCartas && i < imagenDeCartasBot.size(); i++) {
            // Ocultar la carta
            imagenDeCartasBot.get(i).setVisibility(View.GONE);
            imagenDeCartasBot.remove(i);
        }
    }

    public static void quitarCartasMano() {
        List<View> copiaVistasPulsadas = new ArrayList<>(vistasPulsadas);

        for (View vista : copiaVistasPulsadas) {
            Carta carta = new Carta(1, "Oros", (ImageView) vista);
            carta.seleccionada = true;
            moverCarta(carta);
            vista.setVisibility(View.GONE);
            vista.setVisibility(View.GONE);
        }

        // Limpia la lista después de eliminar las vistas
        vistasPulsadas.clear();
    }

    public static void cambiarRonda(int finalRondas){
        numberOfRounds.setText("Round " +finalRondas+ "/" +RONDAS_TOTALES);
    }

    public static void actualizarPuestos() {
        for (Jugador jugador: jugadores) {
            switch(jugador.getRole()) {
                case Presidente:
                    setTextPuesto("P", jugador.numero);

                    break;
                case VicePresidente:
                    setTextPuesto("VP", jugador.numero);
                    break;
                case ViceComemierda:
                    setTextPuesto("VC", jugador.numero);
                    break;
                case Comemierda:
                    setTextPuesto("C", jugador.numero);
            }
        }
    }

    public static void actualizarPuntuaciones() {

        int puntuacionUsuario= jugadores.get(0).getPuntos();
        String puntuacionUsuarioString = String.valueOf(puntuacionUsuario);
        puntuacionUsuarioText.setText(puntuacionUsuarioString);
        int puntuacionPrimero= jugadores.get(1).getPuntos();
        String puntuacionPrimeroString = String.valueOf(puntuacionPrimero);
        puntuacionPrimeroText.setText(puntuacionPrimeroString);
        int puntuacionSegundo= jugadores.get(2).getPuntos();
        String puntuacionSegundoString = String.valueOf(puntuacionSegundo);
        puntuacionSegundoText.setText(puntuacionSegundoString);
        int puntuacionTercero= jugadores.get(3).getPuntos();
        String puntuacionTerceroString = String.valueOf(puntuacionTercero);
        puntuacionTerceroText.setText(puntuacionTerceroString);
        
    }

    

    private static void setTextPuesto (String puesto, int numeroJugador) {
        switch (numeroJugador) {
            case 0:
                puestoJugador0.setText(puesto);
                if (puesto.equalsIgnoreCase("P") || puesto.equalsIgnoreCase("VP")){
                    puestoJugador0.setTextColor(Color.parseColor("#0000FF"));
                } else {
                    puestoJugador0.setTextColor(Color.parseColor("#8B4513"));
                }
                break;
            case 1:
                puestoJugador1.setText(puesto);
                if (puesto.equalsIgnoreCase("P") || puesto.equalsIgnoreCase("VP")){
                    puestoJugador1.setTextColor(Color.parseColor("#0000FF"));
                } else {
                    puestoJugador1.setTextColor(Color.parseColor("#8B4513"));
                }
                break;
            case 2:
                puestoJugador2.setText(puesto);
                if (puesto.equalsIgnoreCase("P") || puesto.equalsIgnoreCase("VP")){
                    puestoJugador2.setTextColor(Color.parseColor("#0000FF"));
                } else {
                    puestoJugador2.setTextColor(Color.parseColor("#8B4513"));
                }
                break;
            case 3:
                puestoJugador3.setText(puesto);
                if (puesto.equalsIgnoreCase("P") || puesto.equalsIgnoreCase("VP")){
                    puestoJugador3.setTextColor(Color.parseColor("#0000FF"));
                } else {
                    puestoJugador3.setTextColor(Color.parseColor("#8B4513"));
                }
        }
    }

    public static void actualizarImagenesCartasBots(){
        imagenesDeCartasBot1.clear();
        imagenesDeCartasBot1.addAll(imagenesDeCartasBot1_aux);

        imagenesDeCartasBot2.clear();
        imagenesDeCartasBot2.addAll(imagenesDeCartasBot2_aux);

        imagenesDeCartasBot3.clear();
        imagenesDeCartasBot3.addAll(imagenesDeCartasBot3_aux);
    }

    public void anadirImagenesCartas() {
       //Anadimos las cartas a un array de cartas
       imagenesDeCartas.add( (ImageView) findViewById(R.id.cartaJugador0_0));
       imagenesDeCartas.add( (ImageView) findViewById(R.id.cartaJugador0_1));
       imagenesDeCartas.add( (ImageView) findViewById(R.id.cartaJugador0_2));
       imagenesDeCartas.add( (ImageView) findViewById(R.id.cartaJugador0_3));
       imagenesDeCartas.add( (ImageView) findViewById(R.id.cartaJugador0_4));
       imagenesDeCartas.add( (ImageView) findViewById(R.id.cartaJugador0_5));
       imagenesDeCartas.add( (ImageView) findViewById(R.id.cartaJugador0_6));
       imagenesDeCartas.add( (ImageView) findViewById(R.id.cartaJugador0_7));
       imagenesDeCartas.add( (ImageView) findViewById(R.id.cartaJugador0_8));
       imagenesDeCartas.add( (ImageView) findViewById(R.id.cartaJugador0_9));

       imagenesCartasCentrales.add( (ImageView) findViewById(R.id.cartaCentro0));
       imagenesCartasCentrales.add( (ImageView) findViewById(R.id.cartaCentro2));
       imagenesCartasCentrales.add( (ImageView) findViewById(R.id.cartaCentro1));
       imagenesCartasCentrales.add( (ImageView) findViewById(R.id.cartaCentro3));

       //Anadimos las cartas de los bots a los distintos array de cartas
       imagenesDeCartasBot1.add( (ImageView) findViewById(R.id.cartaJugador1_0));
       imagenesDeCartasBot1.add( (ImageView) findViewById(R.id.cartaJugador1_1));
       imagenesDeCartasBot1.add( (ImageView) findViewById(R.id.cartaJugador1_2));
       imagenesDeCartasBot1.add( (ImageView) findViewById(R.id.cartaJugador1_3));
       imagenesDeCartasBot1.add( (ImageView) findViewById(R.id.cartaJugador1_4));
       imagenesDeCartasBot1.add( (ImageView) findViewById(R.id.cartaJugador1_5));
       imagenesDeCartasBot1.add( (ImageView) findViewById(R.id.cartaJugador1_6));
       imagenesDeCartasBot1.add( (ImageView) findViewById(R.id.cartaJugador1_7));
       imagenesDeCartasBot1.add( (ImageView) findViewById(R.id.cartaJugador1_8));
       imagenesDeCartasBot1.add( (ImageView) findViewById(R.id.cartaJugador1_9));

       imagenesDeCartasBot2.add( (ImageView) findViewById(R.id.cartaJugador2_0));
       imagenesDeCartasBot2.add( (ImageView) findViewById(R.id.cartaJugador2_1));
       imagenesDeCartasBot2.add( (ImageView) findViewById(R.id.cartaJugador2_2));
       imagenesDeCartasBot2.add( (ImageView) findViewById(R.id.cartaJugador2_3));
       imagenesDeCartasBot2.add( (ImageView) findViewById(R.id.cartaJugador2_4));
       imagenesDeCartasBot2.add( (ImageView) findViewById(R.id.cartaJugador2_5));
       imagenesDeCartasBot2.add( (ImageView) findViewById(R.id.cartaJugador2_6));
       imagenesDeCartasBot2.add( (ImageView) findViewById(R.id.cartaJugador2_7));
       imagenesDeCartasBot2.add( (ImageView) findViewById(R.id.cartaJugador2_8));
       imagenesDeCartasBot2.add( (ImageView) findViewById(R.id.cartaJugador2_9));

       imagenesDeCartasBot3.add( (ImageView) findViewById(R.id.cartaJugador3_0));
       imagenesDeCartasBot3.add( (ImageView) findViewById(R.id.cartaJugador3_1));
       imagenesDeCartasBot3.add( (ImageView) findViewById(R.id.cartaJugador3_2));
       imagenesDeCartasBot3.add( (ImageView) findViewById(R.id.cartaJugador3_3));
       imagenesDeCartasBot3.add( (ImageView) findViewById(R.id.cartaJugador3_4));
       imagenesDeCartasBot3.add( (ImageView) findViewById(R.id.cartaJugador3_5));
       imagenesDeCartasBot3.add( (ImageView) findViewById(R.id.cartaJugador3_6));
       imagenesDeCartasBot3.add( (ImageView) findViewById(R.id.cartaJugador3_7));
       imagenesDeCartasBot3.add( (ImageView) findViewById(R.id.cartaJugador3_8));
       imagenesDeCartasBot3.add( (ImageView) findViewById(R.id.cartaJugador3_9));

        //Anadimos las cartas de los bots a los distintos array de cartas
        imagenesDeCartasBot1_aux.addAll(imagenesDeCartasBot1);
        imagenesDeCartasBot2_aux.addAll(imagenesDeCartasBot2);
        imagenesDeCartasBot3_aux.addAll(imagenesDeCartasBot3);
        imagenesDeCartas_aux.addAll(imagenesDeCartas);
    }

    /**
     * Esta funcion establece el color dependiendo del turno que se le pase */
    public static void setTurnoVisible(int turno){

        for (int k = 0; k <= 3; k++){
            TextView textPlayer = playerTexts.get(k);
            int colorBlanco = 0xFFFFFFFF;
            int colorRojo = 0xFFFF0000;

            if(k == turno){
                textPlayer.setTextColor(colorRojo);
            }else{
                textPlayer.setTextColor(colorBlanco);
            }
        }
        try {
            juego.liberar();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    public static void ordenarImagenesJugador0(Jugador jugador){
        jugador.mano.ordenarMano();
        int k = 0;
        for (ImageView imageView: imagenesDeCartas){
            jugador.mano.cartas.get(k).setImagen(imageView, 0);
            jugador.mano.cartas.get(k).seleccionada = true;
            moverCarta(jugador.mano.cartas.get(k));
        }

        try {
            juego.liberar();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    public static TextView getName () {
        return name;
    }

    public static Juego getJuego () {
        return juego;
    }

    public void openActivity4() {

        ArrayList<Integer> puntuaciones = new ArrayList<>();

        Collections.sort(jugadores, new Comparator<Jugador>() {
            @Override
            public int compare(Jugador jugador1, Jugador jugador2) {
                // Ordenar por puntuacion en orden descendente
                return Integer.compare(jugador2.getPuntos(), jugador1.getPuntos());
            }
        });

        String Primero = jugadores.get(0).getNombre();
        String Segundo = jugadores.get(1).getNombre();  // Corregido el índice
        String Tercero = jugadores.get(2).getNombre();  // Corregido el índice
        String Cuarto = jugadores.get(3).getNombre();   // Corregido el índice

        Intent intent = new Intent(Activity3.this, Activity4.class);


        int puntuacion1 = jugadores.get(0).getPuntos();
        String puntuacion1String = String.valueOf(puntuacion1);
        int puntuacion2 = jugadores.get(1).getPuntos();
        String puntuacion2String = String.valueOf(puntuacion2);
        int puntuacion3 = jugadores.get(2).getPuntos();
        String puntuacion3String = String.valueOf(puntuacion3);
        int puntuacion4 = jugadores.get(3).getPuntos();
        String puntuacion4String = String.valueOf(puntuacion4);

        System.out.println("Puntuacion jugador: " +jugadores.get(1).getPuntos());

        intent.putExtra("keyname1", Primero);
        intent.putExtra("keyname2", Segundo);
        intent.putExtra("keyname3", Tercero);
        intent.putExtra("keyname4", Cuarto);

        intent.putExtra("keyscore1", puntuacion1String);
        intent.putExtra("keyscore2", puntuacion2String);
        intent.putExtra("keyscore3", puntuacion3String);
        intent.putExtra("keyscore4", puntuacion4String);

        startActivity(intent);
        eliminarActivity();
    }

    @Override
    public void onBackPressed() {
        eliminarActivity();
    }


    public void eliminarActivity(){
        hiloDeJuego.interrupt(); // Esto hace que el hilo del juego termine
        finish();
    }

    private void resetearVariables(){
        this.scanner = new Scanner(System.in);
        this.Tirar = false;
        this.esTuTurno = false;

        this.pass = false;

        this.vistasPulsadas = new ArrayList<>();

        this.jugadores = new ArrayList<Jugador>();

        this.imagenesDeCartas = new ArrayList<ImageView>();
        this.imagenesDeCartas_aux = new ArrayList<ImageView>();
        this.imagenesCartasCentrales = new ArrayList<ImageView>();

        this.imagenesDeCartasBot1 = new ArrayList<ImageView>();
        this.imagenesDeCartasBot2 = new ArrayList<ImageView>();
        this.imagenesDeCartasBot3 = new ArrayList<ImageView>();

        //Para guardar las imagenes originales y volverlas a poner la siguiente ronda (falta implementarlo)
        this.imagenesDeCartasBot1_aux = new ArrayList<ImageView>();
        this.imagenesDeCartasBot2_aux = new ArrayList<ImageView>();
        this.imagenesDeCartasBot3_aux = new ArrayList<ImageView>();

        this.cartasEcharJugador = new ArrayList<Carta>();


        this.modoDeJuego = Modo.Juego;

        this.playerTexts = new ArrayList<TextView>();
    }
}

