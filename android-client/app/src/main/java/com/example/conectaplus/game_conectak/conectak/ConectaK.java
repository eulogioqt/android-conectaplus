package com.example.conectaplus.game_conectak.conectak;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.example.conectaplus.game_conectak.mundoadversario.EstadoJuegoAprox;
import com.example.conectaplus.game_conectak.mundosolitario.OverrideHashCode;

/**
 * Juego del conecta-k con dos jugadores que usan fichas diferentes y se alternan en sus movimientos.
 * Esta clase representa los distintos estados en que puede encontrarse el juego del conecta-k.
 *
 * @author José Miguel Horcas Aguilera, Lorenzo Mandow (2010)
 * @author Lorenzo Mandow (2023-10-16)
 */

public class ConectaK extends OverrideHashCode implements EstadoJuegoAprox<ConectaK> {

    private static int ficha1 = 1;    // Ficha del jugador 1 (el que empieza el juego).
    private static int ficha2 = 2;
    private static int vacio = 0;   //espacio sin ficha, valor por defecto de arrays int

    private static char verF1 = 'X';
    private static char verF2 = 'O';
    private static char verVacio = ' ';

    int[][] matriz;                //el tablero de juego
    private int longGanadora;      //longitud ganadora (k)
    private int nFil;
    private int nCol;
    private boolean turno1;        //true si es el turno del primer jugador

    //Los siguientes valores se conservan por motivos de eficiencia:
    private Movimiento ultimoMov;                    // Último movimiento realizado.
    private int it;                                    // Número de movimientos (ply) realizados (iteraciones del juego).

    //Almacen de matrices de posibilidades según el tipo de juego (nFil, nCol, longGanadora)
    // (por si en un mismo programa usamos juegos de distintos tamaños)
    static Map<TipoJuego, MatrizPosibilidades> mp = new HashMap<>();

    ///////////////////////////////////////////////////////////////

    /**
     * Constructor de estado inicial.
     *
     * @param nFil - filas del tablero
     * @param nCol - columnas del tablero
     * @param k    - longitud ganadora
     */

    public ConectaK(int nFil, int nCol, int k) {
        this.matriz = new int[nFil][nCol];
        this.turno1 = true;
        this.nFil = nFil;
        this.nCol = nCol;
        this.longGanadora = k;
        this.ultimoMov = null;
        this.it = 0;
    }

    /**
     * Constructor a partir de los elementos necesarios
     *
     * @param matriz
     * @param turno1
     * @param longGanadora
     * @param mov
     * @param it
     */
    public ConectaK(int[][] matriz, boolean turno1, int nFil, int nCol, int longGanadora,
                    Movimiento mov, int it) {
        this.matriz = matriz;
        this.turno1 = turno1;
        this.nFil = nFil;
        this.nCol = nCol;
        this.longGanadora = longGanadora;
        this.ultimoMov = mov;
        this.it = it;
    }

    ////////////////////////////////////////////////////////////////

