package com.example.president.jugador;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import android.os.Handler;
import android.os.Looper;


import androidx.annotation.IntegerRes;

import java.util.*;

import com.example.president.Activity3;
import com.example.president.Activity4;
import com.example.president.R;
import com.example.president.inteligencia.*;

public class Juego extends Thread implements Runnable{
    private ArrayList<Jugador> jugadores = new ArrayList<Jugador>();
    private ArrayList<Integer> puntuaciones = new ArrayList<Integer>();

    private Activity3 llamarActivity3 = new Activity3();
    private int rondasTotales = 0;
   
    private ArrayList<ImageView> imagenesDeCartas = new ArrayList<ImageView>();

    private ArrayList<View> vistasPulsadas = new ArrayList<>();
   
    private ArrayList<Carta> cartas_lanzadas = new ArrayList<Carta>();

    private ArrayList<ImageView> imagenesCartasCentrales = new ArrayList<ImageView>() ;

    private ArrayList<ImageView> imagenesDeCartasBot1;
    private ArrayList<ImageView> imagenesDeCartasBot2;
    private ArrayList<ImageView> imagenesDeCartasBot3;

    private static Juego juego;

    private  CartasEnJuego cartasEnJuego;

    private final Handler mainHandler;

    private int turno = 0;
    public Juego(Handler mainHandler,
                 ArrayList<Jugador> jugadores,
                 ArrayList<ImageView> imagenesDeCartas,
                 ArrayList<ImageView> imagenesDeCartasBot1,
                 ArrayList<ImageView> imagenesDeCartasBot2,
                 ArrayList<ImageView> imagenesDeCartasBot3,
                 ArrayList<ImageView> imagenesCartasCentrales,
                 CartasEnJuego cartasEnJuego_2,
                 int RONDAS_TOTALES){
        this.imagenesDeCartasBot1 = imagenesDeCartasBot1;
        this.imagenesDeCartasBot2 = imagenesDeCartasBot2;
        this.imagenesDeCartasBot3 = imagenesDeCartasBot3;

        this.mainHandler = mainHandler;
        this.imagenesDeCartas = imagenesDeCartas;
        this.jugadores = jugadores;
        this.cartasEnJuego = cartasEnJuego_2;
        this.imagenesCartasCentrales = imagenesCartasCentrales;

        for (int k = 0; k < 4; k++){
            jugadores.get(k).setRole(Jugador.Role.Nada);
            jugadores.get(k).numero = k;
        }


        this.rondasTotales = RONDAS_TOTALES;
    }

    public synchronized void run(){

       
        int rondas = rondasTotales;
        int rondas_aux = 1;

        Baraja baraja = new Baraja();

        while (rondas > 0) {
            baraja.crearBaraja();
            baraja.mezclar();

            int k = 0;
            for (Jugador jugador: jugadores){
                Mano mano;

                mano = baraja.repartir();

                jugador.mano = mano;
                mano.jugador = jugador;
                switch (k){
                    case 0:
                        mano.setImagenes(imagenesDeCartas);
                        break;
                    case 1:
                        mano.setImagenes(imagenesDeCartasBot1);
                        break;
                    case 2:
                        mano.setImagenes(imagenesDeCartasBot2);
                        break;
                    case 3:
                        mano.setImagenes(imagenesDeCartasBot3);
                        break;
                }
                k++;
            }

            turno = 0;
            boolean fin_partida = false;
            ArrayList<Integer> ordenJugadores = new ArrayList<Integer>();

            if (rondas != rondasTotales){
//            if (true){ //Codigo de pruebas
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Activity3.mostrarTodasLasCartas();
                    }
                });

                ocultarCartas(this.imagenesCartasCentrales);

                Mano comemierda = new Mano(new ArrayList<Carta>(), null);
                Mano viceComemierda = new Mano(new ArrayList<Carta>(), null);
                Mano vicePresidente = new Mano(new ArrayList<Carta>(), null);
                Mano presidente = new Mano(new ArrayList<Carta>(), null);

                //#region Obtener cartas de los jugadores
                for (Jugador jugador: jugadores){
                    if (jugador.getRole() == Jugador.Role.Comemierda){
                        comemierda = jugador.getNCartas(2, true);

                    }else if (jugador.getRole() == Jugador.Role.ViceComemierda){
                        viceComemierda = jugador.getNCartas(1, true);
                    }else if (jugador.numero == 0){
                        mostrarMano(); // bien
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Activity3.modoDar();
                            }
                        });

