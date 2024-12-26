package com.example.conectaplus.game.conectak;

import java.util.LinkedList;

/**
 * Representa la matriz de posibilidades del tablero para el Conecta-K.
 * Las posibilidades de conectar k fichas en el tablero se numeran sistemáticamente
 * desde 0. Cada posición de la matriz contiene una lista de números, correspondientes
 * a las distintas posibilidades de conectar k fichas en las que participa.
 * 
 * @author José Miguel Horcas Aguilera, Lorenzo Mandow
 * 
 * @version 2013-10-22
 *
 */
public class MatrizPosibilidades {

    private LinkedList<Integer>[][] matriz;
    private int numPosibilidades;
    private int k;
    
    /**
     *  Crea una matriz de posibilidades de nFilas x nColumnas con las posibilidades
     *  de conectar k fichas. Para cada posición de la matriz, las posibilidades se
     *  guardan en una lista. Las posibilidades se numeran a partir de 0.
     */
    public MatrizPosibilidades(int nFilas, int nColumnas, int k) {
        matriz = new LinkedList[nFilas][nColumnas];
        for (int f = 0; f < nFilas; f++) {
            for (int c = 0; c < nColumnas; c++) {
                matriz[f][c] = new LinkedList<Integer>();       
            }
        }
        this.k = k;
        numPosibilidades = crearMatrizPosibilidades(); 
    }
    
    private int crearMatrizPosibilidades() {
        int n = 0;
        for (int f = 0; f < matriz.length; f++) {
            for (int c = 0; c < matriz[0].length; c++) {
                if (marcaPosibilidades(f, c, 0, 1, n)) {    // Horizontal derecha.
                    n++;
                }
                if (marcaPosibilidades(f, c, 1, 0, n)) {    // Vertical arriba.
                    n++;
                }
                if (marcaPosibilidades(f, c, 1, 1, n)) {    // Diagonal derecha ascendente.
                    n++;
                }
                if (marcaPosibilidades(f, c, -1, 1, n)) {   // Diagonal derecha descendente.
                    n++;
                }
            }
        }
        return n;
    }
    
    public int numPosibilidades() {
        return numPosibilidades;
    }
    
    public int longGanadora() {
        return k;
    }
    
    /**
     *  @return una lista de números, correspondientes a las posibilidades de conectar k fichas
     *  en las que participa la posición (f,c).
     */
    public LinkedList<Integer> posibilidades(int f, int c) {
        return matriz[f][c];
    }
    
    /**
     * Si puede, inserta 'clave' en las k casillas desde (f,c) en la dirección df,dc.
     * 
     * @param f         Fila.
     * @param c         Columna.
     * @param df        Dirección de la fila.
     * @param dc        Dirección de la columna.
     * @param clave     Valor a insertar. Un número correspondiente a una posibilidad de conectar k fichas en el tablero.
     * @return          Verdadero si es posible insertar 'clave' en las k casillas; falso en otro caso. 
     */
    private boolean marcaPosibilidades(int f, int c, int df, int dc, int clave) {
        if (posValida(f+(k-1)*df, c+(k-1)*dc)) {
            marcaLinea(f, c, df, dc, k, clave);
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Marca con 'clave' k posiciones de la matriz desde (f,c) en la dirección df,dc.
     * 
     * @param f         Fila.
     * @param c         Columna.
     * @param df        Dirección de la fila.
     * @param dc        Dirección de la columna.
     * @param k         Número de posiciones.
     * @param clave     Valor a marcar.
     */
    private void marcaLinea(int f, int c, int df, int dc, int k, int clave) {
        if (k != 0) {
            matriz[f][c].add(clave);
            marcaLinea(f+df, c+dc, df, dc, k-1, clave);
        }
    }
    
    /**
     * Comprueba si la posición (f,c) de la matriz es válida.
     * 
     * @param f Fila.
     * @param c Columna.
     * @return  Verdadero si la posición (f,c) es válida; falso en otro caso.
     */
    private boolean posValida(int f, int c) {
        return 0 <= f && f < matriz.length && 0 <= c && c < matriz[0].length;
    }
}
