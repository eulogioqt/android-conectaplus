package com.example.conectaplus.conectak;

import jugadores.*;
import mundoadversario.Juego;

public class TestTorneo {

    public static void main(String[] args) {
        int numPartidas = 1000;

        ConectaK conectaK = new ConectaK(3, 3, 3);
        Evaluador<ConectaK> evaluador = new EvaluadorCK();

        System.out.printf("%-35s %8s       %8s       %8s\n", " ", "Gana 1", "Empate", "Gana 2");
        System.out.println("-------------------------------------------------------------------------------");

        mostrarResultados(new JugadorAleatorio<>(), new JugadorAleatorio<>(), conectaK, numPartidas, "Aleatorio vs Aleatorio");
        mostrarResultados(new JugadorAleatorio<>(), new JugadorEvaluar<>(evaluador), conectaK, numPartidas, "Aleatorio vs Evaluar");

        for (int profundidad = 2; profundidad <= 6; profundidad++) {
            mostrarResultados(new JugadorAleatorio<>(), new JugadorAlfaBeta<>(evaluador, profundidad), conectaK, numPartidas, "Aleatorio vs AlfaBeta (p=" + profundidad + ")");
        }
    }

    private static void mostrarResultados(Jugador jugador1, Jugador jugador2, ConectaK conectaK, int numPartidas, String descripcion) {
        Juego juego = new Juego(jugador1, jugador2, conectaK);
        int[] resultados = juego.torneo(numPartidas, false);


        System.out.printf("%-35s %8d       %8d       %8d\n",
                descripcion,
                resultados[0],
                resultados[1],
                resultados[2]);
    }
}
/*
Comportamiento anomalo, cuando la profundidad es par, hay veces que el jugador 1 (aleatorio) gana contra alfa-beta.
Si los dos juegan a la prefeccion, el juego termina en empate.
A profundidad impares, el jugador 1 tiene ventaja, al tener mayor numero de piezas colocadas.
 */