//                        Esperamos a que echen las cartas
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                        Mano manoADar = new Mano(cartas_lanzadas,this.jugadores.get(0));
                        this.jugadores.get(0).mano.cartas.removeAll(manoADar.cartas);
                        switch (manoADar.cartas.size()) {
                            case 1:
                                vicePresidente = manoADar;
                                break;
                            case 2:
                                presidente = manoADar;
                            default:
                                break;
                        }
                    } else{
                        if (jugador.getRole() == Jugador.Role.VicePresidente){
                            vicePresidente = jugador.getNCartas(1, false);
                        }else if (jugador.getRole() == Jugador.Role.Presidente){
                            presidente = jugador.getNCartas(2, false);
                        }
                    }

                }
                //#endregion Obtener cartas de los jugadores

                //#region Dar cartas a los jugadores
                for (Jugador jugador: jugadores){
                    if (jugador.getRole() == Jugador.Role.Comemierda){
                        jugador.mano.cartas.addAll(presidente.cartas);
                        turno = jugador.numero;
                    }
                    else if (jugador.getRole() == Jugador.Role.ViceComemierda){
                        jugador.mano.cartas.addAll(vicePresidente.cartas);
                    }
                    else if (jugador.getRole() == Jugador.Role.VicePresidente){
                        jugador.mano.cartas.addAll(viceComemierda.cartas);
                    }
                    else if (jugador.getRole() == Jugador.Role.Presidente){
                        jugador.mano.cartas.addAll(comemierda.cartas);
                    }
                }


                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Activity3.ordenarImagenesJugador0(jugadores.get(0));
                    }
                });

                try {
                    wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                int numeroJugador = 0;
                for (Jugador jugador: jugadores){
                    switch (numeroJugador){
                        case 0:
                            jugador.mano.setImagenes(imagenesDeCartas);
                            break;
                        case 1:
                            jugador.mano.setImagenes(imagenesDeCartasBot1);
                            break;
                        case 2:
                            jugador.mano.setImagenes(imagenesDeCartasBot2);
                            break;
                        case 3:
                            jugador.mano.setImagenes(imagenesDeCartasBot3);
                            break;
                    }
                    numeroJugador++;
                }



                //#endregion Dar cartas a los jugadores

            }else if (rondas == rondasTotales){
                for (Jugador jugador : jugadores) {
                    boolean fin = false;
                    for (Carta carta : jugador.mano.cartas){
                        if (carta.getValor() == 3 && carta.getPalo().equals("Oros")){
                            turno = jugador.numero;
                            fin = true;
                            break;
                        }
                    }
                    if (fin){
                        break;
                    }
                }
            }
            mostrarMano(); // bien

            // La partida
            while (!fin_partida) {

                this.cartasEnJuego = new CartasEnJuego();

                ocultarCartas(this.imagenesCartasCentrales);
                int nJugadoresPasan = 0;
                int numeroJugador = 0;
                for (Jugador jugador: jugadores){
                    jugador.noPasa();
                    if (numeroJugador >= 1){
                        jugador.setCartasEnJuego(cartasEnJuego);
                    }
                    numeroJugador++;
                }

                while (true) {

                    // jugador0 nosotros
                    int tamano = 0;
                    if (cartasEnJuego.ultiMano() != null && cartasEnJuego.ultiMano().cartas != null){
                        tamano = cartasEnJuego.ultiMano().cartas.size();
                    }
                    int turno_anterior = turno;
                    if (!jugadores.get(turno).getFinalPartida()){
                        switch (turno) {
                            case 0:
                                Mano uMano = cartasEnJuego.ultiMano();
                                int valor = 0;
                                try {
                                    if (uMano.cartas.size() > 0){
                                        valor = uMano.cartas.get(0).getValor();
                                    }
                                } catch (Exception e) {
                                }

                                if (uMano == null || valor != 13){
                                    mainHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Activity3.setTurnoVisible(0);
                                        }
                                    });

                                    try {
                                        wait();
                                    } catch (InterruptedException e) {
                                        throw new RuntimeException(e);
                                    }

                                    System.out.println("\n");
                                    jugadores.get(0).verMano();
                                    System.out.println("Que carta o cartas quiere lanzar? [ejem: 1,2]");
                                    System.out.println("O pasar [ejem: pass]");
                                    cartasEnJuego.verUltimaMano();
                                    try {
                                        wait();
                                    } catch (InterruptedException e) {
//                                        throw new RuntimeException(e);
                                    }
                                    if (cartas_lanzadas.size() > 0){
                                        menuJugador();
                                    }

                                }else{
                                    System.out.println("Han echado el dos de oros no puedes seguir");
                                    jugadores.get(0).siPasa();
                                }
                                break;

                            default:
                                jugarJugador(cartasEnJuego,jugadores.get(turno),tamano,nJugadoresPasan);
                                break;
                        }

                        if (jugadores.get(turno_anterior).getFinalPartida()){
                            ordenJugadores.add(turno_anterior);
                        }
                    }

                    if (turno + 1 >= 4){
                        turno = 0;
                    }else{
                        turno ++;
                    }

                    if (ordenJugadores.size() == 3){
                        // Fin partida
                        fin_partida = true;

                        int k_role = 1;
                        for (Integer integer : ordenJugadores) {
                            Jugador jugador2 = jugadores.get(integer);
                            switch (k_role) {
                                case 1:
                                    jugador2.setRole(Jugador.Role.Presidente);
                                    break;
                                case 2:
                                    jugador2.setRole(Jugador.Role.VicePresidente);
                                    break;
                                case 3:
                                    jugador2.setRole(Jugador.Role.ViceComemierda);
                                    break;
                                default:
                                    jugador2.setRole(Jugador.Role.Nada);
                                    break;
                            }
                            k_role++;
                        }

                        // Para establecer al comemierda;
                        ArrayList<Integer> faltantes = new ArrayList<Integer>(Arrays.asList(0, 1, 2, 3));
                        faltantes.removeAll(ordenJugadores);
                        int indexComemierda = faltantes.get(0);
                        jugadores.get(indexComemierda).setRole(Jugador.Role.Comemierda);

                        System.out.println("Fin partida");

                        for (Jugador jugador : jugadores) {
                            jugador.verResultadosPartida();
                            
                            puntuaciones.add(jugador.getPuntosJugador(jugador));
                        }
                        break;
                    }

                    // Para saber si todos han pasado ya
                    nJugadoresPasan = 0;
                    for (Jugador jugador: jugadores){
                        if (jugador.getPasa() || jugador.getFinalPartida()){
                            nJugadoresPasan++;
                        }
                    }

                    try {
                        if (nJugadoresPasan >= 3 && cartasEnJuego.ultiMano().cartas.size() > 0){
                            turno = cartasEnJuego.ultiMano().jugador.numero;
                            break;
                        }
                    }catch (NullPointerException e){
                    }
                }
            }
            rondas--;
            rondas_aux ++;
            int finalRondas = rondas_aux;
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    Activity3.cambiarRonda(finalRondas);
                    Activity3.actualizarPuntuaciones();
                    Activity3.actualizarPuestos();
                    Activity3.actualizarImagenesCartasBots();
                }
            });
            
        }
        System.out.println("Fin partida :)");
        Activity3 activity3Instance = Activity3.getInstance();
        if (activity3Instance != null) {
            activity3Instance.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Código que se ejecutará en el hilo de la interfaz de usuario
                    activity3Instance.openActivity4();
                }
            });
        }
    }

    private void mostrarMano (){
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                for (ImageView imagen: imagenesDeCartas) {
                    imagen.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void ocultarCartas (ArrayList<ImageView> imagenes){
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                for (ImageView imagen : imagenes) {
                    imagen.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
    public void jugarJugador(CartasEnJuego cartasEnJuego, Jugador jugador, int tamano, int nJugadoresPasan){
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                Activity3.setTurnoVisible(jugador.numero);
            }
        });

        try {
            wait();
        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
        }


        int valor = 0;
        if (cartasEnJuego.ultiMano() != null && cartasEnJuego.ultiMano().cartas.size()>0){
            valor = cartasEnJuego.ultiMano().cartas.get(0).getValor();
        }
        Mano mano = jugador.echarCarta(tamano,valor);

        String nombreJugador = jugador.getNombre();
        String[] partes = nombreJugador.split(" ");
        int numeroJugador = Integer.parseInt(partes[1]);

        if (mano.cartas.size() == 0){
            jugador.siPasa();
            try {
                Thread.sleep(700);
            } catch (InterruptedException e) {
//                e.printStackTrace();
            }
            
            System.out.println("EL : " +jugador.getNombre()+ " HA PASADO");//Ej: jugador 1
            cartasEnJuego.manos.add(new Mano(new ArrayList<Carta>(), jugador));
            nJugadoresPasan++;
        }else{

            try {
                Thread.sleep(700);
            } catch (InterruptedException e) {
//                e.printStackTrace();
            }

            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    // Llama al método mostrarCarta desde el hilo principal
                    Activity3.mostrarCartaCentrales(mano.cartas);

                    Activity3.quitarCartasBots(mano.cartas.size(), numeroJugador);

                }
            });

            System.out.println("ECHO CARTAS EL : "+jugador.getNombre());

            // anadido
            mano.verMano();
            cartasEnJuego.manos.add(mano);
            jugador.mano.cartas.removeAll(mano.cartas);

            try {
                Thread.sleep(700);
            } catch (InterruptedException e) {
//                e.printStackTrace();
            }
        }
    }

    public synchronized void liberar() throws InterruptedException {
        notify();
    }

    public void menuJugador() {


        Jugador jugador = jugadores.get(0);
        jugador.verMano();
        System.out.println("Que carta o cartas quiere lanzar? [ejem: 1,2]");
        System.out.println("O pasar [ejem: pass]");
        this.cartasEnJuego.verUltimaMano();
        ArrayList<Carta> cartas_lanzadas_copy = new ArrayList<>(cartas_lanzadas);

        Mano mano = new Mano(cartas_lanzadas_copy,jugador);

        jugador.mano.cartas.removeAll(cartas_lanzadas_copy);
        System.out.println("Has echado");
        mano.verMano();
        this.cartasEnJuego.manos.add(mano);
        cartas_lanzadas.clear();
    }

     // Setter
    public void setCartasLanzadas(ArrayList<Carta> cartasLanzadasJugador0) {
        this.cartas_lanzadas = cartasLanzadasJugador0;
    }

    public CartasEnJuego getCartasEnJuego(){
        return this.cartasEnJuego;
    }

    public int getTurno(){
        return this.turno;
    }

    public ArrayList<Jugador> getJugadores(){
        return this.jugadores;
    }

    public ArrayList<Integer> getPuntuaciones(){
        return this.puntuaciones;
    }



}


