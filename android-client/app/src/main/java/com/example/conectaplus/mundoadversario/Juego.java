package com.example.conectaplus.mundoadversario;

import com.example.conectaplus.jugadores.Jugador;

/**
 * @author Lorenzo Mandow
 * Entorno formado por el estado de un juego y dos jugadores cuyos
 * movimientos se alternan por turnos.
 * @version 2017-12-21
 */
public class Juego<E extends EstadoJuego<E>, J1 extends Jugador<E>, J2 extends Jugador<E>> {
    public J1 jug1;
    public J2 jug2;
    E eIni;

    /**
     * Constructor. Recibe los dos jugadores y el estado inicial del juego.
     *
     * @param j1   Jugador 1 (empieza el juego).
     * @param j2   Jugador 2.
     * @param eIni Estado inicial de un juego.
     */

    public Juego(J1 j1, J2 j2, E eIni) {
        this.jug1 = j1;
        this.jug2 = j2;
        this.eIni = eIni;
    }

    /**
     * Controla el desarrollo de la partida hasta que haya terminado.
     *
     * @param eco Permite opcionalmente mostrar por pantalla el desarrollo del juego.
     * @return 1, 0, -1, según gane el primer jugador, haya empate, o gane el segundo jugador, respectivamente.
     */
    public int jugarPartida(boolean eco) {

        E e = this.eIni;
        Jugador<E> jug;

        if (eco) {
            e.ver();
        }

        //Desarrollo de la partida
        while (!(e.ganaActual() || e.ganaOtro() || e.agotado())) {
            jug = e.turno1() ? this.jug1 : this.jug2;     // Le toca a jug.
            e = jug.mueve(e);
            if (eco) {
                e.ver();
            }
        }//while

        //Mostramos el último tablero
        if (eco) {
            System.out.println("\nPOSICIÓN FINAL DEL JUEGO:\n");
            e.ver();
        }

        //Resultado
        if (e.ganaActual()) {
            if (eco) {
                System.out.println("GANA EL JUGADOR ACTUAL");
            }
            return e.turno1() ? 1 : -1;
        } else if (e.ganaOtro()) {
            if (eco) {
                System.out.println("GANA EL OTRO");
            }
            return e.turno1() ? -1 : 1;
        } else { //agotado
            return 0;
        }
    }
}