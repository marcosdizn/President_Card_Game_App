package com.example.president.inteligencia;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;

public class RedNeuronal implements Serializable{
    private FuncionDeActivacion singma = new FuncionDeActivacion(FuncionDeActivacion.TipoFuncion.Sigmoide);
    private FuncionDeActivacion relu = new FuncionDeActivacion(FuncionDeActivacion.TipoFuncion.ReLu);

    public ArrayList<CapaDeNeuronas> rDeNeuronas = new ArrayList<CapaDeNeuronas>();
    private int ID;
    private int generacion = 0;

    public RedNeuronal(int ID){
        this.ID = ID;

        CapaDeNeuronas primeraCapa = new CapaDeNeuronas(57, 57, relu);
        rDeNeuronas.add(primeraCapa);

        for (int k = 1; k <= 2; k++){
            CapaDeNeuronas capaIntermedia = new CapaDeNeuronas(57, 57, singma);
            rDeNeuronas.add(capaIntermedia);
        }


        for (int k = 1; k <= 2; k++){
            CapaDeNeuronas capaIntermedia = new CapaDeNeuronas(57, 57, relu);
            rDeNeuronas.add(capaIntermedia);
        }

        /*  En la ultima capa se tendran que cartas hay que echar y se echaran esas mismas.
         * La última neurona nos sacará el numero de cartas a echar.
         */
        CapaDeNeuronas ultimaCapa = new CapaDeNeuronas(57, 12, singma);
        rDeNeuronas.add(ultimaCapa);
    }

    public int getID(){
        return this.ID;
    }

    /**
     * Esta funcion va a entrenar la red neuronal
     * @param entrada :
     *  0-3 cartas en la mesa
     *  4- 13 -> cartas del jugador 0 en caso de vacio 1-13 el valor de las mismas
     *  14 - 51 -> cartas ya jugadas
     *  52 - 54 -> Nº cartas otros 3 jugadores.
     *  55 -> Nº cartas en juego ejm: 2 doses, 3 cuatros, 1 rey. -> 2,3,1
     *  56 -> Nº de veces que pasan.
     * @return las cartas a la salida 0-9 las cartas que hay que echar, 10-> Nº de cartas a echar.
     */
    public Double[][] predict(Double[][] entrada){

        /***** Forward pass (paso hacia adelante)  */
        Double[][] Yp_prima = new Double[1][entrada[0].length];
        Yp_prima = entrada;


        for (CapaDeNeuronas capa: rDeNeuronas){
            // Suma ponderada Z = (Yp*capa.w_matrix) + capa.w_matrix
            Double[][] Z = sumarMatrices(multiplicarMatrices(Yp_prima, capa.w_matrix),capa.bias);
            Double[][] a = capa.resultadoFuncionDeActivacion(Z);
            Yp_prima = new Double[a.length][a[0].length];
            Yp_prima = a;
        }

        return Yp_prima;
    }

