package com.example.conectaplus.conectak;

import com.example.conectaplus.jugadores.Evaluador;

/**
 * Clase EvaluadorCK que implementa un evaluador heurístico para el problema del Conecta-K.
 * basado en la codificación calculada a partir de una matriz de posibilidades.
 * 
 * La codificación devuelve:
 * 
 * 0 - un valor siempre a 1
 * 1 - número de posibilidades del primer jugador
 * 2 - número de posibilidades del segundo jugador
 * 
 * La evaluación devuelve: número de posibilidades de MAX - número de posibilidades de MIN
 * 
 * El valor miTurno nos indica si MAX es el primer jugador (true) o el segundo (false).
 * 
 *
 * @author L. Mandow (2023-10-16)
 *
 */

public class EvaluadorCK extends Evaluador<ConectaK> {

	@Override
	protected double evaluacion(ConectaK estado, boolean miTurno) {
		
		int[] codigo = estado.codifica();
		if (miTurno) { //MAX es el primer jugador
			return codigo[1] - codigo[2];
		} else {       //MAX es el segundo jugador
			return codigo[2] - codigo[1];
		}
	}

}
