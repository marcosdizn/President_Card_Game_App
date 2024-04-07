package com.example.president.jugador;
import com.example.president.R;

import java.util.*;
public class  Baraja {
    ArrayList<Carta> cartas = new ArrayList<Carta>();

    public void crearBaraja(){
        String[] palos = {"Oros", "Bastos", "Copas", "Espadas"};
        for (String palo : palos){
            for (int i = 1; i <= 10; i++){
                Carta carta = new Carta(i, palo,null);
                cartas.add(carta);
            }
        }
    }

    public void mezclar(){
        Collections.shuffle(this.cartas);
    }

    public Mano repartir(){
        ArrayList<Carta> cartasMano = new ArrayList<Carta>();
        for (int k = 0; k < 10; k++){
            Carta carta = this.cartas.get(k);
            cartasMano.add(carta);
        }
        for (Carta carta: cartasMano){
            this.cartas.remove(carta);
        }
        Mano mano = new Mano(cartasMano,null);
        return mano;
    }

    public Mano repartirDosDeOros(){
        ArrayList<Carta> cartasMano = new ArrayList<Carta>();
        for (Carta carta: cartas){
            if (carta.getValor() == 13){
                cartasMano.add(carta);
                break;
            }
        }
        this.cartas.removeAll(cartasMano);

        for (int k = 0; k < 9; k++){
            Carta carta = this.cartas.get(k);
            cartasMano.add(carta);
        }

        this.cartas.removeAll(cartasMano);

        Mano mano = new Mano(cartasMano,null);
        return mano;
    }
}


