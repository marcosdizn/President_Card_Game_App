package com.example.president.jugador;

import android.view.View;
import android.widget.ImageView;

import com.example.president.R;

public class  Carta {
    private int numero = 0;
    private int valor = 0;
    private String palo;
    private Double ponderacion;

    private ImageView imagen;

    public boolean seleccionada = false;

    public Carta(int numero, String palo,ImageView imagen){
        this.numero = numero;
        this.palo = palo;
        this.setValor();
        this.imagen = imagen;
        if(imagen != null){
            this.assignImages();
        }
    }

    public void setImagen(ImageView imagen,int numero){

        this.imagen = imagen;
        if(numero == 0 ){
            this.assignImages();
        }else{
            this.imagen.setImageResource(R.drawable.r0);
        }
    }

    public ImageView getImagen(){
        return this.imagen;
    }

    public void setPonderacion(Double ponderacion){
        this.ponderacion = ponderacion;
    }

    public Double getPonderacion(){
        return this.ponderacion;
    }

    public String getNumero(){
        String retorno = String.valueOf(numero);
        return retorno;
    }

    public void setValor(){
        if (this.numero == 1){
            this.valor = 11;
        }else if (this.numero == 2){
            if (this.palo.equals("Oros")){
                this.valor = 13;
            }else{
                this.valor = 12;
            }
        }else{
            this.valor = numero;
        }
    }

    public int getValor(){
        if (this.valor == 0 && this.numero != 0){
            this.setValor();
        }
        return this.valor;
    }

    public String getPalo(){
        return this.palo;
    }

    public String getCarta(){
        return (this.getNumero() + " de " + palo);
    }

    private void assignImages () {

        switch(this.getPalo()) {
            case "Oros":
                switch (this.getNumero()){
                    case "1":
                        this.imagen.setImageResource(R.drawable.o1);
                        break;
                    case "2":
                        this.imagen.setImageResource(R.drawable.o2);
                        break;
                    case "3":
                        this.imagen.setImageResource(R.drawable.o3);
                        break;
                    case "4":
                        this.imagen.setImageResource(R.drawable.o4);
                        break;
                    case "5":
                        this.imagen.setImageResource(R.drawable.o5);
                        break;
                    case "6":
                        this.imagen.setImageResource(R.drawable.o6);
                        break;
                    case "7":
                        this.imagen.setImageResource(R.drawable.o7);
                        break;
                    case "8":
                        this.imagen.setImageResource(R.drawable.o8);
                        break;
                    case "9":
                        this.imagen.setImageResource(R.drawable.o9);
                        break;
                    case "10":
                        this.imagen.setImageResource(R.drawable.o10);
                        break;
                }
                break;

            case "Bastos":
                switch (this.getNumero()){
                    case "1":
                        this.imagen.setImageResource(R.drawable.b1);
                        break;
                    case "2":
                        this.imagen.setImageResource(R.drawable.b2);
                        break;
                    case "3":
                        this.imagen.setImageResource(R.drawable.b3);
                        break;
                    case "4":
                        this.imagen.setImageResource(R.drawable.b4);
                        break;
                    case "5":
                        this.imagen.setImageResource(R.drawable.b5);
                        break;
                    case "6":
                        this.imagen.setImageResource(R.drawable.b6);
                        break;
                    case "7":
                        this.imagen.setImageResource(R.drawable.b7);
                        break;
                    case "8":
                        this.imagen.setImageResource(R.drawable.b8);
                        break;
                    case "9":
                        this.imagen.setImageResource(R.drawable.b9);
                        break;
                    case "10":
                        this.imagen.setImageResource(R.drawable.b10);
                        break;
                }
                break;

            case "Copas":
                switch (this.getNumero()){
                    case "1":
                        this.imagen.setImageResource(R.drawable.c1);
                        break;
                    case "2":
                        this.imagen.setImageResource(R.drawable.c2);
                        break;
                    case "3":
                        this.imagen.setImageResource(R.drawable.c3);
                        break;
                    case "4":
                        this.imagen.setImageResource(R.drawable.c4);
                        break;
                    case "5":
                        this.imagen.setImageResource(R.drawable.c5);
                        break;
                    case "6":
                        this.imagen.setImageResource(R.drawable.c6);
                        break;
                    case "7":
                        this.imagen.setImageResource(R.drawable.c7);
                        break;
                    case "8":
                        this.imagen.setImageResource(R.drawable.c8);
                        break;
                    case "9":
                        this.imagen.setImageResource(R.drawable.c9);
                        break;
                    case "10":
                        this.imagen.setImageResource(R.drawable.c10);
                        break;
                }
                break;
            case "Espadas":
                switch (this.getNumero()){
                    case "1":
                        this.imagen.setImageResource(R.drawable.e1);
                        break;
                    case "2":
                        this.imagen.setImageResource(R.drawable.e2);
                        break;
                    case "3":
                        this.imagen.setImageResource(R.drawable.e3);
                        break;
                    case "4":
                        this.imagen.setImageResource(R.drawable.e4);
                        break;
                    case "5":
                        this.imagen.setImageResource(R.drawable.e5);
                        break;
                    case "6":
                        this.imagen.setImageResource(R.drawable.e6);
                        break;
                    case "7":
                        this.imagen.setImageResource(R.drawable.e7);
                        break;
                    case "8":
                        this.imagen.setImageResource(R.drawable.e8);
                        break;
                    case "9":
                        this.imagen.setImageResource(R.drawable.e9);
                        break;
                    case "10":
                        this.imagen.setImageResource(R.drawable.e10);
                        break;
                }
                break;
        }
    }
}