    // No es necesario el valor de turno1 EN ESTE CASO para equals y hashCode,
    // pues se puede obtener del número par o impar de fichas en el tablero.
    // No ocurre así en otros juegos, donde sí es necesario incluir turno1 en
    // la definición del estado explícitamente.

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.deepHashCode(matriz);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ConectaK other = (ConectaK) obj;
        if (!Arrays.deepEquals(matriz, other.matriz))
            return false;
        return true;
    }

    ////////////////////////////////////////////////////////////////

    @Override
    public boolean turno1() {
        return this.turno1;
    }

    public int fichaActual() {
        if (this.turno1) {
            return ficha1;
        } else {
            return ficha2;
        }
    }

    ////////////////////////////////////////////////////////////////


    @Override
    public boolean agotado() {
        return it == this.nFil * this.nCol;
    }

    @Override
    public boolean ganaActual() {
        return false;  //el último que movió fue el otro, y es el que podría haber ganado
    }

    @Override
    public boolean ganaOtro() {
        if (ultimoMov != null) {  //si el movimiento anterior enlazó k fichas, entonces ha ganado el otro
            int f = ultimoMov.f();
            int c = ultimoMov.c();
            int n1, n2, n3, n4;
            n1 = conectadas(f, c, 1, 0);
            n2 = conectadas(f, c, 0, 1);
            n3 = conectadas(f, c, -1, 1);
            n4 = conectadas(f, c, 1, 1);
            int max = Math.max(Math.max(Math.max(n1, n2), n3), n4);

            return (longGanadora <= max);
        }
        //todavía no se realizó ningún movimiento
        return false;
    }


    /**
     * @param f  Fila.
     * @param c  Columna.
     * @param df Dirección fila.
     * @param dc Dirección columna.
     * @return Número de fichas iguales conectadas con (f,c) en la línea definida por la dirección df, dc.
     */
    public int conectadas(int f, int c, int df, int dc) {
        int ficha = this.matriz[f][c];

        return contar(f, c, df, dc, ficha) + contar(f, c, -df, -dc, ficha) - 1;
    }

    private int contar(int f, int c, int df, int dc, int ficha) {
        int ac = 0;

        while (posValida(f, c) && matriz[f][c] == ficha) {
            ac++;
            f = f + df;
            c = c + dc;
        }
        return ac;
    }

    /**
     * @param f Fila.
     * @param c Columna.
     * @return Verdadero si (f,c) es una posición del tablero.
     */
    public boolean posValida(int f, int c) {
        return f >= 0 && f < this.nFil && c >= 0 && c < this.nCol;
    }


    //////////////////////////////////////////////////////////////////

    @Override
    public void ver() {

        System.out.println();

        for (int f = 0; f < this.nFil; f++) {
            for (int c = 0; c < this.nCol; c++) {
                if (this.matriz[f][c] == 0) {
                    System.out.print(verVacio + " ");
                } else if (this.matriz[f][c] == 1) {
                    System.out.print(verF1 + " ");
                } else {
                    System.out.print(verF2 + " ");
                }
            }
            System.out.println();
        }
        for (int c = 0; c < this.nCol; c++) {
            System.out.print("==");
        }
        System.out.println();
        //res = res.substring(0, res.length()-1);
        for (int c = 0; c < this.nCol; c++) {
            System.out.print(c % 10 + " ");
        }
        System.out.println();

        //System.out.println("It: " + this.it);
        System.out.println("Último movimiento: " + this.ultimoMov);
        System.out.println("Le toca al jugador: " + (this.turno1 ? "1" : "2") + " (" + (this.turno1 ? verF1 : verF2) + ")");
    }

    //////////////////////////////////////////////////////////////

    @Override
    public List<ConectaK> calculaSucesores() {
        List<ConectaK> h = new LinkedList<ConectaK>();

        for (int i = 0; i < this.nCol; i++) {
            if (columnaLibre(i)) {
                h.add(calcularSucNth(i));
            }
        }
        return h;
    }

    /**
     * @param c Columna.
     * @return Verdadero si es posible soltar una ficha en la columna c.
     * Suponemos que la fila 0 es la superior del tablero.
     */
    public boolean columnaLibre(int c) {
        return 0 <= c &&
                c < this.nCol &&
                this.matriz[0][c] == vacio;
    }

    /**
     * @return una copia del tablero actual (this.matriz)
     */
    public int[][] copiarTablero() {
        int[][] matriz2 = new int[this.nFil][this.nCol];

        // para cada columna, copiamos sólo hasta encontrar un vacío o llegar al final
        // es decir, copiamos sólo las fichas, pues suponemos que el vacío es 0 (valor
        // por defecto de arrays en Java)

        for (int c = 0; c < this.nCol; c++) {
            for (int f = this.nFil - 1; f >= 0 && matriz[f][c] != vacio; f--) {
                matriz2[f][c] = this.matriz[f][c];
            }
        }

        return matriz2;
    }

    /**
     * @param c Columna.
     * @return Estado resultado de mover en la columna c.
     */

    public ConectaK calcularSucNth(int c) {
        int[][] matriz2 = copiarTablero();
        Movimiento mov = soltarFicha(matriz2, c, fichaActual());

        return new ConectaK(matriz2, !turno1, this.nFil, this.nCol, this.longGanadora,
                mov, this.it + 1);
    }

    /**
     * Modifica destructivamente la matriz soltando la ficha en la columna c.
     * Se supone que la fila 0 es la superior del tablero, y la ficha cae desde
     * ahí hacia abajo.
     *
     * @param matr  - representación del tablero donde se soltará la ficha
     * @param c     - columna donde se soltará la ficha
     * @param ficha - ficha a soltar
     * @return - el movimiento realizado
     */
    private Movimiento soltarFicha(int[][] matr, int c, int ficha) {
        int f = 0;

        while (f < matr.length && matr[f][c] == vacio) {
            f++;
        }
        if (f != 0) {
            matr[f - 1][c] = ficha;
            return new Movimiento(f - 1, c);
        } else {
            return null;
        }
    }


    ///////////////////////////////////////////////////////////////////

    /**
     * Devuelve un vector con las características del estado para el turno indicado, que son:
     * <p>
     * 0.- un valor que vale siempre 1
     * 1.- el número de posibilidades del jugador 1,
     * 2.- el número de posibilidades del jugador 2.
     *
     * @return Vector de 3 características
     */
    @Override
    public int[] codifica() {

        // Recuperamos la matriz de posibilidades, creándola y guardándola si no existe
        MatrizPosibilidades posi = ConectaK.mp.get(new TipoJuego(this.nFil, this.nCol, this.longGanadora));
        if (posi == null) {
            posi = new MatrizPosibilidades(this.nFil, this.nCol, this.longGanadora);
            ConectaK.mp.put(new TipoJuego(this.nFil, this.nCol, this.longGanadora), posi);
        }

        // Inicialmente se consideran todas las posibilidades abiertas (0) para ambos jugadores
        // (valor por defecto de los array en Java)
        int n = posi.numPosibilidades();
        int[] posiUno = new int[n];
        int[] posiDos = new int[n];


        // A continuación se marcan aquellas posibilidades cerradas (1) por las fichas del oponente.
        for (int f = 0; f < this.nFil; f++) {
            for (int c = 0; c < this.nCol; c++) {
                for (Integer op : posi.posibilidades(f, c)) {
                    if (matriz[f][c] == ficha2) {
                        posiUno[op] = 1;
                    } else if (matriz[f][c] == ficha1) {
                        posiDos[op] = 1;
                    }
                }
            }
        }
        //Se devuelven los resultados
        int[] resultado = new int[3];
        resultado[0] = 1;              //término independiente para la codificación
        resultado[1] = sumaCeros(posiUno);
        resultado[2] = sumaCeros(posiDos);
        return resultado;

    }

    /**
     * @param a Array.
     * @return Suma de todos los ceros del array
     */
    private int sumaCeros(int[] a) {
        int suma = 0;

        for (int i = 0; i < a.length; i++) {
            if (a[i] == 0) {
                suma++;
            }
        }
        return suma;
    }

    public int[][] getMatriz() {
        return matriz;
    }

    public int siguienteFila(int colNum){
        for(int i=nFil-1; i >= 0; i --){
            if(matriz[i][colNum]== 0){
                return i;
            }
        }


        return -1;
    }

    public ConectaK mueveHumano(int colNum) {
        return calcularSucNth(colNum);
    }

    public Movimiento getUltimoMov() {
        return ultimoMov;
    }
}
