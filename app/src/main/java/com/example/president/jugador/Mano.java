package com.example.president.jugador;
import android.widget.ImageView;

import java.util.*;
public class  Mano{
    public ArrayList<Carta> cartas;
    public Jugador jugador;
    //ArrayList <String> cartasAndroid = new ArrayList<String>();

    public Mano(ArrayList<Carta> cartas, Jugador jugador){
        this.cartas = cartas;
        this.ordenarMano();
        this.jugador = jugador;
    }

    public void setImagenes(ArrayList<ImageView> imagenes){
        int numero = 5;
        if(jugador != null){
            numero = jugador.numero;
        }

        int size = Math.min(cartas.size(), imagenes.size());

        for (int k = 0; k < size; k ++){
            cartas.get(k).setImagen(imagenes.get(k), numero);
        }
    }


    public Double getPonderacion(){
        Double ponderacion = 0.0;
        for (Carta carta : cartas) {
            ponderacion += carta.getPonderacion();
        }
        return ponderacion;
    }
    public void verMano(){
        int i = 0;
        if (this.cartas.size() ==  0){
            System.out.println("No hay cartas");
        }else{
            for (Carta carta: this.cartas){
                //cartasAndroid.add(i +") " + carta.getCarta());
                System.out.println(i +") " + carta.getCarta());
                i++;
            }
        }
        System.out.println("");
    }

    /* public  ArrayList <String> getCartasAndroid (){
        return cartasAndroid;
    } */

    /**
     * Ordena la mano en orden descendente. primero la que tiene mas valor.
     */
    public void ordenarMano(){
        Collections.sort(cartas, new Comparator<Carta>() {
            @Override
            public int compare(Carta carta1, Carta carta2) {
                // Ordenar por edad en orden descendente
                return Integer.compare(carta2.getValor(), carta1.getValor());
            }
        });
    }

    public void ordenarManoAscendente(){
        Collections.sort(cartas, new Comparator<Carta>() {
            @Override
            public int compare(Carta carta1, Carta carta2) {
                // Ordenar por edad en orden descendente
                return Integer.compare(carta1.getValor(), carta2.getValor());
            }
        });
    }
}