    private static Double[][] multiplicarMatrices(Double[][] a, Double[][] b) {
        // Se comprueba si las matrices se pueden multiplicar
        if (a[0].length != b.length) {
            throw new IllegalArgumentException("Las matrices no son compatibles para la multiplicación");
        }

        Double[][] c = new Double[a.length][b[0].length];


        // se comprueba si las matrices se pueden multiplicar
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < b[0].length; j++) {
                c[i][j] = 0.0;
                for (int k = 0; k < a[0].length; k++) {
                    // aquí se multiplica la matriz
                    c[i][j] += a[i][k] * b[k][j];
                }
            }
        }
        return c;
    }

    public Double[][] sumarMatrices(Double[][] matriz1, Double[][] matriz2){
        if (matriz1 == null || matriz2 == null) {
            throw new IllegalArgumentException("Las matrices no deben ser nulas");
        }

        int filas = matriz1.length;
        int columnas = matriz1[0].length;

        // Es necesario que tengan las mismas filas y columnas
        if (filas != matriz2.length || columnas != matriz2[0].length) {
            throw new IllegalArgumentException("Las dimensiones de las matrices no son compatibles para la suma");
        }

        // Primero hacer la suma
        Double[][] matrizSuma = new Double[matriz1.length][matriz1[0].length];
        for (int y = 0; y < matriz1.length; y++) {
            for (int x = 0; x < matriz1[y].length; x++) {
                Double suma = matriz1[y][x] + matriz2[y][x];
                matrizSuma[y][x] = suma;
            }
        }
        return matrizSuma;
    }

    public void setGeneracion(int generacion){
        this.generacion = generacion;
    }

    public int getGeneracion(){
        return this.generacion;
    }


    public class CapaDeNeuronas implements Serializable{
        private FuncionDeActivacion funcionDeActivacion;
        public Double[][] bias;
        public Double[][] w_matrix;

        public CapaDeNeuronas(int numeroConexiones, int numeroNeuronas, FuncionDeActivacion funcionDeActivacion){
            this.funcionDeActivacion = funcionDeActivacion;

            Random random = new Random();

            bias = new Double[1][numeroNeuronas];
            for (int j = 0; j < numeroNeuronas; j++) {
                bias[0][j] = random.nextDouble()*2 - 1; //Multiplicamos por dos y restamos 1 para que estea entre -1, 1
            }


            w_matrix = new Double[numeroConexiones][numeroNeuronas];
            for (int i = 0; i < numeroConexiones; i++) {
                for (int j = 0; j < numeroNeuronas; j++) {
                    w_matrix[i][j] = random.nextDouble();
                }
            }
        }

        public Double[][] resultadoFuncionDeActivacion(Double[][] x){
            Double[][] resultado = new Double[x.length][x[0].length];

            for (int i = 0; i < x.length; i ++){
                for (int k = 0; k < x[0].length; k ++){
                    resultado[i][k] =  this.funcionDeActivacion.resultado(x[i][k]);
                }
            }
            return resultado;
        }

    }

    private static class FuncionDeActivacion  implements Serializable{
        private TipoFuncion tipoFuncion;
        public FuncionDeActivacion(TipoFuncion tipoFuncion){
            this.tipoFuncion = tipoFuncion;

        }

        public Double resultado(Double x){
            Double resultado = 0.0;
            switch (tipoFuncion) {
                case Sigmoide:
                    resultado = 1.0 / (1.0 + Math.exp(-x));
                    break;
                case ReLu:
                    if (x > 0){
                        resultado = x;
                    }else{
                        resultado = 0.0;
                    }
                    break;

                default:
                    break;
            }

            return resultado;
        }

        public enum  TipoFuncion {
            Sigmoide("Sigmoide"),
            ReLu("ReLu");
            private final String stringValue;

            TipoFuncion(String stringValue) {
                this.stringValue = stringValue;
            }

            public String getStringValue() {
                return stringValue;
            }
        }
    }

    public void guardarRedString(String nombreArchivo){
        try {
            // Crea un objeto FileWriter con el nombre del archivo
            FileWriter fileWriter = new FileWriter(nombreArchivo);

            // Crea un objeto BufferedWriter que envuelve el FileWriter
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            // Escribe la información en el archivo
            escribir(bufferedWriter);

            // Cierra el BufferedWriter para liberar recursos
            bufferedWriter.close();

            System.out.println("Información guardada en " + nombreArchivo);
        } catch (IOException e) {
            // Manejo de excepciones en caso de error de escritura
            e.printStackTrace();
        }
    }

    private void escribir(BufferedWriter bufferedWriter) throws IOException{


        for (CapaDeNeuronas capaDeNeuronas : rDeNeuronas) {
            bufferedWriter.write("Capa\n");
            for (Double[] doubles : capaDeNeuronas.bias) {
                for (Double double1 : doubles) {
                    bufferedWriter.write(double1 + ",");
                }
            }
            bufferedWriter.write("\nw_matrix\n");

            Double w_matrix[][] = capaDeNeuronas.w_matrix;
            for (int i = 0; i < w_matrix.length; i++) {
                for (int j = 0; j < w_matrix[0].length; j++) {
                    bufferedWriter.write(w_matrix[i][j] + ",");
                }
                bufferedWriter.write("\n");
            }
        }
    }

    public void recuperarRedString(String nombreArchivo){
        try {
            // Crea un objeto FileWriter con el nombre del archivo
            // Escribe la información en el archivo
            // Crea un objeto FileWriter con el nombre del archivo
            FileReader fileReader = new FileReader(nombreArchivo);

            // Crea un objeto BufferedWriter que envuelve el FileWriter
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            leer(bufferedReader);

            // Cierra el BufferedWriter para liberar recursos
            System.out.println("Información guardada en " + nombreArchivo);
        } catch (IOException e) {
            // Manejo de excepciones en caso de error de escritura
            e.printStackTrace();
        }
    }

    public void recuperarRedString(InputStream inputStream){
        try {
            // Crea un objeto FileWriter con el nombre del archivo
            // Escribe la información en el archivo
            // Crea un objeto FileWriter con el nombre del archivo

            // Crea un objeto BufferedWriter que envuelve el FileWriter
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            leer(bufferedReader);

            // Cierra el BufferedWriter para liberar recursos
            System.out.println("Información recuperada");

        } catch (IOException e) {
            // Manejo de excepciones en caso de error de escritura
            e.printStackTrace();
        }
    }

    private void leer(BufferedReader bufferedReader) throws IOException{
        String stringLeido = bufferedReader.lines().reduce("", (line1, line2) -> line1 + line2 + "\n");
        stringLeido = stringLeido.replaceFirst("Capa","");
        String capas[] = stringLeido.split("Capa");
        int k = 0;
        for (String capa : capas) {

            String matrices[] = capa.split("w_matrix");

            String biaString[] = matrices[0].split(",");

            Double[][] bias = this.rDeNeuronas.get(k).bias;
            for (int j = 0; j < biaString.length-1; j++) {
                bias[0][j] = Double.parseDouble(biaString[j].replace("0\n" ,""));
            }

            String w_matrixString[] = matrices[1].replaceFirst("\n", "").split("\n");
            Double[][] w_matrix = this.rDeNeuronas.get(k).w_matrix;
            // w_matrix = new Double[w_matrixString.length][w_matrixString[0].split(",").length];

            for (int i = 0; i < w_matrixString.length; i++) {

                String w_matrixDoubles[] = w_matrixString[i].split(",");

                for (int j = 0; j < w_matrixDoubles.length; j++) {
                    w_matrix[i][j] = Double.parseDouble(w_matrixDoubles[j].replace("0\n" ,""));
                }
            }
            k++;
        }
    }
}


